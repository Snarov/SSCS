package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.io.*;
import static java.lang.Math.*;

//представляет собой точечный источник света с заданной силой света,
//свечение которого распространяется внутри ограничивающей сферы
public class Illuminant implements Serializable {

	//константы
	private final static double Km = 683;			//максимальное значение спектральной световой эффективности монохроматического излучения(лм/Вт)
	public final static long AU = 149597870700L;	//астрономичская единица (м)
	public final static long BOUNDS_RADIUS = 200000000000L;

	//поля
	private final Point3d source = new Point3d();					//местоположение источника света в пр-ве
	private final BoundingSphere bounds = new BoundingSphere();		//сфера, ограничивающее распространение света
	private double intensity = 3E27;								//сила света источника (кд)
	private double luminousEfficacy = 97;							//световая эффективность источника света (лм/Вт)

	//конструкторы
	public Illuminant(double aIntensity, double aLuminousEfficacy, BoundingSphere aBounds) {
		if (aIntensity > 0) {
			intensity = aIntensity;
		}
		if (aLuminousEfficacy > 0) {
			setLuminousEfficacy(aLuminousEfficacy);
		}
		bounds.set(aBounds);
	}

	public Illuminant(double aIntensity, double aLuminousEfficacy, Point3d boundsCenter, double boundsRadius) {
		this(aIntensity, aLuminousEfficacy, new BoundingSphere(boundsCenter, boundsRadius));
	}

	public Illuminant(double aIntensity, double aLuminousEfficacy, BoundingSphere aBounds, Point3d aCoords) {
		this(aIntensity, aLuminousEfficacy, aBounds);
		source.set(aCoords);
	}

	public Illuminant(double aIntensity, double aLuminousEfficacy, Point3d boundsCenter,
			double boundsRadius, Point3d aCoords) {
		this(aIntensity, aLuminousEfficacy, new BoundingSphere(boundsCenter, boundsRadius), aCoords);
	}

	//методы доступа и модификации
	public Point3d getCoords() {
		return source;
	}

	public BoundingSphere getBounds() {
		return bounds;
	}

	public double getIntensity() {
		return intensity;
	}

	public double getLuminousEfficacy() {
		return luminousEfficacy;
	}

	public void setCoords(Point3d aSource) {
		source.set(aSource);
	}

	public void setBounds(BoundingSphere aBounds) {
		bounds.set(aBounds);
	}

	public void setIntensity(double aIntensity) {
		intensity = aIntensity;
	}

	public final void setLuminousEfficacy(double aLuminousEfficacy) {	 //если световая эффективность больше,
		//чем максимально возможная, то устанавливается максимально возможная
		if (aLuminousEfficacy > Km) {
			luminousEfficacy = Km;
		} else {
			luminousEfficacy = aLuminousEfficacy;
		}
	}

	//поведение
	public double getRadiantFlux(Plane plane) {	//расчитывает поток излучения через поверхность в виде параллелограмма,
		//заданного тремя вершинами (А и C противоположны), от этого источника света (Вт)

		double radiantFlux = 0;
		//находим точку пересечения диаганалей параллелограмма, задающего поверхность
		Point3d planeCenter = plane.getCenter();

		if (bounds.intersect(planeCenter)) {								//если плоскость в зоне освещения
			//находим нормаль к плоскости
			Vector3d norm = plane.getNorm();

			//находим вектор луча света
			Vector3d ray = new Vector3d();
			ray.sub(planeCenter, source);

			//находим угол между нормалью и направлением луча (рад)
			double angle = norm.angle(ray);

			//находим значение освещенности	(лк)
			double illuminance = (intensity / ray.lengthSquared()) * abs(cos(angle));

			//находим площадь параллелограмма, пользуясь псевдоскалярным произведением (м^2)
			double area = plane.getArea();

			//находим  поток излучения (Вт)
			double luminousFlux = (illuminance * area) / luminousEfficacy;
		}

		return radiantFlux;
	}
}
