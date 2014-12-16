package serverSnarovIA;

import javax.vecmath.Point3d;
import serverSnarovIA.modelSnarovIA.physicsSnarovIA.PhysicalUniverse;
import serverSnarovIA.modelSnarovIA.stationSnarovIA.Station;

//класс-оболочка над SendingInfo, обновляющий отправляемую информацию
public class StationInfoRefresher {

	private final PhysicalUniverse universe;			//виртуальная вселенная, в которой находится информация

	private final SendingInfo sendingInfo = new SendingInfo();

		public StationInfoRefresher(PhysicalUniverse aUniverse) {
		universe = aUniverse;
	}

	public void refresh() {		//информация обновляется после расчета движком фрейма
		synchronized (universe) {
			try {
				universe.wait();
				//после того, как освободится блокировка
				Station station = (Station) universe.getPhysBodies().get("STATION");
				sendingInfo.setStationCoords(new Point3d(station.getCenter()));

				Station.Panel stationPanel = (station.getPanel());
				sendingInfo.setAltitude(stationPanel.getAltitude());
				sendingInfo.setSpeed(stationPanel.getSpeed());
				sendingInfo.setOxygenLevel(stationPanel.getOxygenLevel());
				sendingInfo.setHydrogenLevel(stationPanel.getHydrogenLevel());
				sendingInfo.setBatteryLevel(stationPanel.getBatteryLevel());
				sendingInfo.setSolarPanelAngle(stationPanel.getSolarPanelAngle());
				sendingInfo.setFrameTime((long)(universe.getdT() * universe.getTimeFactor()));
			} catch (InterruptedException ex) {
			}
		}
	}

	public SendingInfo getSendingInfo() {
		return sendingInfo;
	}

}
