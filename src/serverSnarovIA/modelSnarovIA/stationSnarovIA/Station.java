package serverSnarovIA.modelSnarovIA.stationSnarovIA;

import java.util.*;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.MaterialPoint;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.PhysicalBody;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.Plane;

//представляет собой упрощенную модель симулируемой космической станции, оборудованной двигателями, аккумулятором,солнечными генераторами,
//блоком питания, электролизером, резервуаром для водорода.
public class Station extends PhysicalBody {

	//внутренние классы
	//имена рабочих устройств станции
	private enum WorkingDeviceName {

		LEFT_ENGINE,
		RIGHT_ENGINE,
		TOP_ENGINE,
		BOTTOM_ENGINE,
		FRONT_ENGINE,
		BACK_ENGINE,
		LEFT_SOLAR_PANEL,
		RIGHT_SOLAR_PANEL,
		ELECTROLYZER
	}

	//возможные направления двигателей
	public enum EngineDir {

		left, right, top, bottom, front, back
	}

	//панель приборов станции. Ответственна за расчет параметров и их предоставление. что-то типа прокси-класса для доступа к состоянию внутр обхектов
	public class Panel {

		//поля
		private final Point3d planetCoords;			//координаты планеты, на орбите которой находится станция

		//конструкторы
		public Panel(Point3d aPlanetCoords) {
			planetCoords = aPlanetCoords;
		}

		//поведение
		public double getAltitude(){
			//добавить расчет высоты
			return 0;
		}
		
		public double getSpeed(){
			//добавить расчет скорости
			return 0;
		}
		
		public double getOxygenLevel(){
			return oxygen.getCurrentReserve();
		}
		
		public double getHydrogenLevel(){
			return hydrogen.getCurrentReserve();
		}
		
		public double getBatteryLevel(){
			return battery.getChargeLevel();
		}
	}

	//двигатель создает тягу, способную изменить направление движения станции
	private class Engine implements StationWorkingDevice {
		//Константы

		//поля
		private final EngineDir direction;	//направление выброcа двигателем частиц
		private final Vector3d directionVect;		//вектор направления выброcа двигателем частиц
		private final double maxThrust;				//максимальная тяга двигателя (Н)
		private final Vector3d currentThrustVect = new Vector3d(); //вектор тяги

		private double currentThrust = 0;			//текущая тяга двигателя (Н)
		private double thrustValue;					//цена тяги двигателя (Вт/Н)
		private double workingMassValue;			//расход рабочего тела (кг/Н*с)

		//конструкторы
		private Engine(EngineDir aDirection, double aMaxThrust, double aThrustValue, double aWorkingMassValue) {
			direction = aDirection;
			maxThrust = aMaxThrust;
			thrustValue = aThrustValue;
			workingMassValue = aWorkingMassValue;

			switch (direction) {		//выбор вектора направления двигателя
				case top:
					directionVect = new Vector3d(bodyDirection);
					break;
				case bottom:
					directionVect = new Vector3d(bodyDirection);
					directionVect.negate();
					break;
				case right:
					directionVect = new Vector3d();
					directionVect.cross(bodyDirection, bodyNormal);
					break;
				case left:
					directionVect = new Vector3d();
					directionVect.cross(bodyNormal, bodyDirection);
					break;
				case front:
					directionVect = new Vector3d(bodyNormal);
					break;
				case back:
					directionVect = new Vector3d(bodyNormal);
					directionVect.negate();
					break;
				default:
					directionVect = new Vector3d();
			}
		}

		//методы доступа и модификации
		public EngineDir getDirection() {
			return direction;
		}

		public Vector3d getDirectionVect() {
			return directionVect;
		}

		public double getMaxThrust() {
			return maxThrust;
		}

		public double getCurrentThrust() {
			return currentThrust;
		}

		public Vector3d getCurrentThrustVect() {
			return currentThrustVect;
		}

		public double getThrustValue() {
			return thrustValue;
		}

		public void setCurrentThrust(double aCurrentThrust) {
			if (aCurrentThrust <= maxThrust) {
				currentThrust = aCurrentThrust;
			} else {
				currentThrust = maxThrust;
			}
		}

