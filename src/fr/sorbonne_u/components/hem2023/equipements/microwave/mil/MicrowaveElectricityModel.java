package fr.sorbonne_u.components.hem2023.equipements.microwave.mil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.*;
import fr.sorbonne_u.components.hem2023.utils.Electricity;
import fr.sorbonne_u.components.hem2023.HEM_ReportI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

@ModelExternalEvents(imported = {SetHighMicrowave.class,
								 SetLowMicrowave.class,
								 SetMediumMicrowave.class,
								 SetUnfreezeMicrowave.class,
								 SwitchOnMicrowave.class,
								 SwitchOffMicrowave.class})	
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
public class MicrowaveElectricityModel
extends AtomicHIOA
implements MicrowaveOperationI
{

	private static final long serialVersionUID = 1L;

	public static enum State {
		ON,
		OFF
	}
	
	public static enum Mode {
		UNFREEZE,
		LOW,			
		MEDDIUM,
		HIGH
	}
											
	/** URI for an instance model in MIL simulations; works as long as
	 *   only one instance is created.										*/
	public static final String	MIL_URI = MicrowaveElectricityModel.class.
													getSimpleName() + "-MIL";
	/** URI for an instance model in MIL real time simulations; works as
	 *  long as  only one instance is created.								*/
	public static final String	MIL_RT_URI = MicrowaveElectricityModel.class.
													getSimpleName() + "-MIL_RT";
	/** URI for an instance model in SIL simulations; works as long as
	 *   only one instance is created.										*/
	public static final String	SIL_URI = MicrowaveElectricityModel.class.
													getSimpleName() + "-SIL";

	//in watts
	public static double LOW_MODE_CONSUMPTION = 250.0; 
	public static double MEDDIUM_MODE_CONSUMPTION = 500.0;
	public static double HIGH_MODE_CONSUMPTION = 750.0;
	public static double UNFREEZE_MODE_CONSUMPTION = 1000.0; 
	
	//simulation run parameters
	public static final String LOW_MODE_CONSUMPTION_RPNAME =
													"LOW_MODE_CONSUMPTION";
	public static final String MEDDIUM_MODE_CONSUMPTION_RPNAME =
													"MEDDIUM_MODE_CONSUMPTION";
	public static final String	HIGH_MODE_CONSUMPTION_RPNAME =
													"HIGH_MODE_CONSUMPTION";
	public static final String	UNFREEZE_MODE_CONSUMPTION_RPNAME =
													"UNFREEZE_MODE_CONSUMPTION";
	public static final String	TENSION_RPNAME = "TENSION";

	//nominal tension (in Volts) 						
	public static double TENSION = 220.0; 

	//nominals values
	protected State	currentState = State.OFF;
	protected Mode currentMode = Mode.LOW;
	
	/** true when the electricity consumption of the dryer has changed
	 *  after executing an external event; the external event changes the
	 *  value of <code>currentState</code> and then an internal transition
	 *  will be triggered by putting through in this variable which will
	 *  update the variable <code>currentIntensity</code>.					*/
	protected boolean consumptionHasChanged = false;

	//in kwh
	protected double totalConsumption;

	/** current intensity in amperes; intensity is power/tension.			*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentIntensity = new Value<Double>(this);
	
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	public	MicrowaveElectricityModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			AtomicSimulatorI simulationEngine
			) throws Exception
		{
			super(uri, simulatedTimeUnit, simulationEngine);
			// set the logger to a standard simulation logger
			this.getSimulationEngine().setLogger(new StandardLogger());
		}
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public State getState() {
		return this.currentState;
	}
	
	public Mode getMode() {
		return this.currentMode;
	}
	
	public void setMode(Mode mode) {
		this.currentMode = mode;
	}
	
	public void setState (State state) {
		this.currentState = state;
	}
	
	@Override
	public void	turnOn()
	{
		if (this.currentState == MicrowaveElectricityModel.State.OFF) {
			this.currentMode = MicrowaveElectricityModel.Mode.LOW;
			this.toggleConsumptionHasChanged();
		}
	}
	
	@Override
	public void	turnOff()
	{
		if (this.currentState != MicrowaveElectricityModel.State.OFF) {
			this.currentState = MicrowaveElectricityModel.State.OFF;
			this.toggleConsumptionHasChanged();
		}
	}
	
	@Override
	public void	setHigh()
	{
		if (this.currentMode != MicrowaveElectricityModel.Mode.HIGH) {
			this.currentMode = MicrowaveElectricityModel.Mode.HIGH;
			this.toggleConsumptionHasChanged();
		}
	}
	
	@Override
	public void setLow()
	{
		if (this.currentMode != MicrowaveElectricityModel.Mode.LOW) {
			this.currentMode = MicrowaveElectricityModel.Mode.LOW;
			this.toggleConsumptionHasChanged();
		}
	}
	
	@Override
	public void	setMeddium()
	{
		if (this.currentMode != MicrowaveElectricityModel.Mode.MEDDIUM) {
			this.currentMode = MicrowaveElectricityModel.Mode.MEDDIUM;
			this.toggleConsumptionHasChanged();
		}
	}
	
	@Override
	public void	setUnfreeze()
	{
		if (this.currentMode != MicrowaveElectricityModel.Mode.UNFREEZE) {
			this.currentMode = MicrowaveElectricityModel.Mode.UNFREEZE;
			this.toggleConsumptionHasChanged();
		}
	}
	
	public void toggleConsumptionHasChanged() {
		if(consumptionHasChanged)
			consumptionHasChanged = false;
		else 
			consumptionHasChanged = true;
	}
	
	//un peu different de l'origininal (une methode en moins)
	@Override
	public void initialiseState(Time startTime) {
		super.initialiseState(startTime);
		currentState = State.OFF;
		currentMode = Mode.LOW;
		consumptionHasChanged = false;
		totalConsumption = 0.0;
		
		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}
	
	@Override
	public void			initialiseVariables()
	{
		super.initialiseVariables();

		this.currentIntensity.initialise(0.0);
	}

	@Override
	public ArrayList<EventI> output() {
		return null;
	}
	
	@Override
	public Duration timeAdvance() {
		if (consumptionHasChanged) {
			this.toggleConsumptionHasChanged();
			return new Duration(0.0, this.getSimulatedTimeUnit());
		} else 
			return Duration.INFINITY;
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		super.userDefinedInternalTransition(elapsedTime);
		
		Time t = this.getCurrentStateTime();
		switch(currentState) {
			case OFF: 	currentIntensity.setNewValue(0.0, t);
					  	break;
			case ON: 	switch(currentMode) {
							case LOW: 		currentIntensity.setNewValue(LOW_MODE_CONSUMPTION / TENSION, t);
									  		break;
							case MEDDIUM: 	currentIntensity.setNewValue(MEDDIUM_MODE_CONSUMPTION / TENSION, t);
										  	break;
							case HIGH: 		currentIntensity.setNewValue(HIGH_MODE_CONSUMPTION / TENSION, t);
							  		   		break;
							case UNFREEZE:  currentIntensity.setNewValue(UNFREEZE_MODE_CONSUMPTION / TENSION, t);
					  		   				break;
						 }
						 break;
		}
		// Tracing
		StringBuffer message = new StringBuffer("executes an internal transition ");
		message.append("with current consumption ");
		message.append(this.currentIntensity.getValue());
		message.append(" at ");
		message.append(this.currentIntensity.getTime());
		message.append(".\n");
		this.logMessage(message.toString());
	}
	
	//contrôler le nombre d'événements externes dans currentEvents
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		super.userDefinedExternalTransition(elapsedTime);
		
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert currentEvents != null;
		
		for(EventI cei : currentEvents) {
			Event ce = (Event)cei;
			assert ce instanceof AbstractMicrowaveEvent;
			
			this.totalConsumption += 
					Electricity.computeConsumption(elapsedTime, 
													TENSION * this.currentIntensity.getValue());
			
			StringBuffer message = new StringBuffer("executes an external transition");
			message.append(ce.toString());
			message.append(")\n");
			this.logMessage(message.toString());
			
			ce.executeOn(this);
		}
	}
	
	@Override
	public void endSimulation(Time endTime) {
		Duration d = endTime.subtract(this.getCurrentStateTime());
		this.totalConsumption +=
				Electricity.computeConsumption(d,
												TENSION * this.currentIntensity.getValue());

		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}
	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) 
			throws MissingRunParameterException {
		super.setSimulationRunParameters(simParams);
		
		String lowName = ModelI.createRunParameterName(
				getURI(), LOW_MODE_CONSUMPTION_RPNAME);
		String MeddiumName = ModelI.createRunParameterName(
				getURI(), MEDDIUM_MODE_CONSUMPTION_RPNAME);
		String highName =ModelI.createRunParameterName(
				getURI(),HIGH_MODE_CONSUMPTION_RPNAME);
		String unfreezeName = ModelI.createRunParameterName(
				getURI(), UNFREEZE_MODE_CONSUMPTION_RPNAME);
		String tensionName = ModelI.createRunParameterName(
				getURI(), TENSION_RPNAME);
		
		if(simParams.containsKey(lowName))
			LOW_MODE_CONSUMPTION = (double)simParams.get(lowName);
		if(simParams.containsKey(MeddiumName))
			MEDDIUM_MODE_CONSUMPTION = (double)simParams.get(MeddiumName);
		if(simParams.containsKey(highName))
			HIGH_MODE_CONSUMPTION = (double)simParams.get(highName);
		if(simParams.containsKey(unfreezeName))
			UNFREEZE_MODE_CONSUMPTION = (double)simParams.get(unfreezeName);
		if(simParams.containsKey(tensionName))
			TENSION = (double)simParams.get(tensionName);
	}
	
	public static class MicrowaveElectricityReport 
	implements SimulationReportI, HEM_ReportI {
		private static final long serialVersionUID = 1L;
		protected String modelURI;
		protected double totalConsumption;
		
		public MicrowaveElectricityReport(String modelURI, double totalConsumption) {
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
		}
		
		@Override
		public String getModelURI() {
			return null;
		}

		@Override
		public String printout(String indent) {
			StringBuffer ret = new StringBuffer(indent);
			ret.append("---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			ret.append("total consumption in kwh = ");
			ret.append(this.totalConsumption);
			ret.append(".\n");
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}
	}
	
	@Override
	public SimulationReportI getFinalReport() {
		return new MicrowaveElectricityReport(this.getURI(), this.totalConsumption);
	}	
}
