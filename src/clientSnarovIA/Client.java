package clientSnarovIA;

import clientSnarovIA.viewSnarovIA.SSCSFrame;
import java.net.*;
import javax.swing.*;
import controllerSnarovIA.*;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;

//клиент инициализирует представление, связывается с сервером через TCP для аутентификации и инициализаци панели управления, 
//получает датаграммы с сервера и вызывает методы удаленного объекта на сервере.
public class Client {

	//константы
	private static final int REGISTRY_PORT = 4096;
	private static final int UDP_PORT = 2048;
	private static final int ACK_WAIT_TIME = 1000;	//максимальное ремя ожидания подтверждения от сервера

	//поля
	private static SSCSFrame appFrame;				//главный фрейм приложения

	private static boolean isConnected = false;		//соединен ли клиент с сервером
	private static InetAddress serverAddr;
	private static Socket authSock;				//TCP сокет для авторизации на сервере

	private static RemoteController controller;

	public static void main() {
		//этап 1 - создание фрейма
		setupFrame();
		while (!isConnected);	//ожидание подключения к серверу

		//этап 2 - инициализация удаленного контроллера
		setupController();
		//этап 3 - прием датаграм от сервера
		startReceiving();
	}

	//модификация и доступ
	public static void setIsConnected(boolean isConnected) {
		Client.isConnected = isConnected;
	}

	//поведение
	private static void setupFrame() {
		SwingUtilities.invokeLater(() -> {
			appFrame = new SSCSFrame();
			appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			appFrame.setVisible(true);
		});
	}

	private static void setupController() {
		try {
			controller = LocateRegistry.getRegistry(serverAddr.toString(), REGISTRY_PORT).lookup("Controller");
		} catch (RemoteException | NotBoundException ex) {
			System.err.println(ex.getMessage());
			System.exit(126);
		}
	}

	private static void startReceiving() {			//принимает пакеты и регулярно шлет серверу подтверждения
		try (DatagramSocket socket = new DatagramSocket(2048)) {
			DatagramPacket receivedPacket = new DatagramPacket(null, 0);
			DatagramPacket ackPacket = new DatagramPacket(new byte[]{'\06'}, 1, serverAddr, UDP_PORT);
			long lastACKTime = System.currentTimeMillis();
			while (true) {
				socket.receive(receivedPacket);	//прием
				socket.send(ackPacket);			//подтверждение

				//десериализация информации о кадре
				
				
				//обновление представления
				appFrame.getView().update()
			}

		} catch (SocketException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private static boolean authorize(InetAddress addr, int port, String password) {		//пытается авторизоваться на сервере. true, если удалось подключиться
		//если не подключен к серверу, то подключить
		try {
			if (authSock == null)
				authSock = new Socket(addr, port);

			new PrintStream(authSock.getOutputStream()).println(password);	//передать пароль

			//ожидание получения подтверждения
			long currentTime = System.currentTimeMillis();
			while (System.currentTimeMillis() - currentTime < ACK_WAIT_TIME) {
				if (authSock.getInputStream().read() == '\06') {
					authSock.close();
					return true;
				}
			}
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
		return false;
	}
}
