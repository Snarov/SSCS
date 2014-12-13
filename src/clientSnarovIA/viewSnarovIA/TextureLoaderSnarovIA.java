package clientSnarovIA.viewSnarovIA;

//загрузка текстур для моих нужд
import com.sun.j3d.utils.image.TextureLoader;
import java.awt.Component;
import javax.media.j3d.Texture2D;

abstract class TextureLoaderSnarovIA {

	final static Texture2D loadTexture(String filePath, Component observer, int width, int height) {
		TextureLoader textureLoader = new TextureLoader(filePath, observer);
		Texture2D texture = new Texture2D(
				Texture2D.BASE_LEVEL,
				Texture2D.RGB,
				width,
				height);
		texture.setImage(0, textureLoader.getImage());
		return texture;
	}
}
