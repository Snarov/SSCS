package clientSnarovIA.viewSnarovIA;

import clientSnarovIA.Client;
import javax.swing.*;
import serverSnarovIA.ViewInitData;

//основное окно приложения
public class SSCSFrame extends JFrame{
	//константы
	public static final String TITLE = "Система управления космической станцией";
	
	//поля
	private AuthorizationComponent authComp;	
	private View view;
	
	//конструкторы
	public  SSCSFrame(){
		setTitle(TITLE);
		
//		authComp = new AuthorizationComponent();
//		add(authComp);
//		pack();
		
		initView(null, Client.TEXTURE_PATH);
	}
	
	public View getView(){
		return view;
	}
	
	public void initView(ViewInitData viewInitData, String texturePath){		//создает представление и отображает его в этом фрейме
		view = new View(viewInitData, texturePath);
		//remove(authComp);
		//Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		//setBounds(0, 0, dimension.width, dimension.height);			//разворачиваем на весь экран
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		add(view);
	}
}
