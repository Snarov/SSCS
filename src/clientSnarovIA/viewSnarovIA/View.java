package clientSnarovIA.viewSnarovIA;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JComponent;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import serverSnarovIA.ViewInitData;

//главный компонент представления, содержащий холст с виртуальной вселенной и панель управления станцией
public class View extends JComponent {

	private static final int DIST_FACTOR = 1000;

	private SimpleUniverse universe;	//представление виртуальной вселенной
	private Canvas3D canvas;			//холст с представлением

	public View(ViewInitData initInfo, String texturePath) {
		setLayout(new BorderLayout());

		//настройка холста
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(config);
		universe = new SimpleUniverse(canvas);
		add(BorderLayout.CENTER, canvas);

		//создание графа сцены
		BranchGroup sceneRoot = new BranchGroup();
		//sceneRoot.addChild(new Earth(texturePath, canvas));
		sceneRoot.addChild(new Sphere());
		//sceneRoot.addChild(new Sun(texturePath, canvas));
//		Point3f sunCoords = new Point3f(19, 0, 0);
//		PointLight sunLight = new PointLight(new Color3f(1, 1, 1), sunCoords, new Point3f(1, 0, 0));
//		Bounds influenceRegion = new BoundingSphere(new Point3d(sunCoords.x, sunCoords.y, sunCoords.z) , 2 * 19);
//		sunLight.setBounds(influenceRegion);
//		sceneRoot.addChild(sunLight);
//		sceneRoot.addChild(new Sphere(5));
		DirectionalLight dl = new DirectionalLight(new Color3f(1,1,1), new Vector3f(-1, 0 ,0));
		dl.setBounds(new BoundingSphere(new Point3d(), 100));
		sceneRoot.addChild(dl);
		//установка камеры
		TransformGroup vptg = universe.getViewingPlatform().getMultiTransformGroup().getTransformGroup(0);
		Transform3D vpTranslation = new Transform3D();
		vpTranslation.setTranslation(new Vector3d(0, 0, 3));
		vptg.setTransform(vpTranslation);
		//добавление поведения
		OrbitBehavior ob = new OrbitBehavior(canvas);
		ob.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE));
		universe.getViewingPlatform().setViewPlatformBehavior(ob);

		//добавление сцены на холст
		sceneRoot.compile();
		universe.addBranchGraph(sceneRoot);
	}
}
