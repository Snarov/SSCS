package serverSnarovIA;

import javax.vecmath.Point3d;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.PhysicalUniverse;
import serverSnarovIA.modelSnarovIA.stationSnarovIA.Station;

//класс-оболочка над SendingInfo, обновляющий отправляемую информацию
public class StationInfoRefresher {

	//поля
	private final PhysicalUniverse universe;			//виртуальная вселенная, в которой находится информация

	private SendingInfo sendingInfo;

	//конструкторы
	public StationInfoRefresher(PhysicalUniverse aUniverse) {
		universe = aUniverse;
	}

	public void refresh() {		//информация обновляется после расчета движком фрейма
		synchronized (universe) {
			try {
				universe.wait();
				//после того, как освободится блокировка
				Station station = (Station) universe.getPhysBodies().get("Station");
				sendingInfo.setStationCoords(new Point3d(station.getCenter()));

				Station.Panel stationPanel = (station.getPanel());
				sendingInfo.setAltitude(stationPanel.getAltitude());
				sendingInfo.setSpeed(stationPanel.getSpeed());
				sendingInfo.setOxygenLevel(stationPanel.getOxygenLevel());
				sendingInfo.setHydrogenLevel(stationPanel.getHydrogenLevel());
				sendingInfo.setBatteryLevel(stationPanel.getBatteryLevel());
				sendingInfo.setSolarPanelAngle(stationPanel.getSolarPanelAngle());
			} catch (InterruptedException ex) {
			}
		}
	}

	public SendingInfo getSendingInfo() {
		return sendingInfo;
	}

}
