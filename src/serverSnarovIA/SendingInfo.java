package serverSnarovIA;

import java.io.Serializable;
import javax.vecmath.Point3d;

//структура данных, отправляемая клиенту в качестве датаграм
public class SendingInfo implements Serializable {

	private Point3d stationCoords;
	private double altitude;
	private double speed;
	private double oxygenLevel;
	private double hydrogenLevel;
	private double batteryLevel;
	private double solarPanelAngle;
	private long frameTime;	

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

	public void setStationCoords(Point3d stationCoords) {
		this.stationCoords = stationCoords;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setOxygenLevel(double oxygenLevel) {
		this.oxygenLevel = oxygenLevel;
	}

	public void setHydrogenLevel(double hydrogenLevel) {
		this.hydrogenLevel = hydrogenLevel;
	}

	public void setBatteryLevel(double batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public void setSolarPanelAngle(double solarPanelAngle) {
		this.solarPanelAngle = solarPanelAngle;
	}

	public long getFrameTime() {
		return frameTime;
	}

	public void setFrameTime(long frameTime) {
		this.frameTime = frameTime;
	}
}
