/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan.mil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.random.RandomDataGenerator;
import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.*;

/**
 * @author Yukhoi
 *
 */
public class FanUserModel extends AtomicES_Model {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** URI for an instance model; works as long as only one instance is
	 *  created.															*/
	public static final String	URI = FanUserModel.class.getSimpleName();

	/** time interval between event outputs in hours.						*/
	protected static double		STEP_MEAN_DURATION = 5.0/60.0; // 5 minutes
	/** time interval between hair dryer usages in hours.					*/
	protected static double		DELAY_MEAN_DURATION = 4.0;

	/**	the random number generator from common math library.				*/
	protected final RandomDataGenerator	rg ;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a fan user MIL model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do.</i>
	 */
	public				FanUserModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();
		this.getSimulationEngine().setLogger(new StandardLogger());
	}
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	protected void		generateNextEvent()
	{
		EventI current = this.eventList.peek();
		// compute the next event type given the current event
		ES_EventI nextEvent = null;
		if (current instanceof SwitchOffFan) {
			// compute the time of occurrence for the next hair dryer usage
			Time t2 = this.computeTimeOfNextUsage(current.getTimeOfOccurrence());
			nextEvent = new SwitchOnFan(t2);
		} else {
			// compute the time of occurrence for the next event
			Time t = this.computeTimeOfNextEvent(current.getTimeOfOccurrence());
			if (current instanceof SwitchOnFan) {
				nextEvent = new SetHighFan(t);
			} else if (current instanceof SetHighFan) {
				nextEvent = new SetLowFan(t);
			} else if (current instanceof SetMeddiumFan) {
				nextEvent = new SetLowFan(t);
			} else if (current instanceof SwitchOnMusicFan) {
				nextEvent = new SwitchOffMusicFan(t);
			} else if (current instanceof SwitchOffMusicFan) {
				nextEvent = new SwitchOffFan(t);
			}
		}
		// schedule the event to be executed by this model
		this.scheduleEvent(nextEvent);
	}
	
	protected Time		computeTimeOfNextEvent(Time from)
	{
		assert	from != null;

		// generate randomly the next time interval but force it to be
		// greater than 0 by returning at least 0.1 
		double delay = Math.max(this.rg.nextGaussian(STEP_MEAN_DURATION,
													 STEP_MEAN_DURATION/2.0),
								0.1);
		// compute the new time by adding the delay to from
		Time t = from.add(new Duration(delay, this.getSimulatedTimeUnit()));
		return t;
	}
	
	protected Time		computeTimeOfNextUsage(Time from)
	{
		assert	from != null;

		// generate randomly the next time interval but force it to be
		// greater than 0 by returning at least 0.1 
		double delay = Math.max(this.rg.nextGaussian(DELAY_MEAN_DURATION,
													 DELAY_MEAN_DURATION/10.0),
								0.1);
		// compute the new time by adding the delay to from
		Time t = from.add(new Duration(delay, this.getSimulatedTimeUnit()));
		return t;
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.rg.reSeedSecure();

		// compute the time of occurrence for the first event
		Time t = this.computeTimeOfNextEvent(this.getCurrentStateTime());
		// schedule the first event
		this.scheduleEvent(new SwitchOnFan(t));
		// re-initialisation of the time of occurrence of the next event
		// required here after adding a new event in the schedule.
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.getNextTimeAdvance());

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}
	
	@Override
	public ArrayList<EventI>	output()
	{
		// generate and schedule the next event
		if (this.eventList.peek() != null) {
			this.generateNextEvent();
		}
		// this will extract the next event from the event list and emit it
		return super.output();
	}
	
	@Override
	public void			endSimulation(Time endTime)
	{
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}
	
	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/** run parameter name for {@code STEP_MEAN_DURATION}.					*/
	public static final String		MEAN_STEP_RPNAME = "STEP_MEAN_DURATION";
	/** run parameter name for {@code STEP_MEAN_DURATION}.					*/
	public static final String		MEAN_DELAY_RPNAME = "STEP_MEAN_DURATION";
	
	@Override
	public void			setSimulationRunParameters(
		Map<String, Serializable> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String stepName =
				ModelI.createRunParameterName(getURI(), MEAN_STEP_RPNAME);
		if (simParams.containsKey(stepName)) {
			STEP_MEAN_DURATION = (double) simParams.get(stepName);
		}
		String delayName =
				ModelI.createRunParameterName(getURI(), MEAN_DELAY_RPNAME);
		if (simParams.containsKey(delayName)) {
			DELAY_MEAN_DURATION = (double) simParams.get(delayName);
		}
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	@Override
	public SimulationReportI	getFinalReport()
	{
		return null;
	}
}
