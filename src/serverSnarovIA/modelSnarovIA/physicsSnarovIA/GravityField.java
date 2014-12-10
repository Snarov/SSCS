package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import static java.lang.Math.pow;

//представляет собой гравитационное поле в пространстве, воздействующее силой притяжения на материальные точки внутри
//области ограничивающей сферы
public class GravityField extends ForceField {

	//константы
	private final static double GRAVITY_FIELD_RADIUS = 30000000;	  //радиус поля по умолчанию (м)
	private final static double G = 6.67545E-11f;					//гравитационная постоянная (м3·с−2·кг−1)

	//поля
	private double mass = 5.972E24;										  //масса, создающая гравитацию (кг)

	//конструкторы
	public GravityField(double aMass, BoundingSphere aBounds) {
		super(aBounds);

		if (getBounds().getRadius() <= 0)
			getBounds().setRadius(GRAVITY_FIELD_RADIUS);
		if (aMass > 0)

		mass = aMass;
	}

	public GravityField(double aMass, Point3d aCenter, BoundingSphere aBounds) {
		this(aMass, aBounds);
		setCenter(aCenter);
	}

	public GravityField(double aMass, Point3d aCenter, Point3d boundsCenter, double boundsRadius) {
		this(aMass, aCenter, new BoundingSphere(boundsCenter, boundsRadius));
	}

	//методы доступа и модификации
	public double getMass() {
		return mass;
	}

	public void setMass(double aMass) {
		mass = aMass;
	}

	//поведение
	@Override
	public Vector3d forceForPhysicalBody(PhysicalBody physBody) {	//рассчитывает силу притяжения (Н) для указанной точки
		Vector3d gravityForce = null;
		MaterialPoint matPoint = physBody.getCenter();
		//рассматриваем только центр масс тела
		if (getBounds().intersect(matPoint)) {						//если точка находится в области действия гравитации
			gravityForce = new Vector3d();

			//расстояние от источника гравитации до мат. точки (м)
			double distance = getCenter().distance(matPoint);

			if (distance > 0) {
				//находим направление действия силы
				Vector3d gravityForceDirection = new Vector3d();
				gravityForceDirection.sub(getCenter(), matPoint);
				gravityForceDirection.normalize();

				//модуль вектора силы
				double force = G * mass * matPoint.getMass() / pow(distance, 2);

				//сама сила
				gravityForce.set(gravityForceDirection);
				gravityForce.scale(force);
			}
		}
		return gravityForce;
	}
}
