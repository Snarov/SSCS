package clientSnarovIA.viewSnarovIA;

import javax.swing.*;

//основное окно приложения
public class SSCSFrame extends JFrame{
	//константы
	public static final String TITLE = "Система управления космической станцией";
	
	//поля
	private final AuthorizationComponent authComp;	
	private WorkspaceComponent workComp;
	
	//конструкторы
	public  SSCSFrame(){
		authComp = new AuthorizationComponent();
		setTitle(TITLE);
		add(authComp);
		pack();
	}
	
	public WorkspaceComponent getView(){
		return workComp;
	}
}
