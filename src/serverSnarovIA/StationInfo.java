package serverSnarovIA;

import javax.vecmath.Point3d;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.PhysicalUniverse;
import serverSnarovIA.modelSnarovIA.stationSnarovIA.Station;

//информация о станции для отправки клиенту
public class StationInfo implements SendingInfo{
	
	//поля
	transient private final PhysicalUniverse universe;			//станция, относительно которой указывается информация	
	
	private Point3d stationCoords;
	private double altitude;
	private double speed;
	private	double oxygenLevel;
	private	double hydrogenLevel;
	private double batteryLevel;
	private double solarPanelAngle;
	
	//конструкторы
	public StationInfo(PhysicalUniverse aUniverse){
		universe = aUniverse;
	}
	
	@Override
	public void refresh(){		//информация обновляется после расчета движком фрейма
		synchronized(universe){
			try {
				universe.wait();
				//после того, как освободится блокировка
				Station station = (Station)universe.getPhysBodies().get("Station");
				stationCoords = new Point3d(station.getCenter());
				
				Station.Panel stationPanel = (station.getPanel());
				altitude = stationPanel.getAltitude();
				speed = stationPanel.getSpeed();
				oxygenLevel = stationPanel.getOxygenLevel();
				hydrogenLevel = stationPanel.getHydrogenLevel();
				batteryLevel = stationPanel.getBatteryLevel();
				solarPanelAngle = stationPanel.getSolarPanelAngle();
			} catch (InterruptedException ex) {}
		}
	}

	public Point3d getStationCoords() {
		return stationCoords;
	}

	public double getAltitude() {
		return altitude;
	}

	public double getSpeed() {
		return speed;
	}

	public double getOxygenLevel() {
		return oxygenLevel;
	}

	public double getHydrogenLevel() {
		return hydrogenLevel;
	}

	public double getBatteryLevel() {
		return batteryLevel;
	}

	public double getSolarPanelAngle() {
		return solarPanelAngle;
	}
}
