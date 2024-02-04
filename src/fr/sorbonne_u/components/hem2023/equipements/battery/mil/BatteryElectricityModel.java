package fr.sorbonne_u.components.hem2023.equipements.battery.mil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2023.HEM_ReportI;
import fr.sorbonne_u.components.hem2023.equipements.battery.interfaces.BatteryI.BATTERY_MODE;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

public class BatteryElectricityModel extends AtomicHIOA {

	private static final long serialVersionUID = 1L;
	
	public static final String URI = BatteryElectricityModel.class.getSimpleName();
	
	public static double MAX_POWER = 10000.0;
	public static double TENSION_NOMINAL = 250.0;
	
	protected final Duration integrationStep;
	protected static double STEP = 60.0 / 3600.0;
	
	protected BATTERY_MODE currentMode = BATTERY_MODE.CONSOMATION;
	
	protected boolean isModeHasChanged = false;
	
	protected final Value<Double> totalPowerStored = new Value<Double>(this);
	protected Value<Double> totalPowerProduced;
	protected Value<Double> totalPowerConsumed;

	public BatteryElectricityModel(String uri, TimeUnit simulatedTimeUnit, AtomicSimulatorI simulationEngine) {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.integrationStep = new Duration(STEP, simulatedTimeUnit);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}
	
	public void setMode(BATTERY_MODE mode, Time t) {
		if(mode != this.currentMode) {
			this.isModeHasChanged = true;
			this.currentMode = mode;
		}
	}
	
	public BATTERY_MODE getMode() {
		return this.currentMode;
	}

	@Override
	public void initialiseState(Time initialTime) {
		super.initialiseState(initialTime);
		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins\n");
	}
	
	@Override
	public boolean useFixpointInitialiseVariables() {
		return true;
	}
	
	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables() {
		if(!this.totalPowerStored.isInitialised()) {
			this.totalPowerStored.initialise(0.0);
			
			StringBuffer sb = new StringBuffer("Total power stored: ");
			sb.append(this.totalPowerStored.getValue());
			sb.append("Wh at ");
			sb.append(this.totalPowerStored.getTime());
			sb.append("\n");
			this.logMessage(sb.toString());
			
			return new Pair<>(1, 0);
		}
		else {
			return new Pair<>(0, 0);
		}
	}
	
	@Override
	public Duration timeAdvance() {
		return this.integrationStep;
	}
	
	@Override
	public void userDefinedInternalTransition(Duration elaspedTime) {
		if(this.totalPowerProduced.getValue().doubleValue() +
				this.totalPowerStored.getValue().doubleValue() < 
				BatteryElectricityModel.MAX_POWER) {
			
			this.totalPowerStored.setNewValue(this.totalPowerProduced.getValue().doubleValue() +
					this.totalPowerStored.getValue().doubleValue(), this.totalPowerStored.getTime());
		}
		else {
			this.logMessage("The energy stored in the battery is full\n");
		}
		
		if(this.totalPowerStored.getValue().doubleValue() - 
				this.totalPowerConsumed.getValue().doubleValue() > 0) {
			
			this.totalPowerStored.setNewValue(this.totalPowerStored.getValue().doubleValue() -
					this.totalPowerConsumed.getValue().doubleValue(), this.totalPowerStored.getTime());
		}
		else {
			this.logMessage("The energy stored in the battery is not enough\n");
		}
		
		StringBuffer tracing1 = new StringBuffer();
		tracing1.append("current power stored: ");
		tracing1.append(Math.round(this.totalPowerStored.getValue().doubleValue()) + " Wh");
		tracing1.append(" at " + this.totalPowerStored.getTime() + "\n");
		this.logMessage(tracing1.toString());
		
		StringBuffer tracing2 = new StringBuffer();
		tracing2.append("current power produced: ");
		tracing2.append(Math.round(this.totalPowerProduced.getValue().doubleValue()) + " Wh");
		tracing2.append(" at " + this.totalPowerProduced.getTime() + "\n");
		this.logMessage(tracing2.toString());
		
		StringBuffer tracing3 = new StringBuffer();
		tracing3.append("current power consumed: ");
		tracing3.append(Math.round(this.totalPowerConsumed.getValue().doubleValue()) + " Wh");
		tracing3.append(" at " + this.totalPowerConsumed.getTime() + "\n");
		this.logMessage(tracing3.toString());
		
		this.totalPowerConsumed.setNewValue(0.0, this.totalPowerConsumed.getTime());
		this.totalPowerProduced.setNewValue(0.0, this.totalPowerConsumed.getTime());
		
		super.userDefinedInternalTransition(elaspedTime);
	}
	
	@Override
	public void endSimulation(Time endTime) {
		if(this.totalPowerProduced.getValue().doubleValue() +
				this.totalPowerStored.getValue().doubleValue() < 
				BatteryElectricityModel.MAX_POWER) {
			
			this.totalPowerStored.setNewValue(this.totalPowerProduced.getValue().doubleValue() +
					this.totalPowerStored.getValue().doubleValue(), this.totalPowerStored.getTime());
		}
		else {
			this.logMessage("The energy stored in the battery is full\n");
		}
		
		if(this.totalPowerStored.getValue().doubleValue() - 
				this.totalPowerConsumed.getValue().doubleValue() > 0) {
			
			this.totalPowerStored.setNewValue(this.totalPowerStored.getValue().doubleValue() -
					this.totalPowerConsumed.getValue().doubleValue(), this.totalPowerStored.getTime());
		}
		else {
			this.logMessage("The energy stored in the battery is not enough\n");
		}
		
		this.logMessage("simulation ends");
		this.logMessage(new BatteryElectricityReport(URI, 
				Math.round(this.totalPowerStored.getValue().doubleValue())).printout("-"));
	}
	
	public static final String MAX_POWER_CAPACITY_RUNPNAME = "MAX_POWER_CAPACITY";
	public static final String TENSION_RUNPNAME = "TENSION";
	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) 
			throws MissingRunParameterException {
		super.setSimulationRunParameters(simParams);
		
		String parameterName = ModelI.createRunParameterName(this.getURI(), 
				MAX_POWER_CAPACITY_RUNPNAME);
		if(simParams.containsKey(parameterName))
			MAX_POWER = (double)simParams.get(parameterName);
		
		String tensionName = ModelI.createRunParameterName(this.getURI(), 
				TENSION_RUNPNAME);
		if(simParams.containsKey(tensionName))
			TENSION_NOMINAL = (double)simParams.get(tensionName);
	}
	
	public static class BatteryElectricityReport implements SimulationReportI, HEM_ReportI {
		private static final long serialVersionUID = 1L;
		protected String modelURI;
		protected double totalStored;
		
		public BatteryElectricityReport(String modelURI, double totalStored) {
			super();
			this.modelURI = modelURI;
			this.totalStored = totalStored;
		}

		@Override
		public String printout(String indent) {
			StringBuffer ret = new StringBuffer(indent);
			ret.append("\n=========================\n");
			ret.append("||\t");
			ret.append(this.modelURI);
			ret.append(" reports \t ||\n");
			ret.append("||\t");
			ret.append("total stored = " + this.totalStored);
			ret.append("\t||\n");
			ret.append("=========================\n");
			
			return ret.toString();
		}

		@Override
		public String getModelURI() {
			return this.modelURI;
		}
	}
	
	@Override
	public SimulationReportI getFinalReport() {
		return new BatteryElectricityReport(URI, this.totalPowerStored.getValue().doubleValue());
	}

}
