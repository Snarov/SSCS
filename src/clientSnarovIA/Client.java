package clientSnarovIA;

import clientSnarovIA.viewSnarovIA.SSCSFrame;
import java.net.*;
import javax.swing.*;

//клиент инициализирует представление, связывается с сервером через TCP для аутентификации и инициализаци панели управления, 
//получает датаграммы с сервера и вызывает методы удаленного объекта на сервере.
public class Client {
	//константы
	private static final int REGISTRY_PORT = 2048;
	
	static{
		try {
			Class.forName("RemoteController");		//интерфейс удаленного контроллера
		} catch (ClassNotFoundException ex) {
			System.err.println(ex.getMessage());
			System.exit(126);
		}
	}
	
	//поля
	private static SSCSFrame appFrame;				//главный фрейм приложения
	private static boolean isConnected = false;		//соединен ли клиент с сервером
	private static InetAddress serverAddr;
	private static int serverPort;
	private static RemoteController controller;
	
	public static void main(){
		//этап 1 - создание фрейма
		setupFrame();
		while(!isConnected);	//ожидание подключения к серверу
		
		//этап 2 - инициализация удаленного контроллера
		setupController();
		//этап 3 - прием датаграм от сервера
		startReceiving();
	}
	
	//модификация и доступ
	
	public static void setIsConnected(boolean isConnected){
		Client.isConnected = isConnected;
	}
	
	//поведение
	private static void setupFrame(){
		SwingUtilities.invokeLater(() -> {
			appFrame = new SSCSFrame();
			appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			appFrame.setVisible(true);
		});
	}
	
	private static void setupController(){
		co
	}
	
	private static void startReceiving(){
		
	}
	
	private static void authorize(){
		
	}
}
