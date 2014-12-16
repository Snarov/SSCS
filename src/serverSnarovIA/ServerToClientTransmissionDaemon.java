package serverSnarovIA;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.PhysicalUniverse;

//поток-демон, выполняющий отправку данных от модели на сервере к клиенту (использует протокол UDP)
public class ServerToClientTransmissionDaemon extends Thread {

	//константы
	private static final int REMOTE_VIEW_PORT = 2048;	//порт клиента, принимающий данные
	private static final int ACK_TIMEOUT = 15;			//вреия ожидания подтверждения
	private static final short MAX_PACKETS_LOSS = 50;	//допустимое количество неподтвержденных пакетов подряд. При достижении этого числа клиент считается отключенным

	//поля
	private final StationInfoRefresher stationInfoRefresher;	    //некоторая структура данных, подлежащая отправке
	private final DatagramSocket socket;
	private final InetAddress remoteViewAddr;

	//конструкторы
	public ServerToClientTransmissionDaemon(PhysicalUniverse physUniverse, InetAddress aRemoteViewAddr) throws SocketException {
		socket = new DatagramSocket(REMOTE_VIEW_PORT);
		
		//socket.setSoTimeout(ACK_TIMEOUT);

		stationInfoRefresher = new StationInfoRefresher(physUniverse);
		remoteViewAddr = aRemoteViewAddr;
	}

	@Override
	public void run() {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();			//поток байтов для дальнейшей передачи
		try (ObjectOutputStream oos = new ObjectOutputStream(byteStream)) {
			short lostPacketsCount = 0;

			while (lostPacketsCount < MAX_PACKETS_LOSS) {
				stationInfoRefresher.refresh();

				oos.writeObject(stationInfoRefresher.getSendingInfo());
				byte[] buffer = byteStream.toByteArray();

				DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, remoteViewAddr, REMOTE_VIEW_PORT);
				socket.send(datagram);
//				try {
//					socket.receive(datagram);
//					lostPacketsCount = 0;
//				} catch (SocketTimeoutException ex) {
//					++lostPacketsCount;
//				}

				oos.reset();
				byteStream.reset();
			}
		} catch (IOException ex) {
		}
		
		System.out.println(remoteViewAddr.toString() + "отключился");
	}
}
