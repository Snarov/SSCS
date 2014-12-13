package clientSnarovIA.viewSnarovIA;
	//!!!ИСПРАВИТЬ ГОВНОКОД, ВЫСРАНЫЙ МНОЙ НОЧЬЮ В СУББОТУ!!!!
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PointLight;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.*;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import serverSnarovIA.ViewInitData;

//главный компонент представления, содержащий холст с виртуальной вселенной и панель управления станцией
public class View extends JComponent {

	//константы
	//показывает, во сколько раз отличается единица длина модели от единицы длины представления //(модель - м; представление - км)
	private static final int DIST_FACTOR = 1000;
	private static final float EARTH_RAD = 1f;			//радиус земли
	private static final int EARTH_SPHERE_DIVISIONS = 128;
	private static final Color3f DEF_AMBIENT_COLOR = new Color3f(.1f, .1f, .1f);
	private static final Color3f DEF_EMISSIVE_COLOR = new Color3f(0, 0, 0);
	private static final Color3f DEF_DIFFUSE_COLOR = new Color3f(.6f, .6f, .6f);
	private static final Color3f DEF_SPECULAR_COLOR = new Color3f(0, 0, 0);
	private static final float DEF_SHINESS = 300f;
	private static final int TEXTURE_WIDTH = 2500;
	private static final int TEXTURE_HEIGHT = 1250;
	private static final int AU = 15;
	private static final String EARTH_TEX_NAME = "EarthTex.jpg";
	private static final String SUN_TEX_NAME = "SunTex.jpg";
	private static final float DFI = .01f;
	private static final int DT = 15;
	
	//поля
	private SimpleUniverse universe;	//представление виртуальной вселенной
	private Canvas3D canvas;			//холст с представлением
	private float fi;

	//конструкторы
	public View(ViewInitData initInfo, String texturePath) {
		setLayout(new BorderLayout());

		//настройка холста
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(config);
		universe = new SimpleUniverse(canvas);
		add(BorderLayout.CENTER, canvas);

		//создание графа сцены
		BranchGroup sceneRoot = new BranchGroup();

		//создание планеты c атмосферой
		sceneRoot.addChild(configureEarth(texturePath));
		
		//создание звезды
		sceneRoot.addChild(configureSun(texturePath));
		//настройка освещения
		Color3f sunLightColor = new Color3f(1, 1, 1);
		Point3f sunCoords = new Point3f(AU, 0, 0);
		PointLight sunLight = new PointLight(sunLightColor, sunCoords, new Point3f(0.5f, 0, 0));
		Bounds influenceRegion = new BoundingSphere(new Point3d(sunCoords.x, sunCoords.y, sunCoords.z) , 1.2 * AU);

		sunLight.setInfluencingBounds(influenceRegion);

		sceneRoot.addChild(sunLight);

		//установка камеры
		ViewingPlatform vp = universe.getViewingPlatform();
		TransformGroup vptg = vp.getMultiTransformGroup().getTransformGroup(0);

		Transform3D vpTranslation = new Transform3D();
		Vector3d translationVector = new Vector3d(0, 0, 3);

		vpTranslation.setTranslation(translationVector);
		vptg.setTransform(vpTranslation);

		//добавление сцены на холст
		sceneRoot.compile();
		universe.addBranchGraph(sceneRoot);

		//добавление поведения
		OrbitBehavior ob = new OrbitBehavior(canvas);
		ob.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE));
		universe.getViewingPlatform().setViewPlatformBehavior(ob);
		
		

	}

	private Node configureEarth(String texturePath) {		//создает представление планеты Земля
		//установка материала
		Appearance earthApp = new Appearance();
		earthApp.setMaterial(new Material(
				DEF_AMBIENT_COLOR,
				DEF_EMISSIVE_COLOR,
				DEF_DIFFUSE_COLOR,
				DEF_SPECULAR_COLOR,
				DEF_SHINESS
		));
		
		//чтобы правильно падал свет
		TextureAttributes texAttr = new TextureAttributes();
		texAttr.setTextureMode(TextureAttributes.MODULATE);
		earthApp.setTextureAttributes(texAttr);

		//Загрузка текстуры
		TextureLoader textureLoader = new TextureLoader(texturePath + "/" + EARTH_TEX_NAME, canvas);
		Texture2D texture = new Texture2D(
				Texture2D.BASE_LEVEL,
				Texture2D.RGB,
				TEXTURE_WIDTH,
				TEXTURE_HEIGHT);
		texture.setImage(0, textureLoader.getImage());
		
		earthApp.setTexture(texture);

		Sphere earth = new Sphere(EARTH_RAD, Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS, EARTH_SPHERE_DIVISIONS, earthApp);
		TransformGroup tg = new TransformGroup();
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg.addChild(earth);

//		Appearance atmApp = new Appearance();
//		atmApp.setColoringAttributes(new ColoringAttributes(.0f, .0f, .7f, ColoringAttributes.NICEST));
//		atmApp.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, .3f));
//		Sphere atm = new Sphere(EARTH_RAD * 1.01f, Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS, EARTH_SPHERE_DIVISIONS, atmApp);
		//earth.addChild(atm);
		
		//вращаем землю
		new Timer(DT, (e) -> {
			fi += DFI;
			Transform3D rotation = new Transform3D();
			rotation.setRotation(new AxisAngle4f(new Vector3f(0, 1, 0), -fi));
			tg.setTransform(rotation);
		}).start();
		return tg;
	}

	private Node configureSun(String texturePath){	
		Appearance sunApp = new Appearance();
		
		
		TextureLoader textureLoader = new TextureLoader(texturePath + "/" + SUN_TEX_NAME, canvas);
		Texture2D texture = new Texture2D(
				Texture2D.BASE_LEVEL,
				Texture2D.RGB,
				2400,
				2400);
		texture.setImage(0, textureLoader.getImage());
		
		sunApp.setTexture(texture);
		
		Box sun = new Box(0f, 2.4f, 2.4f, Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS, sunApp);
		//sun.getShape(Box.LEFT).setAppearance(sunApp);
		
		
		TransformGroup tg = new TransformGroup();
		tg.addChild(sun);
		
		Transform3D transform = new Transform3D();
		transform.setTranslation(new Vector3d(AU + 1, 0, 0));
		tg.setTransform(transform);
		
		return tg;
	}
}
