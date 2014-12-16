package serverSnarovIA.modelSnarovIA;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.Atmosphere;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.BoundingSphere;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.GravityField;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.Illuminant;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.MaterialPoint;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.PhysicalUniverse;
import serverSnarovIA.modelSnarovIA.stationSnarovIA.Station;

public class Model {
	//константы

	private static final String CONF_FILE_NAME = "/home/snarov/NetBeansProjects/SSCS/SSCS.conf";
	public static final double EARTH_RAD = 6.378E6;
	private static final double DEF_ALTITUDE = 300000;

	//поля
	private static long frameRate = 33;									//время кадра (мс)
	private final PhysicalUniverse physicalUniverse;									//вселенная

	//конструкторы
	public Model(long aFrameRate) {
		if(aFrameRate > 0)
			frameRate = aFrameRate;
		
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
		double sunIntensity = confReader.getConfValue("SUN_INTENSITY");
		double sunLuminousEfficacy = confReader.getConfValue("SUN_LUMINOUS_EFFICACY");
		double sunDistance = confReader.getConfValue("SUN_DISTANCE");

		//создание и заполнение виртуальной вселенной
		physicalUniverse = new PhysicalUniverse(frameRate, 1);

		MaterialPoint center = new MaterialPoint(0, 0, 1);
		center.scale(EARTH_RAD + (altitude > 0 ? altitude : DEF_ALTITUDE));
			
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
				electrolyzerECE,
				new Point3d()
		);

		GravityField earthGravity = new GravityField(earthMass, new BoundingSphere(new Point3d(), gravityFieldRadius));

		Vector3d velocity = earthGravity.getEscapeVelocity(center);
		station.getCenter().setVelocity(velocity);
		
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

		Point3d sunCoords = new Point3d(1, 0, 0);
		sunCoords.scale(sunDistance > 0 ? sunDistance : Illuminant.AU);
		Illuminant sun = new Illuminant(
				sunIntensity,
				sunLuminousEfficacy,
				new BoundingSphere(sunCoords, Illuminant.BOUNDS_RADIUS),
				sunCoords
		);

		physicalUniverse.addForceField("EARTH_GRAVITY", earthGravity);
		physicalUniverse.addForceField("EARTH_ATMOSPHERE", earthAtmosphere);
		physicalUniverse.addPhysBody("STATION", station);
		physicalUniverse.addLightSource("SUN", sun);

	}
	
	public Model(long aFrameRate, PhysicalUniverse oldUniverse){
		frameRate = aFrameRate;
		physicalUniverse = oldUniverse;
		
		physicalUniverse.initTimer();
		physicalUniverse.initTimerTask();
	}
	
	public PhysicalUniverse getUniverse(){
		return physicalUniverse;
	}
}
