package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

import java.util.*;
import java.util.concurrent.*;
import javax.vecmath.Vector3d;

//представляет собой контейнер для физических тел и силовых полей. Обеспечивает их взаимодействие в пр-ве.
//почти потокобезопасный
public class PhysicalUniverse {

	//поля
	private final ConcurrentHashMap<String, PhysicalBody> physBodies = new ConcurrentHashMap<>();		//список всех объектов в пр-ве
	private final ConcurrentHashMap<String, ForceField> forceFields = new ConcurrentHashMap<>();		//список всех полей сил в пр-ве
	private final ConcurrentHashMap<String, Illuminant> lightSources = new ConcurrentHashMap<>();		//список всех источников света в пр-ве
	private final TimerTask integrationTask;
	private volatile long dT;					//квант времени	(мс)
	private volatile double timeFactor;			//фактор времени характеризует скорость течения времени
	private Timer timer;						//таймер, обеспечивающий запуск выполнения операций интегрирования в отдельном потоке	

	//конструкторы
	public PhysicalUniverse(long dT0, double aTimeFactor) {
		dT = dT0;
		timeFactor = aTimeFactor;
		timer = new Timer(true);	//запускает таймер, работающий как демон

		integrationTask = new TimerTask() { //задача для другого потока
			@Override
			public synchronized void run() { //вызывает метод из внешнего класса (просто чтобы писать не здесь, а ниже)
				integrate();
			}
		};
	}
	
	public PhysicalUniverse(long dT0, double aTimeFactor, ConcurrentHashMap<String, PhysicalBody> aPhysBodies,
			ConcurrentHashMap<String, ForceField> aForceFields,
			ConcurrentHashMap<String, Illuminant> aLightSources) {
		this(dT0, aTimeFactor);
		
		if (aPhysBodies != null)
			physBodies.putAll(aPhysBodies);
		
		if (aForceFields != null)
			forceFields.putAll(aForceFields);
		
		if (aLightSources != null)
			lightSources.putAll(aLightSources);
	}

	//методы модификации и доступа
	public long getdT() {
		return dT;
	}
	
	public double getTimeFactor() {
		return timeFactor;
	}
	
	public synchronized ConcurrentHashMap<String, PhysicalBody> getPhysBodies() {
		return physBodies;
	}
	
	public synchronized ConcurrentHashMap<String, ForceField> getForceFields() {
		return forceFields;
	}
	
	public synchronized ConcurrentHashMap<String, Illuminant> getLightSources() {
		return lightSources;
	}
	
	public synchronized void setdT(long dT0) {
		dT = dT0;
		startTime();
	}
	
	public synchronized void setTimeFactor(double aTimeFactor) {
		timeFactor = aTimeFactor;
		startTime();
	}
	
	public synchronized void setPhysBodies(ConcurrentHashMap<String, PhysicalBody> aPhysBodies) {
		physBodies.clear();
		physBodies.putAll(aPhysBodies);
	}
	
	public synchronized void setForceFields(ConcurrentHashMap<String, ForceField> aForceFields) {
		forceFields.clear();
		forceFields.putAll(aForceFields);
	}
	
	public synchronized void setLightSources(ConcurrentHashMap<String, Illuminant> aLightSources) {
		lightSources.clear();
		lightSources.putAll(aLightSources);
	}

	//поведение
	
	public synchronized void addPhysBody(String newPhysBodyName, PhysicalBody newPhysBody){
		physBodies.put(newPhysBodyName, newPhysBody);
	}
	
	public synchronized void addForceField(String newForceFieldName, ForceField newForceField){
		forceFields.put(newForceFieldName, newForceField);
	}
	
	private synchronized void integrate() {				//в этом методе движок расчитывает, как взаимодействуют объекты
		for (PhysicalBody physBody : physBodies.values()) {
			Vector3d resultForce = new Vector3d();
			
			for (ForceField forceField : forceFields.values()) {
				Vector3d anotherForce = forceField.forceForPhysicalBody(physBody);	//узнаем значение каждой силы, пораждаемой каждым полем
				
				if (anotherForce != null)
					resultForce.add(anotherForce);
			}
			
			physBody.getCenter().setForce(resultForce);
			
			for (Plane plane : physBody.getPlanes()) {		//расчитываем световой поток через каждую плоскость
				double resultFlux = 0;
				
				for (Illuminant lightSource : lightSources.values()) {
					resultFlux += lightSource.getRadiantFlux(plane);
				}
				
				plane.setRadiantFlux(resultFlux);
			}
			
			physBody.integrate(dT);			//изменить параметры тела
		}
		notifyAll();
	}
	
	public void startTime() {		//начинает ход времени
		//stopTime();
		timer.scheduleAtFixedRate(integrationTask, 0, (long) (dT / timeFactor));	//немедленный запуск задачи с фиксированной частотой исполнения
	}
	
	public void stopTime() {			//останавливает ход времени
		timer.cancel();
	}
}
