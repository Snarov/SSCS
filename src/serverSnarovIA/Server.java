package serverSnarovIA;

import java.util.*;
import java.io.File;
import java.io.*;
import java.util.regex.*;
import java.net.*;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import serverSnarovIA.controllerSnarovIA.Controller;
import serverSnarovIA.controllerSnarovIA.RemoteController;
import serverSnarovIA.modelSnarovIA.Model;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.PhysicalUniverse;

//основной класс содержащий точку входа в приложение. Отвечает за конфигурацию модели станции, 
//инициализацию виртуального мира и его  загрузку и сохранение и ответ на подключение клиента для передачи ему информации
public class Server {

	//константы
	private static final String SAVE_READ_ERR = "Ошибка чтения сохраненного состояния вселенной";
	private static final String CLIENT_CONNECTED_MSG = "%s connected";
	private static final int REGISTRY_PORT = 4096;
	private static final byte[] RECEIVING = {'\06', '\06', '\06'};
	//поля
	private static boolean restart = false;								//инициализируется ли виртуальная вселенная заново
	private static int port = 1488;										//порт, на котором работает сервер
	private static final Password password = new Password();			//шифрованный пароль подключения
	private static long modelFrameRate = 33;									//время кадра (мс)
	private static String saveDir = null;								//директория с сохранениями 
	private static final HashMap<String, CmdArgAction> cmdArgActions = new HashMap<>();	// отображение имен аргументов командной строки на соотв. действия

	private static Model model;
	private static Controller controller;

	//блок инициализации отображения задает связи между командами и их обработчиками
	static {
		//опция -p value назначает номер порта
		cmdArgActions.put("-p", (value) -> {
			if (value == null) {
				return;
			}
			try {
				port = Integer.parseUnsignedInt(value);
				if (port > 65535) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException exc) {
				System.err.println("Номер порта указан неверно.");
				System.exit(126);
			}
		});

		//опция -pwd value устанавливает пароль для подключения
		cmdArgActions.put("-pwd", (value) -> {
			if (value == null) {
				return;
			}
			if (!Pattern.matches(Password.PWD_PATTERN, value)) {
				System.err.println("Некорректный формат пароля. Должны быть строчные и прописные латинские буквы и цифры");
				System.exit(126);
			}
			if (!password.setPassword(value)) {
				System.exit(126);
			}
		});

		//опция -fr value задает квант времени (время кадра)
		cmdArgActions.put("-fr", (value) -> {
			if (value == null) {
				return;
			}
			try {
				modelFrameRate = Integer.parseUnsignedInt(value);
			} catch (NumberFormatException exc) {
				System.err.println("Неверное значение времени кадра");
				System.exit(126);
			}
		});

		//опция -sp value задает директорию сохранения состояния вселенной
		cmdArgActions.put("-sp", (value) -> {
			if (value == null) {
				return;
			}
			File testDir = new File(value);
			if (testDir.isDirectory()) {
				saveDir = value;
			} else {
				System.err.println("Неверная директория сохранений");
				System.exit(126);
			}
		});

		//опция -r указывает на инициализацию виртуальной вселенной заново
		cmdArgActions.put("-r", (value) -> {
			restart = true;
		});
	}

	public static void main(String[] args) {
		//этап 1 - разбор командной строки
		parseCmd(args);
		//этап 2 - подготовка модели
		setupModel();
		//этап 3 - подготовка контроллера
		setupController();
		//этап 4 - работа с удаленным клиентом
		workWithClient();
	}

	//разбиение на процедуры
	private static void parseCmd(String args[]) {
		//разбор аргументов командной строки
		for (int i = 0; i < args.length; i++) {
			if (cmdArgActions.containsKey(args[i])) {
				cmdArgActions.get(args[i]).process(++i < args.length ? args[i] : null);
			}
		}
	}

	private static void setupModel() {	//проводит необходимые действия для работы с моделью
		//инициализация модели
		if (restart) {
			model = new Model(modelFrameRate);
		} else {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveDir + "/" + UniverseStateSaverDaemon.SAVENAME))) {
				PhysicalUniverse oldUniverse = (PhysicalUniverse) ois.readObject();
				model = new Model(modelFrameRate, oldUniverse);
			} catch (IOException | ClassNotFoundException ex) {
				System.err.println(SAVE_READ_ERR + "\n" + ex.getMessage());
			}
		}

		//инициализация демона-сохраняльщика
		UniverseStateSaverDaemon saverDaemon = new UniverseStateSaverDaemon(model.getUniverse(), saveDir);

		//запуск движка и демона-сохраняльщика
		model.getUniverse().startTime();
		saverDaemon.start();
	}

	private static void setupController() {	//создает контроллер и настраивает для него RMI
		controller = new Controller(model);
		try {
			//получаем объект-заглушку для использования на стороне клиента
			RemoteController stub = (RemoteController) UnicastRemoteObject.exportObject(controller, 0);

			//регистрируем заглушку, чтобы ее потом можно было получить удаленно
			Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);
			registry.bind("Controller", stub);
		} catch (RemoteException | AlreadyBoundException ex) {
			System.err.println(ex);
			System.exit(126);
		}
	}

	private static void workWithClient() {
		//ожидание авторизации со стороны клиента
		while (true) {
			try (ServerSocket serverSocket = new ServerSocket(port, 1); //слушающий сокет для 1 входящего соединения
					Socket authSocket = serverSocket.accept() //получение оконечной точки сетевого соединения с клиентом для проверки пароля
					) {
				Scanner scanner = new Scanner(authSocket.getInputStream());

				String clientPassword;

				while (true) {			//ожидание передачи верного пароля от клиента
					if (scanner.hasNextLine()) {
						clientPassword = scanner.nextLine();
						if (Pattern.matches(Password.PWD_PATTERN, clientPassword) && password.comparePass(clientPassword)) {
							authSocket.getOutputStream().write((int) '\06');		//послать клиенту ACK (подтверждение)
							break;
						} else {
							authSocket.getOutputStream().write('\15');	//negative ACK
						}
					}
				}

				System.out.println(String.format(CLIENT_CONNECTED_MSG, authSocket.getInetAddress()));	//вывод строки подключения

				//послать клиенту данные инициализации модели
				ObjectOutputStream ois = new ObjectOutputStream(authSocket.getOutputStream());
				ois.writeObject(new ViewInitData((model)));

				//ожидание подтверждения готовности к приему
				byte[] recBuf;

				do {
					recBuf = new byte[3];
					authSocket.getInputStream().read(recBuf);
				} while (!Arrays.equals(recBuf, RECEIVING));

				ServerToClientTransmissionDaemon stctd = new ServerToClientTransmissionDaemon(model.getUniverse(), authSocket.getInetAddress());
				stctd.run();
			} catch (IOException ex) {
				System.err.println(ex);
			}

		}
	}
}
