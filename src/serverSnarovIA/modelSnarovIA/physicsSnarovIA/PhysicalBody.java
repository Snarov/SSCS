package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

import java.io.*;
import java.util.*;
import javax.vecmath.Vector3d;

//простейшее представление физического тела: сфера с центром, в котором сосредоточена масса тела
public class PhysicalBody implements Serializable{

	//константы
	public static final double DRAG_COEFFICIENT = 0.47;				//коффициент сопротивления формы для сферы
	//поля
	private final ArrayList<Plane> planes = new ArrayList<>();		//список плоскотей, которые содержит тело
	private final MaterialPoint center = new MaterialPoint();		//центр масс тела
	private double radius;											//радиус сферы, имитирующей форму тела

	//конструкторы
	public PhysicalBody(double aRadius) {
		radius = aRadius;
	}

	public PhysicalBody(MaterialPoint aCenter, double aRadius, double mass) {
		this(aRadius);
		center.set(aCenter);
		center.setMass(mass);
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
	public void setForce(Vector3d force){		//устанавливает силу, действующую на точку, являющуюся центром масс тела
		center.setForce(force);
	}
	
	public void integrate(long timeMillis) {
		Vector3d bodyOffset = center.integrate(timeMillis);			//перенаправление вызова в обволакиваемый объект - центр масс
		for(Plane plane : planes)
			plane.transfer(bodyOffset);
	}

}
