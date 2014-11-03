package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

import java.util.*;
import java.util.concurrent.*;

//представляет собой контейнер для физических тел и силовых полей. Обеспечивает их взаимодействие в пр-ве.
//почти потокобезопасный
public class PhysicalUniverse {

	//поля
	private volatile double dT;					//квант времени	(мс)
	private volatile double timeFactor;			//фактор времени характеризует скорость течения времени
	private Timer timer;				//таймер, обеспечивающий запуск выполнения операций интегрирования в отдельном потоке	
	private final ConcurrentHashMap<String, PhysicalBody> physBodies = new ConcurrentHashMap<>();		//список всех объектов в пр-ве
	private final ConcurrentHashMap<String, ForceField> forceFields = new ConcurrentHashMap<>();		//список всех полей сил в пр-ве
	
	//конструкторы
	public PhysicalUniverse(double dT0, double aTimeFactor){		
		dT = dT0;
		timeFactor = aTimeFactor;
		timer = new Timer(true);	//запускает таймер, работающий как демон
	}
	
	public PhysicalUniverse(double dT0, double aTimeFactor, ConcurrentHashMap<String, PhysicalBody> aPhysBodies
														  , ConcurrentHashMap<String, ForceField> aForceFields){	
		this(dT0, aTimeFactor);
		
		if(aPhysBodies != null)
			physBodies.putAll(aPhysBodies);
		
		if(aForceFields != null)
			forceFields.putAll(aForceFields);
	}
				
	//методы модификации и доступа

	public double getdT() {
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

	public synchronized void setdT(double dT0) {
		dT = dT0;
		restart();
	}

	public synchronized void setTimeFactor(double aTimeFactor) {
		timeFactor = aTimeFactor;
		restart();
	}

	public synchronized void setPhysBodies(ConcurrentHashMap<String, PhysicalBody> aPhysBodies) {
		physBodies.clear();
		physBodies.putAll(aPhysBodies);
	}

	public synchronized void setForceFields(ConcurrentHashMap<String, ForceField> aForceFields) {
		forceFields.clear();
		forceFields.putAll(aForceFields);
	}
	
	//поведение
	private void restart()			//перезапускает таймер, опираясь на новый квант времени и временной фактор
	{
		timer.cancel();
		//... еще что-то должно быть здесь
	}
	
	private void run(){				//в этом методе движок расчитывает как взаимодействуют объекты
		
	}
	
	public void startTime(){		//начинает ход времени
		//... еще что-то должно быть здесь
	}
	
	public void stopTime(){			//останавливает ход времени
		timer.cancel();
	}
}