		//поведение
		@Override
		public void work(long timeMillis) {					//просчитывает работу двигателя за определенное время
			if (currentThrust == 0) {
				return;
			}

			double necessaryWorkingMass = workingMassValue * currentThrust * timeMillis / 1000;	//необходимая масса раб.тела (кг)
			double necessaryEnergy = thrustValue * currentThrust * timeMillis / 3600000; //необходимая энергия (Вт*ч)

			if (battery.uncharge(necessaryEnergy) && hydrogen.retrieveMater(necessaryWorkingMass)) {	//если хватает энергии и раб. тела
				if (currentThrustVect.length() != currentThrust) {
					currentThrustVect.scale(currentThrust, directionVect);
					currentThrustVect.negate();
				}
			} else {
				currentThrustVect.scale(0);
			}
		}
	}

	//представляет собой электролизер, с помощью энергии аккумулятора проводящий электролиз воды и помещающий его продукты в резервуары
	private class Electrolyzer implements StationWorkingDevice {

		//константы
		private final double ENERGY_COST = 2.5811537E7;	 //затраты на электролиз (Дж/кг)
		private final double O_MASS_PART = .888;		//массовая доля кислорода в воде;
		private final double H_MASS_PART = .112;		//массовая доля водорода в воде

		//поля
		private final double maxPower;	//максимальная мощность электролиза
		private final double ECE;			//кпд электролиза
		private double currentPower;		//текущая мощность электролиза

		//конструкторы
		private Electrolyzer(double aMaxPower, double aECE) {
			maxPower = aMaxPower;
			ECE = aECE;
		}

		//методы доступа и модификации
		public double getMaxPower() {
			return maxPower;
		}

		public double getECE() {
			return ECE;
		}

		public double getCurrentPower() {
			return currentPower;
		}

		public void setCurrent(double aCurrentPower) {
			if (aCurrentPower <= currentPower) {
				currentPower = aCurrentPower;
			} else {
				currentPower = maxPower;
			}
		}

		//поведение
		@Override
		public void work(long timeMillis) {
			double electrolisisEnergy = currentPower * timeMillis / 1000;	//энергия, необходимая для этого такта электролиза (Дж)
			double waterMass = electrolisisEnergy / ENERGY_COST;
			if (battery.uncharge(electrolisisEnergy) && water.retrieveMater(waterMass)) {
				double waterDecompositionMass = electrolisisEnergy / ENERGY_COST;
				oxygen.addMater(waterDecompositionMass * O_MASS_PART);
				hydrogen.addMater(H_MASS_PART);
			}
		}
	}

	//солнечная панель для получения электроэнергии от солнца. Представляет прямоугольную панель с приводом для вращения
	private class SolarPanel implements StationWorkingDevice {

		//константы
		public final static double LENGHT = 10;		//длина прямоугольника солнечной панели (м)
		public final static double WIDTH = 3;		//ширина прямоугольника солнечной панели (м)
		//поля
		private final double ECE;			//КПД панели
		private final Plane plane;			//поглащающая плоскость панели
		private final Vector3d axis;		//направляющий вектор оси поворота
		private final double rotateSpeed;	//угловая скорость поворота панели(Рад/с)

		private double currentAngle = 0;	//текущий угол поворота
		private double targetAngle = 0;		//целевой угол поворота

		//конструкторы
		private SolarPanel(double aECE, Plane aPlane, Vector3d aAxis, double aRotateSpeed) {
			ECE = aECE;
			plane = aPlane;
			axis = aAxis;
			rotateSpeed = aRotateSpeed;
		}

		//методы модификации и доступа
		public double getECE() {
			return ECE;
		}

		public Plane getPlane() {
			return plane;
		}

		public Vector3d getAxis() {
			return axis;
		}

		public double getRotateSpeed() {
			return rotateSpeed;
		}

		//поведение
		private void rotate(double angle) {		//начинает вращение панели на заданный угол
			targetAngle += angle;
			targetAngle -= (int) (targetAngle / (2 * Math.PI)) * Math.PI;	// угол лежит на отрезке [-PI; PI]
		}

		@Override
		public void work(long timeMillis) {
			if (currentAngle != targetAngle) {	//если вращение продолжается

				double rotation;
				if (targetAngle > currentAngle) //определяем направление вращения
				{
					rotation = rotateSpeed * (timeMillis / 1000);
				} else {
					rotation = -rotateSpeed * (timeMillis / 1000);
				}

				currentAngle += rotation;
				AxisAngle4d axisAngle = new AxisAngle4d(axis, rotation);
				plane.rotate(axisAngle);
			}

			double collectedEnergy = plane.getRadiantFlux() * ECE * timeMillis / 3600000;	//выделенная из света энергия (Вт*ч)		
			battery.charge(collectedEnergy);
		}
	}

