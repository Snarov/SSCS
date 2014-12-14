package clientSnarovIA.viewSnarovIA;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

//атмосфера планеты. Реализуется с помощью градиента с изменением прозрачности
public class Atmosphere extends TransformGroup {

	private static final int SPHERE_DIVISIONS = 64;	//поменьше, чем у планеты, чтобы не кушать ресурсики
	private static final Color3f DEF_AMBIENT_COLOR = new Color3f(0, 0, .1f);
	private static final Color3f DEF_EMISSIVE_COLOR = new Color3f(0, 0, 0);
	private static final Color3f DEF_SPECULAR_COLOR = new Color3f(0, 0, 0);
	private static final float DEF_SHINESS = 70f;

	private final Point3d coords;
	private final float altitude;		//отношение высоты атмосферы к радиусу планеты
	private final int detalisation;		//уровень детализации (количество сплошных слоев)
	private final float transparency;	//уровень прозрачности последнего слоя
	private final Color3f color;

	public Atmosphere(Point3d planetCoords,
			float planetRad,
			float aAltitude,
			int aDetalisation,
			float aTransparency,
			Color3f aColor) {
		coords = new Point3d(planetCoords);
		altitude = aAltitude;
		detalisation = aDetalisation;
		transparency = aTransparency;
		color = aColor;

		ColoringAttributes ca = new ColoringAttributes(color, ColoringAttributes.NICEST);
		PolygonAttributes pa = new PolygonAttributes(PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_FRONT, 0);
		Material material = new Material(DEF_AMBIENT_COLOR, DEF_EMISSIVE_COLOR, color, DEF_SPECULAR_COLOR, DEF_SHINESS);
		for (int layerNum = 0; layerNum < detalisation; layerNum++) {
			Appearance layerApp = new Appearance();
			layerApp.setColoringAttributes(ca);
			layerApp.setPolygonAttributes(pa);
			layerApp.setMaterial(material);
			layerApp.setTransparencyAttributes(new TransparencyAttributes(
					TransparencyAttributes.NICEST,
					transparency));
			addChild(new Sphere(
					planetRad * (1 + (altitude / detalisation) * (layerNum + 1)),
					Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS,
					SPHERE_DIVISIONS,
					layerApp));
		}

		Transform3D transform = new Transform3D();
		transform.setTranslation(new Vector3d(coords));
		setTransform(transform);
	}

	public double getAltitude() {
		return altitude;
	}

	public int getDetalisation() {
		return detalisation;
	}

	public float getTransparency() {
		return transparency;
	}

	public Color3f getColor() {
		return color;
	}

}
