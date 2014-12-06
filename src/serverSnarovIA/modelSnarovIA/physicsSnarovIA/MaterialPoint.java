package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

//материальная точка представляет собой точку центра масс тела в трехмерном евклидовом пространстве
//реализует поведение инертной точки в реальном мире
public class MaterialPoint extends Point3d{

	//поля
	private double mass = 1;                                  //масса (кг)
	private Vector3d force = new Vector3d();                 //вектор результирующей силы (Н)
	private Vector3d velocity = new Vector3d();              //вектор скорости (м/c)
	private Vector3d omega = new Vector3d();                 //вектор угловой скорости (вращение вокруг оси, проходящей через центр масс)(рад/c)

	private final Vector3d acceleration = new Vector3d();         //вектор ускорония (косвенное поле, зависит от силы) (м/c^2)

	//конструкторы
	public MaterialPoint() {
		super();
	}

	public MaterialPoint(double x, double y, double z) {
		super(x, y, z);
	}

	public MaterialPoint(Point3d point) {
		super(point);
	}

	public MaterialPoint(float aMass, Vector3d aForce, Vector3d aVelocity, Vector3d aOmega) {
		super();
		mass = aMass;
		force = aForce;
		velocity = aVelocity;
		omega = aOmega;
	}

	public MaterialPoint(double x, double y, double z, float aMass, Vector3d aForce, Vector3d aVelocity, Vector3d aOmega) {
		super(x, y, z);
		mass = aMass;
		force = aForce;
		velocity = aVelocity;
		omega = aOmega;
	}

	public MaterialPoint(Point3d point, float aMass, Vector3d aForce, Vector3d aVelocity, Vector3d aOmega) {
		super(point);
		mass = aMass;
		force = aForce;
		velocity = aVelocity;
		omega = aOmega;
	}

	//методы доступа и модификации
	public double getMass() {
		return mass;
	}

	public void setMass(double aMass) {
		mass = aMass;
	}

	public Vector3d getForce() {
		return force;
	}

	public Vector3d getVelocity() {
		return velocity;
	}

	public Vector3d getOmega() {
		return omega;
	}

	public void setForce(Vector3d aForce) {
		force = aForce;
		acceleration.scale(1 / mass, force);
	}

	public void setVelocity(Vector3d aVelocity) {
		velocity = aVelocity;
	}

	public void setOmega(Vector3d aOmega) {
		omega = aOmega;
	}

	public void set(MaterialPoint mp) {
		super.set(mp);
		mass = mp.mass;
		force = mp.force;
		velocity = mp.velocity;
		omega = mp.omega;
	}

	//поведение
	public void addForce(Vector3d additionalForce) {                        //прибавляет новую силу к текущему вектору силы
		force.add(additionalForce);
		acceleration.scale(1 / mass, additionalForce);                  //и задает вектор ускорения
	}

	public Vector3d integrate(long timeMillis) {     //высчитывает состояние объекта через timeMillis миллисекунд,используя метод интегрирования Эйлера
				//возвращает вектор перемещения мат. точки (как едиснтвенный результат работы методы)

		float timeSeconds = (float)timeMillis / 1000;
		//интегрируем ускорение и находим допольнительную составляющую скорости
		Vector3d additionalVelocity = new Vector3d();           //вектор, на который изменится скорость в середине временного интервала
		additionalVelocity.scale(timeSeconds, acceleration);
		velocity.add(additionalVelocity);                   //скорость в середине временного интервала

		Vector3d offset = new Vector3d();
		offset.scale(timeSeconds, velocity);             //смещение точки за временной интервал
		add(offset);
		return offset;
	}
}
