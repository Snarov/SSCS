package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

//представляет собой поле сил, ограниченное сферой


import javax.vecmath.Point3d;
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;
import java.io.*;

public abstract class ForceField implements Serializable{
	
	//поля
	private final Point3d center = new Point3d();				  //координаты источника силы (центр поля)
	private final BoundingSphere bounds = new BoundingSphere();   //зона, в которой действуют силы
	
	//конструкторы
	ForceField(BoundingSphere aBounds){
		bounds.set(aBounds);
	}
	
	ForceField(Point3d aCenter, BoundingSphere aBounds){
		this(aBounds);
		center.set(aCenter);
	}
	
	//методы доступа и модификации
	public Point3d getCenter() {
		return center;
	}

	public BoundingSphere getBounds() {
		return bounds;
	}

	public void setCenter(Point3d aCenter) {
		center.set(aCenter);
	}

	public void setBounds(BoundingSphere aBounds) {
		bounds.set(aBounds);
	}
	
	//поведение
	public void translate(Transform3D translation) {    //перемещение центра поля и центра ограничивающей сферы в пр-ве
		if ((translation.getType() & Transform3D.TRANSLATION) != 0) {
			translation.transform(center);

			Point3d newBoundsCenter = new Point3d();
			bounds.getCenter(newBoundsCenter);
			translation.transform(newBoundsCenter);
			bounds.setCenter(newBoundsCenter);
		}
	}
	
	abstract public Vector3d forceForPhysicalBody(PhysicalBody physBody);
}
