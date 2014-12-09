package serverSnarovIA;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//поток-демон, выполняющий отправку данных от модели на сервере к клиенту (использует протокол UDP)
public class ModelToViewTransmissionDaemon extends Thread {

	//константы
	private static final int REMOTE_VIEW_PORT = 2048;	//порт клиента, принимающий данные

	//поля
	private SendingInfo sendingInfo;	    //некоторая структура данных, подлежащая отправке
	private DatagramSocket socket;
	private InetAddress remoteViewAddr;

	//конструкторы
	public ModelToViewTransmissionDaemon(SendingInfo aSendingInfo, InetAddress aRemoteViewAddr) throws SocketException {
		socket = new DatagramSocket();

		sendingInfo = aSendingInfo;
		remoteViewAddr = aRemoteViewAddr;
	}

	@Override
	public void run() {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();			//поток байтов для дальнейшей передачи
		try (ObjectOutputStream oos = new ObjectOutputStream(byteStream)) {

			while (true) {

			}
		} catch (IOException ex) {
			;
		}
	}
}
