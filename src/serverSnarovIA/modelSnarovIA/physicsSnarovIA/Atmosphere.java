package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

import javax.media.j3d.BoundingSphere;
import javax.vecmath.Vector3d;
import javax.vecmath.Point3d;
import static java.lang.Math.pow;

//представляет собой атмосферу, заполненную газом, находящимся между внутренней в внешней ограничивающими сферами.
//Реагирует силой сопротивления на тела, попавшие в нее
public class Atmosphere extends ForceField {

	//константы
	private static final double R = 8.31447;						//универсальная газовая постоянная
	private static final double EXTERNAL_BOUNDS_RADUIS = 7400000;	//дефолтная внешняя граница атмосферы
	private static final double INTERNAL_BOUNDS_RADUIS = 6400000;	//дефолтная внутреняя граница атмосферы

	//поля
	private final BoundingSphere internalBounds = new BoundingSphere();			//граница внутренней сферы атмосферы,на поверхности лежат точки нулевой высоты

	private double g = 9.81;									//ускоронеие свободного падения (м/с^2)
	private double tempLapseRate = 0.0065;						//скорость падения температуры (К/м)
	private double baseTemperature = 273;						//температура на нулевой высоте (К)
	private double basePressure = 100000;						//давление на нулевой высоте (Па)
	private double molarMass = 0.0289644;						//молярная масса атмосферного газа (кг/моль)

	//конструкторы
	public Atmosphere(BoundingSphere externalBounds, BoundingSphere aInternalBounds) {
		super(externalBounds);
		if (getBounds().getRadius() <= 0)
			getBounds().setRadius(EXTERNAL_BOUNDS_RADUIS);
		if (aInternalBounds.getRadius() <= 0)
			aInternalBounds.setRadius(INTERNAL_BOUNDS_RADUIS);
		internalBounds.set(aInternalBounds);
	}

	public Atmosphere(BoundingSphere externalBounds, BoundingSphere aInternalBounds, double aTempLapseRate,
			double aG, double aBasePressure, double aTemperature, double aMolarMass) {
		this(externalBounds, aInternalBounds);

		if (aTempLapseRate > 0)
			tempLapseRate = aTempLapseRate;
		if (aG > 0)
			g = aG;
		if (aBasePressure > 0)
			basePressure = aBasePressure;
		if (aTemperature > 0)
			baseTemperature = aTemperature;
		if (aMolarMass > 0)
			molarMass = aMolarMass;
	}

	//методы доступа и модификации
	public double getG() {
		return g;
	}

	public double getTempLapseRate() {
		return tempLapseRate;
	}

	public double getBaseTemperature() {
		return baseTemperature;
	}

	public BoundingSphere getInternalBounds() {
		return internalBounds;
	}

	public double getBasePressure() {
		return basePressure;
	}

	public double getMolarMass() {
		return molarMass;
	}

	public void setG(double aG) {
		g = aG;
	}

	public void setTempLapseRate(double aTempLapseRate) {
		tempLapseRate = aTempLapseRate;
	}

	public void setBaseTemperature(double aBaseTemperature) {
		baseTemperature = aBaseTemperature;
	}

	public void setInternalBounds(BoundingSphere aInternalBounds) {
		internalBounds.set(aInternalBounds);
	}

	public void setBasePressure(double aBasePressure) {
		basePressure = aBasePressure;
	}

	public void setMolarMass(double aMolarMass) {
		molarMass = aMolarMass;
	}

	//поведение
	@Override
	public Vector3d forceForPhysicalBody(PhysicalBody physBody) { //просчитывает силу сопротивления газа движению тела в атмосфере
		Vector3d dragForce = null;
		MaterialPoint matPoint = physBody.getCenter();

		if (getBounds().intersect(matPoint) && !internalBounds.intersect(matPoint)) {		//если точка находится в газе
			//находим высоту центра масс тела в атмосфере
			Point3d internalBoundsCenter = new Point3d();
			internalBounds.getCenter(internalBoundsCenter);
			double height = matPoint.distance(internalBoundsCenter) - internalBounds.getRadius();

			//вычисляем температуру на этой высоте
			double temperature = baseTemperature - tempLapseRate * height;
			if (temperature < 0)
				return null;	//сила не действует

			//вычисляем давление атмосферы на этой высоте
			double pressure = basePressure * pow(1 - (tempLapseRate * height) / baseTemperature,
					(g * molarMass) / (R * tempLapseRate));

			//вычисляем плотность на этой высоте
			double density = (pressure * molarMass) / (temperature * R);

			//вычислим модуль силы сопротивления
			double force = PhysicalBody.DRAG_COEFFICIENT * (density * matPoint.getVelocity().lengthSquared() / 2)
					* Math.PI * pow(physBody.getRadius(), 2);

			//получим направление силы, противоположное направлению вектора скорости центра тела
			Vector3d forceDirection = new Vector3d(matPoint.getVelocity());
			forceDirection.negate();
			forceDirection.normalize();

			//Вычислим сам вектор силы
			dragForce = new Vector3d(forceDirection);
			dragForce.scale(force);
		}
		return dragForce;
	}
}
