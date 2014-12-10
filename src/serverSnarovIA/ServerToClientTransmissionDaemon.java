package serverSnarovIA;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

//поток-демон, выполняющий отправку данных от модели на сервере к клиенту (использует протокол UDP)
public class ServerToClientTransmissionDaemon extends Thread {

	//константы
	private static final int REMOTE_VIEW_PORT = 2048;	//порт клиента, принимающий данные
	private static final int TIMEOUT = 5000;

	//поля
	private final SendingInfo sendingInfo;	    //некоторая структура данных, подлежащая отправке
	private final DatagramSocket socket;
	private final InetAddress remoteViewAddr;

	//конструкторы
	public ServerToClientTransmissionDaemon(SendingInfo aSendingInfo, InetAddress aRemoteViewAddr) throws SocketException {
		socket = new DatagramSocket();
		socket.setSoTimeout(TIMEOUT);
		
		sendingInfo = aSendingInfo;
		remoteViewAddr = aRemoteViewAddr;
	}

	@Override
	public void run() {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();			//поток байтов для дальнейшей передачи
		try (ObjectOutputStream oos = new ObjectOutputStream(byteStream)) {
			while (true) {
				sendingInfo.refresh();

				oos.writeObject(sendingInfo);
				byte[] buffer = byteStream.toByteArray();

				DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, remoteViewAddr, REMOTE_VIEW_PORT);
				socket.send(datagram);
				try {
					socket.receive(datagram);
				}catch(SocketTimeoutException ex){
					socket.close();
					return;
				}
				byteStream.reset();
			}
		} catch (IOException ex) {
		}
	}
}
