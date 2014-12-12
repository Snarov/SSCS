package clientSnarovIA.viewSnarovIA;

import javax.swing.*;
import serverSnarovIA.ViewInitData;

//основное окно приложения
public class SSCSFrame extends JFrame{
	//константы
	public static final String TITLE = "Система управления космической станцией";
	
	//поля
	private final AuthorizationComponent authComp;	
	private View view;
	
	//конструкторы
	public  SSCSFrame(){
		authComp = new AuthorizationComponent();
		setTitle(TITLE);
		add(authComp);
		pack();
	}
	
	public View getView(){
		return view;
	}
	
	public void initView(ViewInitData viewInitData){		//создает представление и отображает его в этом фрейме
		//view = new View(viewInitData);
		remove(authComp);
		add(view);
		pack();
	}
}
