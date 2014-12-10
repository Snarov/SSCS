package clientSnarovIA.viewSnarovIA;

import javax.swing.*;

//основное окно приложения
public class SSCSFrame extends JFrame{
	//константы
	public static final String TITLE = "Space Station Control System";
	
	//поля
	AuthorizationComponent authComp;	
	WorkspaceComponent workComp;
	
	//конструкторы
	public  SSCSFrame(){
		authComp = new AuthorizationComponent();
		setTitle(TITLE);
		add(authComp);
	}
}
