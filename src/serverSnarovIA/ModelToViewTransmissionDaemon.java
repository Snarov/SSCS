package serverSnarovIA;

import java.net.*;

//поток-демон, выполняющий отправку данных от модели на сервере к клиенту (использует протокол UDP)
public class ModelToViewTransmissionDaemon extends Thread{
	//константы
	private static final int PORT = 2048;	//порт клиента, принимающий данные
	
	//поля
	private SendingInfo sendingInfo;	    //некоторая структура данных, подлежащая отправке
	private final DatagramSocket socket;	//сокет для соединения UDP
	
		
}
