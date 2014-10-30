package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

import javax.media.j3d.BoundingSphere;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import static java.lang.Math.pow;

//представляет собой гравитационное поле в пространстве, воздействующее силой притяжения на материальные точки внутри
//области ограничивающей сферы
public class GravityField extends ForceField {

	//константы
	private final static float G = 6.67545E-11f;				  //гравитационная постоянная (м3·с−2·кг−1)

	//поля
	private double mass;										  //масса, создающая гравитацию (кг)

	//конструкторы
	GravityField(double aMass, BoundingSphere aBounds) {
		super(aBounds);
		mass = aMass;
	}

	GravityField(double aMass, Point3d aCenter, BoundingSphere aBounds) {
		this(aMass, aBounds);
		setCenter(aCenter);
	}

	GravityField(double aMass, Point3d aCenter, Point3d boundsCenter, double boundsRadius) {
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
		return gravityForce;
	}
}
