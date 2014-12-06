package serverSnarovIA;

import java.util.*;
import java.io.File;
import java.security.*;
import javax.media.j3d.BoundingSphere;
import javax.vecmath.Point3d;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.Atmosphere;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.GravityField;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.MaterialPoint;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.PhysicalUniverse;
import serverSnarovIA.modelSnarovIA.stationSnarovIA.Station;

//основной класс содержащий точку входа в приложение. Отвечает за конфигурацию модели станции, 
//инициализацию виртуального мира и его  загрузку и сохранение и ответ на подключение клиента для передачи ему информации
public class Server {

	//константы
	private static final String CONF_FILE_NAME = "/home/snarov/NetBeansProjects/SSCS/SSCS.conf";
	private static final String HASH_ALG = "MD5";		//алгоритм шифрования пароля
	//поля
	private static boolean restart = false;			//инициализируется ли виртуальная вселенная заново
	private static int port = 1488;					//порт, на котором работает сервер
	private static byte[] password;					//шифрованный пароль подключения
	private static long frameRate = 33;				//время кадра (мс)
	private static String saveDir = null;			//директория с сохранениями 
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

		//опция -fr value задает квант времени (время кадра)
		cmdArgActions.put("-fr", (value) -> {
			if (value == null)
				return;
			try {
				frameRate = Integer.parseUnsignedInt(value);
			} catch (NumberFormatException exc) {
				System.err.println("Неверное значение времени кадра");
				System.exit(126);
			}
		});

		//опция -sp value задает директорию сохранения состояния вселенной
		cmdArgActions.put("-sp", (value) -> {
			if (value == null)
				return;
			File testDir = new File(value);
			if (testDir.isDirectory()) {
				saveDir = value;
			}else{
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
		//разбор аргументов командной строки
		for (int i = 0; i < args.length; i++) {
			if (cmdArgActions.containsKey(args[i]))
				cmdArgActions.get(args[i]).process(++i < args.length ? args[i] : null);
		}

		//чтение файла конфигурации
		ConfReader confReader = new ConfReader(CONF_FILE_NAME);
		//параметры станции
		double altitude = confReader.getConfValue("ALTITUDE");			//высота расположения станции
		double batteryCapacity = confReader.getConfValue("BATTERY_CAPACITY");
		double waterCapacity = confReader.getConfValue("WATER_CAPACITY");
		double oxygenCapacity = confReader.getConfValue("OXYGEN_CAPACITY");
		double hydrogenCapacity = confReader.getConfValue("HYDROGEN_CAPACITY");
		double engineMaxThrust = confReader.getConfValue("ENGINE_MAX_THRUST_VALUE");
		double engineThrustValue = confReader.getConfValue("ENGINE_THRUST_VALUE");
		double engineWorkingMassValue = confReader.getConfValue("ENGINE_WORKING_MASS_VALUE");
		double solarPanelECE = confReader.getConfValue("SOLAR_PANEL_ECE");
		double solarPanelRotateSpeed = confReader.getConfValue("SOLAR_PANEL_ROTATE_SPEED");
		double electrolyzerMaxPower = confReader.getConfValue("ELECTROLYZER_MAX_POWER");
		double electrolyzerECE = confReader.getConfValue("ELECTROLYZER_ECE");
		//параметры внешнего окружения
		double earthMass = confReader.getConfValue("EARTH_MASS");
		double gravityFieldRadius = confReader.getConfValue("GRAVITY_FIELD_RADIUS");
		double atmExternalBoundsRadius = confReader.getConfValue("ATM_EXTERNAL_BOUNDS_RADIUS");
		double atmInternalBoundsRadius = confReader.getConfValue("ATM_INTERNAL_BOUNDS_RADIUS");
		double atmTempLapseRate = confReader.getConfValue("ATM_TEMP_LAPSE_RATE");
		double G = confReader.getConfValue("G");
		double atmBasePressure = confReader.getConfValue("ATM_BASE_PRESSURE");
		double atmTemperature = confReader.getConfValue("ATM_TEMPERATURE");
		double atmMolarMass = confReader.getConfValue("ATM_MOLAR_MASS");

		//создание и заполнение виртуальной вселенной
		PhysicalUniverse physicalUniverse = new PhysicalUniverse(frameRate, 1);

		MaterialPoint center = new MaterialPoint(0, 0, 1);
		center.scale(altitude);
		Station station = new Station(
				center,
				batteryCapacity,
				waterCapacity,
				oxygenCapacity,
				hydrogenCapacity,
				engineMaxThrust,
				engineThrustValue,
				engineWorkingMassValue,
				solarPanelECE,
				solarPanelRotateSpeed,
				electrolyzerMaxPower,
				electrolyzerECE
		);

		GravityField earthGravity = new GravityField(earthMass, new BoundingSphere(new Point3d(), gravityFieldRadius));

		Point3d atmCenter = new Point3d();
		Atmosphere earthAtmosphere = new Atmosphere(
				new BoundingSphere(atmCenter, atmExternalBoundsRadius),
				new BoundingSphere(atmCenter, atmInternalBoundsRadius),
				atmTempLapseRate,
				G,
				atmBasePressure,
				atmTemperature,
				atmMolarMass
		);

		physicalUniverse.addForceField("EARTH_GRAVITY", earthGravity);
		physicalUniverse.addForceField("EARTH_ATMOSPHERE", earthAtmosphere);
		physicalUniverse.addPhysBody("STATION", station);

		//инициализация демона-сохраняльщика
		UniverseStateSaverDaemon saverDaemon = new UniverseStateSaverDaemon(physicalUniverse, saveDir);
	}
}