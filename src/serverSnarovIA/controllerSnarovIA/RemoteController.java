
package serverSnarovIA.controllerSnarovIA;

import java.rmi.Remote;
import java.rmi.RemoteException;
import serverSnarovIA.modelSnarovIA.stationSnarovIA.Station;

//интерфейс, определяющий функционал удаленного контроллера
public interface RemoteController extends Remote{
	public void setTimeFactor(double value) throws RemoteException;
	public void setEngineThrust(Station.WorkingDeviceName engine, double value) throws RemoteException;
	public void setSolarPanelAngle(double value) throws RemoteException;
	public void setElectrolyzerPower(double value) throws RemoteException;
}
