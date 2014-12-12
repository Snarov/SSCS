package clientSnarovIA.viewSnarovIA;

import com.sun.j3d.utils.universe.SimpleUniverse;
import javax.media.j3d.Canvas3D;
import javax.swing.*;

//главный компонент представления, содержащий холст с виртуальной вселенной и панель управления станцией
public class View extends JComponent{
	//поля
	private SimpleUniverse universe;	//представление виртуальной вселенной
	private Canvas3D canvas;			//холст с представлением
}	
