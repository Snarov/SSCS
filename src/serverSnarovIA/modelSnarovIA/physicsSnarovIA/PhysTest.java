package serverSnarovIA.modelSnarovIA.physicsSnarovIA;

import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.Canvas3D;
import javax.swing.JFrame;

public class PhysTest extends JFrame {

	Canvas3D canvas;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			PhysTest test = new PhysTest();
			test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			test.run();
			test.setVisible(true);
		});
	}

	private PhysTest() {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(config);
		canvas.setSize(800, 600);
		add(canvas);
		pack();
	}

	private void run() {
		//модель
	
	}
}
