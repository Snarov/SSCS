package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

import java.util.*;

//простейшее представление физического тела: сфера с центром, в котором сосредоточена масса тела
public class PhysicalBody {

	//константы
	public static final double DRAG_COEFFICIENT = 0.47;				//коффициент сопротивления формы для сферы
	//поля
	private final ArrayList<Plane> planes = new ArrayList<>();		//список плоскотей, которые содержит тело
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

	//методы доступа и модификации
	public MaterialPoint getCenter() {
		return center;
	}

	public double getRadius() {
		return radius;
	}

	public ArrayList<Plane> getPlanes() {
		return planes;
	}

	public void setCenter(MaterialPoint aCenter) {
		center.set(aCenter);
	}

	public void setRadius(double aRadius) {
		radius = aRadius;
	}

	public void setPlanes(ArrayList<Plane> aPlanes) {
		planes.clear();
		planes.addAll(aPlanes);
	}

	//поведение
	public void integrate(long timeMillis) {
		center.integrate(timeMillis);			//перенаправление вызова в обволакиваемый объект - центр масс
	}

}