	//представляет собой аккумулятор, который хранит электричество для нужд станции
	private class Battery {

		//поля
		private final double capacity;	//емкость аккумулятора (Вт * ч)
		private double chargeLevel;		//уровень заряда (Вт * ч)

		//конструкторы
		private Battery(double aCapacity) {
			capacity = aCapacity;
		}

		//методы модификации и доступа
		public double getCapacity() {
			return capacity;
		}

		public double getChargeLevel() {
			return chargeLevel;
		}

		//поведение
		public boolean charge(double charge) {	//заряжает аккумулятор на указанную величину. возвращает истина, если заряд "влез"
			if (chargeLevel + charge >= capacity) {
				chargeLevel = capacity;
				return false;
			} else {
				chargeLevel += charge;
				return true;
			}
		}

		public boolean uncharge(double charge) {	//разряжает аккумулятор на указанную величину. вовзращает истина, если хватило заряда
			if (chargeLevel - charge < 0) {
				return false;
			} else {
				chargeLevel -= charge;
				return true;
			}
		}
	}

	private class Reservoir {

		//поля
		private final double capacity;			//максимальное количество сохраняемого в-ва (кг)
		private double currentReserve;			//текущее кол-вол в-ва в резервуаре

		//конструкторы
		private Reservoir(double aCapacity) {
			capacity = aCapacity;
		}

		//методы модификации и доступа
		public double getCapacity() {
			return capacity;
		}

		public double getCurrentReserve() {
			return currentReserve;
		}

		//поведение
		public boolean addMater(double mass) { //добавить определенную массу(кг) хранимого в-ва. Истина, если влезло.
			if (currentReserve + mass > capacity) {
				currentReserve = capacity;
				return false;
			} else {
				currentReserve += mass;
				return true;
			}
		}

		public boolean retrieveMater(double mass) {	//извлечь определенную массу(кг) в-ва. Истина если его хватило.
			if (currentReserve - mass < 0) {
				return false;
			} else {
				currentReserve -= mass;
				return true;
			}
		}
	}

	//константы
	public final static double DIST_TO_PANELS = 8;		//расстояние от центра станции до солнечных панелей

	//дефолтные значения параметров станции (корректировка в дебаге )
	public final static double RADIUS = 18;
	public final static double MASS = 30000;
	public final static double BATTERY_CAPACITY = 100;
	public final static double WATER_CAPACITY = 5000;
	public final static double OXYGEN_CAPACITY = 10;
	public final static double HYDROGEN_CAPACITY = 5;
	public final static double ENGINE_MAX_THRUST = 1000;
	public final static double ENGINE_THRUST_VALUE = 1;
	public final static double ENGINE_WORKING_MASS_VALUE = 1;
	public final static double SOLAR_PANEL_ECE = 0.5;
	public final static double SOLAR_PANEL_ROTATE_SPEED = 1;
	public final static double ELECTROLYZER_MAX_POWER = 1000;
	public final static double ELECTORLYZER_ECE = 0.5;

	//поля
	private final Vector3d bodyDirection = new Vector3d(0, 1, 0);	//направление станции (ориентация верхней части)
	private final Vector3d bodyNormal = new Vector3d(0, 0, 1);		//нормаль к станции (ориентация фронтальной части)

	private final Battery battery;							// аккумулятор
	private final Reservoir water;							// емкость с водой
	private final Reservoir oxygen;							// емкость с кислородом
	private final Reservoir hydrogen;						// емкость с водородом
	
	private final Panel panel;

	private final EnumMap<WorkingDeviceName, StationWorkingDevice> workingDevices; //активные раб. устройства станции

