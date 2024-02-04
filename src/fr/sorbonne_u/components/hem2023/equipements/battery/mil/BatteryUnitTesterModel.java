package fr.sorbonne_u.components.hem2023.equipements.battery.mil;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

public class BatteryUnitTesterModel extends AtomicModel {

	private static final long serialVersionUID = 1L;
	public static final String URI = BatteryUnitTesterModel.class.getCanonicalName();
	
	protected int step;

	public BatteryUnitTesterModel(String uri, TimeUnit simulatedTimeUnit, AtomicSimulatorI simulationEngine) {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	@Override
	public Duration timeAdvance() {
		if(this.step < 10) 
			return new Duration(1.0, this.getSimulatedTimeUnit());
		return Duration.INFINITY;
	}
	
	@Override 
	public void initialiseState(Time initialTime) {
		super.initialiseState(initialTime);
		this.step = 1;
		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins\n");
	}
	
	@Override
	public void endSimulation(Time endTime) {
		this.logMessage("simulation ends\n");
		super.endSimulation(endTime);
	}
	
	@Override
	public SimulationReportI getFinalReport() {
		return null;
	}
}
