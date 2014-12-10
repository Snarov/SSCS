package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

//заглушка для класса javax.media.j3d.BoundingSphere (т.к. он не поддерживает сериализацию а исходники либы не нашел)

import javax.vecmath.Point3d;
import java.io.Serializable;

public class BoundingSphere implements Serializable{
	
	//поля
	private Point3d center;
	private double radius;
	//конструкторы
	
	public BoundingSphere(){
		center = new Point3d();
		radius = 0;
	}
	
	public BoundingSphere(Point3d pntd, double d) {
		center = pntd;
		radius = d;
	}
	
	//модификация и доступ

	public void getCenter(Point3d newBoundsSenter) {
		newBoundsSenter.set(center);
	}

	public double getRadius() {
		return radius;
	}
	
	public void set(BoundingSphere newBounds){
		center = newBounds.center;
		radius = newBounds.radius;
	}
	
	public void setCenter(Point3d pntd) {
		center.set(pntd);
	}
	
	public void setRadius(double d) {
		radius = d;
	}
	
	//поведение
	public boolean intersect(Point3d pntd) {		//прибегает к использованию алгоритма из javax.media.j3d.BoundingSphere
		javax.media.j3d.BoundingSphere tmpBS = new javax.media.j3d.BoundingSphere(center, radius);
		return tmpBS.intersect(pntd);
	}
}
