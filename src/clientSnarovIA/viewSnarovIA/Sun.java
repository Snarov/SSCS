package clientSnarovIA.viewSnarovIA;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import java.awt.Component;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

//спрайтовое солнце и источник света
public class Sun extends TransformGroup{
	//примитив
	private static final float SPRITE_SIZE = 2.4f;		//длина стороны спрайта
	//текстура
	private static final String SUN_TEX_NAME = "SunTex.jpg";
	private static final int TEXTURE_WIDTH = 2400;
	private static final int TEXTURE_HEIGHT = 2400;
//	//материал
//	private static final Color3f DEF_AMBIENT_COLOR = new Color3f(.1f, .1f, .1f);
//	private static final Color3f DEF_EMISSIVE_COLOR = new Color3f(0, 0, 0);
//	private static final Color3f DEF_DIFFUSE_COLOR = new Color3f(.6f, .6f, .6f);
//	private static final Color3f DEF_SPECULAR_COLOR = new Color3f(0, 0, 0);
//	private static final float DEF_SHINESS = 300f;
	private static final Color3f SUN_LIGHT_COLOR = new Color3f(1, 1, 1);
	private static final Point3f SUN_LIGHT_ATTENUATION = new Point3f(.5f, 0, 0);
	private static final int AU = 20;
	
	public Sun(String texturePath, Component observer){
		Appearance sunApp = new Appearance();
		sunApp.setTexture(TextureLoaderSnarovIA.loadTexture(texturePath + "/" + SUN_TEX_NAME, observer, TEXTURE_WIDTH, TEXTURE_HEIGHT));
		
		//собственно сам спрайт
		Box sun = new Box(0f, SPRITE_SIZE, SPRITE_SIZE, Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS, sunApp);
		
		//источник света
		Point3f sunCoords = new Point3f(AU, 0, 0);
		PointLight sunLight = new PointLight(SUN_LIGHT_COLOR, sunCoords, SUN_LIGHT_ATTENUATION);
		Bounds influenceRegion = new BoundingSphere(new Point3d(sunCoords.x, sunCoords.y, sunCoords.z) , 2 * AU);
		sunLight.setBounds(influenceRegion);
		sunLight.setEnable(true);
		addChild(sun);
		addChild(sunLight);
		
		//размещение в пр-ве
		Transform3D transform = new Transform3D();
		transform.setTranslation(new Vector3d(AU, 0, 0));
		setTransform(transform);
	}
}
