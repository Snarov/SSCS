package serverSnarovIA.modelSnarovIA.stationSnarovIA;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.PhysicalBody;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.Plane;

//представляет собой упрощенную модель симулируемой космической станции, оборудованной двигателями, аккумулятором,солнечными генераторами,
//блоком питания, электролизером, резервуаром для водорода.
public class Station extends PhysicalBody {

	//внутренние классы
	//возможные направления двигателей
	public enum EngineDir {

		top, bottom, left, right
	}

	//двигатель создает тягу, способную изменить направление движения станции
	private class Engine implements StationWorkingDevice {
		//Константы

		//поля
		private final Station.EngineDir direction;	//направление выброcа двигателем частиц
		private final Vector3d directionVect;		//вектор направления выброcа двигателем частиц
		private final double maxThrust;				//максимальная тяга двигателя (Н)

		private double currentThrust = 0;			//текущая тяга двигателя (Н)
		private double thrustValue;					//цена тяги двигателя (Вт/Н)
		private double workingMassValue;			//расход рабочего тела (кг/Н*с)

		//конструкторы
		private Engine(Station.EngineDir aDirection, double aMaxThrust, double aThrustValue, double aWorkingMassValue) {
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

		public double getThrustValue() {
			return thrustValue;
		}

		public void setCurrentThrust(double aCurrentThrust) {
			if (aCurrentThrust <= maxThrust)
				currentThrust = aCurrentThrust;
			else
				currentThrust = maxThrust;
		}

		//поведение
		@Override
		public void work(double timeMillis) {					//просчитывает работу двигателя за определенное время
			//здесь должно быть что то еще
		}

	}

	//представляет собой электролизер, с помощью энергии аккумулятора проводящий электролиз воды и помещающий его продукты в резервуары
	private class Electrolyzer implements StationWorkingDevice {

		//константы
		private final double ENERGY_COST = 2.5811537E7;	 //затраты на электролиз (Дж/кг)
		private final double O_MASS_PART = .888;		//массовая доля кислорода в воде;
		private final double H_MASS_PART = .112;		//массовая доля водорода в воде

		//поля
		private final double maxCurrent;	//максимальный ток электролиза
		private final double ECE;			//кпд электролиза
		private final double voltage;		//напряжение на электродах
		private double current;				//ток электролиза

		//конструкторы
		private Electrolyzer(double aMaxCurrent, double aECE, double aVoltage) {
			maxCurrent = aMaxCurrent;
			ECE = aECE;
			voltage = aVoltage;

		}

		//методы доступа и модификации
		public double getMaxCurrent() {
			return maxCurrent;
		}

		public double getECE() {
			return ECE;
		}

		public double getVoltage() {
			return voltage;
		}

		public double getCurrent() {
			return current;
		}

		public void setCurrent(double aCurrent) {
			current = aCurrent;
		}

		//поведение
		@Override
		public void work(double timeMillis) {
			//здесь должно быть что то еще
		}

	}

	//солнечная панель для получения электроэнергии от солнца. Представляет прямоугольную панель с приводом для вращения
	private class SolarPanel implements StationWorkingDevice {

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
		public void work(double timeMillis) {
			if (currentAngle != targetAngle) {	//если вращение продолжается
				
				double rotation;
				if (targetAngle > currentAngle)	//определяем направление вращения
					rotation = rotateSpeed * (timeMillis / 1000);
				else
					rotation = -rotateSpeed * (timeMillis / 1000);
				
				AxisAngle4d axisAngle = new AxisAngle4d(axis, rotation);
				plane.rotate(axisAngle);
			}
			
			double collectedEnergy = plane.getRadiantFlux() * ECE;		//!!!ПОРАБОТАТЬ НАД ПОТОКОБЕЗОПАСНОСТЬЮ!!!
			
			//здесь должно быть что то еще
		}
	}

	//представляет собой аккумулятор, который хранит электричество для нужд станции
	private class Battery {

		//поля
		private final double capacity;	//емкость аккумулятора (А * ч)
		private double chargeLevel;		//уровень заряда (А * ч)

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

		public boolean uncharge(double charge) {	//разряжает аккумулятор на указанную величину. вовзращает истинра, если хватило заряда
			if (chargeLevel - charge < 0)
				return false;
			else {
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
			if (currentReserve - mass < 0)
				return false;
			else {
				currentReserve -= mass;
				return true;
			}
		}
	}
	//поля
	private final Vector3d bodyDirection = new Vector3d();	//направление станции (ориентация верхней части)
	private final Vector3d bodyNormal = new Vector3d();		//нормаль к станции (ориентация фронтальной части)

}
