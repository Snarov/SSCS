package serverSnarovIA.controllerSnarovIA;

import serverSnarovIA.modelSnarovIA.Model;
import serverSnarovIA.modelSnarovIA.stationSnarovIA.Station;

//тонкий контроллер для уведомления модели о изменениях
public class Controller implements RemoteController {

	//поля
	private final Model model;

	//конструкторы
	public Controller(Model aModel) {
		model = aModel;
	}

	//поведение
	@Override
	public void setTimeFactor(double timeFactor){
		model.getUniverse().setTimeFactor(timeFactor);
	}
	
	@Override
	public void setEngineThrust(Station.WorkingDeviceName engine, double thrust){
		((Station)model.getUniverse().getPhysBodies().get("Station")).setEngineThrust(engine, thrust);
	}
	
	@Override
	public void setSolarPanelAngle(double angle){
		((Station)model.getUniverse().getPhysBodies().get("Station")).rotateSolarPanels(angle);
	}
	
	@Override
	public void setElectrolyzerPower(double power){
		((Station)model.getUniverse().getPhysBodies().get("Station")).setElectrolyzerPower(power);
	}
}
