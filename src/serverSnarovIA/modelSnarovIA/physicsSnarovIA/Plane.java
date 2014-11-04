package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import static java.lang.Math.sin;

//представляет собой плоскую поверхность в виде параллелограмма, заданного тремя точками в пространстве (А и С противоположны)
//единственная роль - имитация светового потока через поверхность
public class Plane {

	//поля
	private final Point3d A = new Point3d();
	private final Point3d B = new Point3d();
	private final Point3d C = new Point3d();

	private double radiantFlux;				//величина светового потока через эту плоскость

	//конструкторы
	public Plane(Point3d a, Point3d b, Point3d c) {
		A.set(a);
		B.set(b);
		C.set(c);
	}

	//методы доступа и модификации
	public Point3d getA() {
		return A;
	}

	public Point3d getB() {
		return B;
	}

	public Point3d getC() {
		return C;
	}

	public double getRadiantFlux() {
		return radiantFlux;
	}

	public void setA(Point3d a) {
		A.set(a);
	}

	public void setB(Point3d b) {
		B.set(b);
	}

	public void setC(Point3d c) {
		C.set(c);
	}

	public void setRadiantFlux(double aRadiantFlux) {
		radiantFlux = aRadiantFlux;
	}

	public void set(Plane plane) {
		A.set(plane.A);
		B.set(plane.B);
		C.set(plane.C);
	}

	//поведение
	public Point3d getCenter() {				//находиn точку пересечения диаганалей параллелограмма, задающего поверхность
		Point3d planeCenter = new Point3d();
		planeCenter.add(A, C);
		planeCenter.scale(.5);

		return planeCenter;
	}

	public Vector3d getrNorm() {			//находить вектор нормали к плоскости
		//находим векторы, соответсвующие сторонам параллелограмма
		Vector3d sideVec1 = new Vector3d();
		Vector3d sideVec2 = new Vector3d();
		sideVec1.sub(B, A);
		sideVec2.sub(C, B);

		//находим нормаль к плоскости
		Vector3d norm = new Vector3d();
		norm.cross(sideVec1, sideVec2);

		return norm;
	}

	public double getArea() {			//возвращает площадь параллелограмма
		//находим векторы, соответсвующие сторонам параллелограмма
		Vector3d sideVec1 = new Vector3d();
		Vector3d sideVec2 = new Vector3d();
		sideVec1.sub(B, A);
		sideVec2.sub(C, B);

		double area = sideVec1.length() * sideVec2.length() * sin(sideVec1.angle(sideVec2));

		return area;
	}

}
