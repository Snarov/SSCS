package clientSnarovIA.viewSnarovIA;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import java.awt.Component;
import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

//Планета вместе с атмосферой
public class Earth extends TransformGroup {
	//примитив
	private static final float EARTH_RAD = 1f;				//радиус земли
	private static final int EARTH_SPHERE_DIVISIONS = 128;	//это вам не полтора полигона
	//текстура
	private static final String EARTH_TEX_NAME = "EarthTex.jpg";
	private static final int TEXTURE_WIDTH = 2500;
	private static final int TEXTURE_HEIGHT = 1250;
	//материал
	private static final Color3f DEF_AMBIENT_COLOR = new Color3f(.1f, .1f, .1f);
	private static final Color3f DEF_EMISSIVE_COLOR = new Color3f(0, 0, 0);
	private static final Color3f DEF_DIFFUSE_COLOR = new Color3f(.6f, .6f, .6f);
	private static final Color3f DEF_SPECULAR_COLOR = new Color3f(1f, 1f, 1f);
	private static final float DEF_SHINESS = 300f;
	private static final double OMEGA = 7.29211E-5;									//скорость вращения земли (рад/с)
	private static final double AXIS_ANGLE = 0.40909263;							//угол наклона оси земли к плоскости орбиты
	private static final Vector3d ROTATION_AXIS = new Vector3d(0, 1, 0);			//вектор оси вращения

	static {
		Matrix3d rotation = new Matrix3d();
		rotation.rotZ(AXIS_ANGLE);
		//превращаем нормальный вектор орбиты в направляющий вектор оси вращения
		rotation.transform(ROTATION_AXIS);
	}

	private double angle;				//угол поворота земли (рад)

	public Earth(String texturePath, Component observer) {
		//установка материала
		Appearance earthApp = new Appearance();
		earthApp.setMaterial(new Material(
				DEF_AMBIENT_COLOR,
				DEF_EMISSIVE_COLOR,
				DEF_DIFFUSE_COLOR,
				DEF_SPECULAR_COLOR,
				DEF_SHINESS
		));

//		чтобы правильно падал свет
		TextureAttributes texAttr = new TextureAttributes();
		texAttr.setTextureMode(TextureAttributes.MODULATE);
		earthApp.setTextureAttributes(texAttr);

		//Загрузка текстуры
		earthApp.setTexture(TextureLoaderSnarovIA.loadTexture(texturePath + "/" + EARTH_TEX_NAME, observer, TEXTURE_WIDTH, TEXTURE_HEIGHT));
		
		//сама форма
		Sphere earth = new Sphere(EARTH_RAD, Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS, EARTH_SPHERE_DIVISIONS, earthApp);

		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		addChild(earth);

//		Appearance atmApp = new Appearance();
//		atmApp.setColoringAttributes(new ColoringAttributes(.0f, .0f, .7f, ColoringAttributes.NICEST));
//		atmApp.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, .3f));
//		Sphere atm = new Sphere(EARTH_RAD * 1.01f, Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS, EARTH_SPHERE_DIVISIONS, atmApp);
		//earth.addChild(atm);
	}

	//вращает землю вокруг своей оси 
	public void rotate(long dT) {
		angle += OMEGA * dT / 1000;
		Transform3D rotation = new Transform3D();
		rotation.setRotation(new AxisAngle4d(ROTATION_AXIS, angle));
		setTransform(rotation);
	}
}