	public Station(
			MaterialPoint center, //собирает станциию с заявленными параметрами и помещает ее в пр-ве
			double batteryCapacity,
			double waterCapacity,
			double oxygenCapacity,
			double hydrogenCapacity,
			double engineMaxThrust,
			double engineThrustValue,
			double engineWorkingMassValue,
			double solarPanelECE,
			double solarPanelRotateSpeed,
			double electrolyzerMaxPower,
			double electrolyzerECE,
			Point3d earthCoords
	) {
		super(center, RADIUS, MASS);

		battery = new Battery(batteryCapacity > 0 ? batteryCapacity : BATTERY_CAPACITY);
		water = new Reservoir(waterCapacity > 0 ? waterCapacity : WATER_CAPACITY);
		oxygen = new Reservoir(oxygenCapacity > 0 ? oxygenCapacity : OXYGEN_CAPACITY);
		hydrogen = new Reservoir(hydrogenCapacity > 0 ? hydrogenCapacity : HYDROGEN_CAPACITY);

		workingDevices = new EnumMap(WorkingDeviceName.class);

		engineMaxThrust = engineMaxThrust > 0 ? engineMaxThrust : ENGINE_MAX_THRUST;
		engineThrustValue = engineThrustValue > 0 ? engineThrustValue : ENGINE_THRUST_VALUE;
		engineWorkingMassValue = engineWorkingMassValue > 0 ? engineWorkingMassValue : ENGINE_WORKING_MASS_VALUE;
		for (int i = 0; i < EngineDir.values().length; i++) {
			workingDevices.put(WorkingDeviceName.values()[i],
					new Engine(EngineDir.values()[i], engineMaxThrust, engineThrustValue, engineWorkingMassValue));
		}

		Vector3d leftSolarPanelAxis = new Vector3d();
		Vector3d rightSolarPanelAxis = new Vector3d();
		leftSolarPanelAxis.cross(bodyNormal, bodyDirection);
		rightSolarPanelAxis.set(leftSolarPanelAxis);
		rightSolarPanelAxis.negate();
		Plane leftSolarPanelPlane = new Plane(
				new Point3d(getCenter().x - DIST_TO_PANELS,
						getCenter().y - SolarPanel.WIDTH,
						getCenter().z),
				new Point3d(getCenter().x - DIST_TO_PANELS,
						getCenter().y + SolarPanel.WIDTH,
						getCenter().z),
				new Point3d(getCenter().x - DIST_TO_PANELS - SolarPanel.LENGHT,
						getCenter().y + SolarPanel.WIDTH,
						getCenter().z)
		);
		Plane rightSolarPanelPlane = new Plane(
				new Point3d(getCenter().x + DIST_TO_PANELS,
						getCenter().y - SolarPanel.WIDTH,
						getCenter().z),
				new Point3d(getCenter().x + DIST_TO_PANELS,
						getCenter().y + SolarPanel.WIDTH,
						getCenter().z),
				new Point3d(getCenter().x + DIST_TO_PANELS + SolarPanel.LENGHT,
						getCenter().y + SolarPanel.WIDTH,
						getCenter().z)
		);
		solarPanelECE = solarPanelECE > 0 ? solarPanelECE : SOLAR_PANEL_ECE;
		solarPanelRotateSpeed = solarPanelRotateSpeed > 0 ? solarPanelRotateSpeed : SOLAR_PANEL_ROTATE_SPEED;
		workingDevices.put(WorkingDeviceName.LEFT_SOLAR_PANEL, new SolarPanel(
				solarPanelECE,
				leftSolarPanelPlane,
				leftSolarPanelAxis,
				solarPanelRotateSpeed));
		workingDevices.put(WorkingDeviceName.RIGHT_SOLAR_PANEL, new SolarPanel(
				solarPanelECE,
				rightSolarPanelPlane,
				leftSolarPanelAxis,
				solarPanelRotateSpeed));
		setPlanes(new ArrayList<Plane>() {
			{
				add(leftSolarPanelPlane);
				add(rightSolarPanelPlane);
			}
		}
		);

		workingDevices.put(WorkingDeviceName.ELECTROLYZER, new Electrolyzer(
				electrolyzerMaxPower > 0 ? electrolyzerMaxPower : ELECTROLYZER_MAX_POWER,
				electrolyzerECE > 0 ? electrolyzerECE : ELECTORLYZER_ECE));
		
		panel = new Panel(earthCoords);
	}

	public Panel getPanel(){
		return panel;
	}
	
	@Override
	public void integrate(long timeMillis) {		//просчитывает изменение состояния станции
		workingDevices.forEach((key, value) -> {
			value.work(timeMillis);
			if (key.compareTo(WorkingDeviceName.BACK_ENGINE) <= 0) //если устройство - двигатель
			{
				getCenter().addForce(((Engine) value).getCurrentThrustVect());	//то склдываем его тягу с силами, действующми на физ.тело
			}
		});

		super.integrate(timeMillis);			//производим расчет состояния станции как физ. тела
	}
}
