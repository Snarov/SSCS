package clientSnarovIA.viewSnarovIA;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JComponent;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import serverSnarovIA.ViewInitData;

//главный компонент представления, содержащий холст с виртуальной вселенной и панель управления станцией
public class View extends JComponent {

	private static final int DIST_FACTOR = 1000;
	private static final double CLIP_DISTANCE = 1000;
	private static final double FOV = 1;

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
		sceneRoot.addChild(new Earth(texturePath, canvas));
		sceneRoot.addChild(new Sun(texturePath, canvas));
		
		//установка камеры
		TransformGroup vptg = universe.getViewingPlatform().getMultiTransformGroup().getTransformGroup(0);
		Transform3D vpTranslation = new Transform3D();
		vpTranslation.setTranslation(new Vector3d(0, 0, 3));
		vptg.setTransform(vpTranslation);
		universe.getViewer().getView().setBackClipDistance(CLIP_DISTANCE);	//дальность отрисовки
		universe.getViewer().getView().setFieldOfView(FOV);
		//добавление поведения камеры
		OrbitBehavior ob = new OrbitBehavior(canvas);
		ob.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE));
		universe.getViewingPlatform().setViewPlatformBehavior(ob);

		//добавление сцены на холст
		sceneRoot.compile();
		universe.addBranchGraph(sceneRoot);
	}
}
