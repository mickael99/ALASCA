package fr.sorbonne_u.components.hem2023.equipements.microwave.mil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.*;
import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

@ModelExternalEvents(exported = {SwitchOnMicrowave.class,
		 						 SwitchOffMicrowave.class,
		 						 SetLowMicrowave.class,
		 						 SetMediumMicrowave.class,
								 SetHighMicrowave.class,
								 SetUnfreezMicrowave.class})
public class MicrowaveUserModel extends AtomicES_Model {

	private static final long serialVersionUID = 1L;
	
	public static final String URI = MicrowaveUserModel.class.getSimpleName();
	
	protected static double STEP_MEAN_DURATION = 5.0 / 60.0;
	protected static double DELAY_MEAN_DURATION = 4.0; 
	
	public static final String MEAN_STEP_RPNAME = "STEP_MEAN_DURATION";
	public static final String MEAN_DELAY_RPNAME = "STEP_MEAN_DURATION";
	
	protected final RandomDataGenerator rg;

	public MicrowaveUserModel(String uri, TimeUnit simulatedTimeUnit, 
								AtomicSimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	protected void generateNextEvent() {
		EventI current = this.eventList.peek();
		ES_EventI nextEvent = null;
		if(current instanceof SwitchOffMicrowave) {
			Time t = this.computeTimeOfNextUsage(current.getTimeOfOccurrence());
			nextEvent = new SwitchOnMicrowave(t);
		}
		else {
			Time t = this.computeTimeOfNextEvent(current.getTimeOfOccurrence());
			if(current instanceof SwitchOnMicrowave)
				nextEvent = new SetLowMicrowave(t);
			if(current instanceof SetLowMicrowave) 
				nextEvent = new SetMediumMicrowave(t);
			if(current instanceof SetMediumMicrowave) 
				nextEvent = new SetHighMicrowave(t);
			if(current instanceof SetHighMicrowave) 
				nextEvent = new SetUnfreezMicrowave(t);
			if(current instanceof SetUnfreezMicrowave) 
				nextEvent = new SwitchOffMicrowave(t);
		}
		this.scheduleEvent(nextEvent);
	}
	
	protected Time computeTimeOfNextEvent(Time from) {
		assert from != null;
		
		double delay = Math.max(this.rg.nextGaussian(
				STEP_MEAN_DURATION, STEP_MEAN_DURATION / 2.0), 0.1);	
		Time t = from.add(new Duration(delay, this.getSimulatedTimeUnit()));
		
		return t;
	}
	
	protected Time computeTimeOfNextUsage(Time from) {
		assert from != null;
		
		double delay = Math.max(this.rg.nextGaussian(
				DELAY_MEAN_DURATION, DELAY_MEAN_DURATION / 10.0), 0.1);	
		Time t = from.add(new Duration(delay, this.getSimulatedTimeUnit()));
		
		return t;
	}
	
	@Override
	public void initialiseState(Time initialTime) {
		super.initialiseState(initialTime);
		this.rg.reSeedSecure();
		
		Time t = this.computeTimeOfNextEvent(this.getCurrentStateTime());
		this.scheduleEvent(new SwitchOnMicrowave(t));
		
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = 
				this.getCurrentStateTime().add(this.getNextTimeAdvance());
		
		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}
	
	@Override
	public ArrayList<EventI> output() {
		if(this.eventList.peek() != null)
			this.generateNextEvent();
		return super.output();
	}
	
	@Override
	public void endSimulation(Time endTime) {
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}
	
	@Override
	public void setSimulationRunParameters(
			Map<String, Serializable> simParams) throws MissingRunParameterException {
		super.setSimulationRunParameters(simParams);
		
		String stepName =
				ModelI.createRunParameterName(getURI(), MEAN_STEP_RPNAME);
		if (simParams.containsKey(stepName)) 
			STEP_MEAN_DURATION = (double)simParams.get(stepName);
		
		String delayName =
				ModelI.createRunParameterName(getURI(), MEAN_DELAY_RPNAME);
		if (simParams.containsKey(delayName)) 
			DELAY_MEAN_DURATION = (double)simParams.get(delayName);
		
	}
	
	@Override
	public SimulationReportI getFinalReport() {
		return null;
	}
}
