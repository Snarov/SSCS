package clientSnarovIA;

import clientSnarovIA.viewSnarovIA.SSCSFrame;
import java.net.*;
import javax.swing.*;
import controllerSnarovIA.*;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//клиент инициализирует представление, связывается с сервером через TCP для аутентификации и инициализаци панели управления, 
//получает датаграммы с сервера и вызывает методы удаленного объекта на сервере.
public class Client {
	//константы
	private static final int REGISTRY_PORT = 4096;
	private static final int UDP_PORT = 2048;
	private static final int ACK_INTERVAL = 1000;
	
		
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
		try {
			controller = LocateRegistry.getRegistry(serverAddr.toString(), REGISTRY_PORT).lookup("Controller");
		} catch (RemoteException | NotBoundException ex) {
			System.err.println(ex.getMessage());
			System.exit(126);
		}
	}
	
	private static void startReceiving(){			//принимает пакеты и регулярно шлет серверу подтверждения
		try(DatagramSocket socket = new DatagramSocket(2048)){
			DatagramPacket receivedPacket = new DatagramPacket(null, 0);
			long lastACKTime = System.currentTimeMillis();
			while(true){
				socket.receive(receivedPacket);
				if(System.currentTimeMillis() - lastACKTime >= ACK_INTERVAL){
					DatagramPacket ackPacket = new DatagramPacket(new byte[]{'\06'}, 1);
					socket.send(ackPacket);
				}
				//здесь должно быть что то еще
			}
				
		} catch (SocketException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private static boolean authorize(InetAddress addr, int port, String password){		//пытается авторизоваться на сервере. true если удалось
		
	}
}
