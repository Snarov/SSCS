package serverSnarovIA;

import java.io.Serializable;
import javax.vecmath.Point3d;
import serverSnarovIA.modelSnarovIA.Model;
import serverSnarovIA.modelSnarovIA.stationSnarovIA.Station;

//данные, необходимые клиенту для инициализации представления
public class ViewInitData implements Serializable{

	//поля
	private final double batteryCapacity;
	private final double waterCapacity;
	private final double oxygenCapacity;
	private final double hydrogenCapacity;
	private final double engineMaxThrust;
	private final double sunIntensity;
	private final double sunDistance;
	
	//конструкторы
	public ViewInitData(Model model){
		Station station = (Station)model.getUniverse().getPhysBodies().get("Station");
		
		batteryCapacity = station.getBatteryCapacity();
		waterCapacity = station.getWaterCapacity();
		oxygenCapacity = station.getOxygenCapacity();
		hydrogenCapacity = station.getHydrogenCapacity();
		engineMaxThrust = station.getEngineMaxThrust();
		
		sunIntensity = model.getUniverse().getLightSources().get("SUN").getIntensity();
		sunDistance = model.getUniverse().getLightSources().get("SUN").getCoords().distance(new Point3d());
	}
	
	//методы модификапции
	public double getBatteryCapacity() {
		return batteryCapacity;
	}

	public double getWaterCapacity() {
		return waterCapacity;
	}

	public double getOxygenCapacity() {
		return oxygenCapacity;
	}

	public double getHydrogenCapacity() {
		return hydrogenCapacity;
	}

	public double getEngineMaxThrust() {
		return engineMaxThrust;
	}

	public double getSunIntensity() {
		return sunIntensity;
	}

	public double getSunDistance() {
		return sunDistance;
	}
}
