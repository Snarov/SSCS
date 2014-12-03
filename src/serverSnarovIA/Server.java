package serverSnarovIA;

import java.util.*;
import java.security.*;

//основной класс содержащий точку входа в приложение. Отвечает за конфигурацию модели станции, 
//инициализацию виртуального мира и его  загрузку и сохранение и ответ на подключение клиента для передачи ему информации
public class Server {

	//константы

	private static final String HASH_ALG = "MD5";		//алгоритм шифрования пароля
	//поля
	private static boolean restart = false;			//инициализируется ли виртуальная вселенная заново
	private static int port = 1488;					//порт, на котором работает сервер
	private static byte[] password;					//шифрованный пароль подключения
	private static final HashMap<String, CmdArgAction> cmdArgActions = new HashMap<>();	// отображение имен аргументов командной строки на соотв. действия

	//блок инициализации отображения задает связи между командами и их обработчиками
	static {
		//опция -p value назначает номер порта
		cmdArgActions.put("-p", (value) -> {
			if (value == null)
				return;
			try {
				port = Integer.parseUnsignedInt(value);
				if (port > 65535)
					throw new NumberFormatException();
			} catch (NumberFormatException exc) {
				System.err.println("Номер порта указан неверно.");
				System.exit(126);
			}
		});

		//опция -pwd value устанавливает пароль для подключения
		cmdArgActions.put("-pwd", (value) -> {
			if (value == null)
				return;
			byte[] notEncPassword = value.getBytes();
			try {
				MessageDigest md = MessageDigest.getInstance(HASH_ALG);
				md.reset();
				md.update(notEncPassword);
				password = md.digest();
			} catch (NoSuchAlgorithmException exc) {
				System.err.println(exc.getMessage());
				System.exit(126);
			} finally {
				Arrays.fill(notEncPassword, (byte) 0);
			}
		});

		//опция -r инициализирует виртуальную вселенную заново
		cmdArgActions.put("-r", (value) -> {
			restart = true;
		});
	}

	public static void main(String[] args) {
		//разбор аргументов командной строки
		for(int i = 0; i < args.length; i++){
			
		}
	}
}
