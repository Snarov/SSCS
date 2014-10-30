package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

//простейшее представление физического тела: сфера с центром, в котором сосредоточена масса тела
public class PhysicalBody {

	//константы
	public static final double DRAG_COEFFICIENT = 0.47;				//коффициент сопротивления формы для сферы
	//поля
	private final MaterialPoint center = new MaterialPoint();		//центр масс тела
	private double radius;											//радиус сферы, имитирующей форму тела

	//конструкторы
	PhysicalBody(double aRadius) {
		radius = aRadius;
	}

	PhysicalBody(MaterialPoint aCenter, double aRadius) {
		this(aRadius);
		center.set(aCenter);
	}

	public MaterialPoint getCenter() {
		return center;
	}

	public double getRadius() {
		return radius;
	}

	public void setCenter(MaterialPoint aCenter) {
		center.set(aCenter);
	}

	public void setRadius(double aRadius) {
		radius = aRadius;
	}

}
