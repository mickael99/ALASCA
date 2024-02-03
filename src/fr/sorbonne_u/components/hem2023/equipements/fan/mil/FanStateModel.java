/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan.mil;

import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.*;


import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanElectricityModel.MusicState;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanElectricityModel.State;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

/**
 * @author Yukhoi
 *
 */
@ModelExternalEvents(
		imported = {SwitchOnFan.class,SwitchOffFan.class,
					SetLowFan.class,SetHighFan.class,
					SetMeddiumFan.class, SwitchOnMusicFan.class,
					SwitchOffMusicFan.class},
		exported = {SwitchOnFan.class,SwitchOffFan.class,
					SetLowFan.class,SetHighFan.class,
					SetMeddiumFan.class, SwitchOnMusicFan.class,
					SwitchOffMusicFan.class}
		)
public class FanStateModel extends AtomicModel implements FanOperationI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for an instance model in MIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	MIL_URI = FanStateModel.class.
													getSimpleName() + "-MIL";
	/** URI for an instance model in MIL real time simulations; works as
	 *  long as only one instance is created.								*/
	public static final String	MIL_RT_URI = FanStateModel.class.
													getSimpleName() + "-MIL_RT";
	/** URI for an instance model in SIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	SIL_URI = FanStateModel.class.
													getSimpleName() + "-SIL";

	/** current state of the fan.									*/
	protected State						currentState;
	protected MusicState				currentMusicState;
	/** last received event or null if none.								*/
	protected AbstractFanEvent	lastReceived;
	
	/**
	 * create a fan state model instance.
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
	 * @throws Exception		<i>to do</i>.
	 */
	public				FanStateModel(
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

	@Override
	public void turnOn() {
		if (this.currentState == FanElectricityModel.State.OFF) {
			// then put it in the state LOW
			this.currentState = FanElectricityModel.State.LOW;
			this.currentMusicState = FanElectricityModel.MusicState.OFF;
		}
	}

	@Override
	public void turnOff() {
		// a SwitchOff event can be executed when the state of the 
		// fan model is *not* in the state OFF
		if (this.currentState != FanElectricityModel.State.OFF) {
			// then put it in the state OFF
			this.currentState = FanElectricityModel.State.OFF;
			this.currentMusicState = FanElectricityModel.MusicState.OFF;
		}
	}

	@Override
	public void setHigh() {
		// a SetHigh event can only be executed when the state of the
		// fan model is not in the state HIGH
		if (this.currentState != FanElectricityModel.State.HIGH) {
			// then put it in the state HIGH
			this.currentState = FanElectricityModel.State.HIGH;
		}
	}

	@Override
	public void setLow() {
		// a SetHigh event can only be executed when the state of the
		// fan model is not in the state LOW
		if (this.currentState != FanElectricityModel.State.LOW) {
			// then put it in the state LOW
			this.currentState = FanElectricityModel.State.LOW;
		}
	}

	@Override
	public void setMeddium() {
		// a SetHigh event can only be executed when the state of the
		// fan model is in the state MEDDIUM
		if (this.currentState != FanElectricityModel.State.MEDDIUM) {
			// then put it in the state MEDDIUM
			this.currentState = FanElectricityModel.State.MEDDIUM;
		}
	}

	@Override
	public void turnOnMusic() {
		// a turnOnMusic event can only be executed when the music state of the
		// fan model is in the state OFF
		if (this.currentMusicState == FanElectricityModel.MusicState.OFF) {
			this.currentMusicState = FanElectricityModel.MusicState.ON;
		}
	}

	@Override
	public void turnOffMusic() {
		// a turnOnMusic event can only be executed when the music state of the
		// fan model is in the state ON
		if (this.currentMusicState == FanElectricityModel.MusicState.ON) {
			this.currentMusicState = FanElectricityModel.MusicState.OFF;
		}
	}
	
	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.lastReceived = null;
		this.currentState = State.OFF;
		this.currentMusicState = MusicState.OFF;

		this.getSimulationEngine() .toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}
	
	@Override
	public ArrayList<EventI>	output()
	{
		assert	this.lastReceived != null;

		ArrayList<EventI> ret = new ArrayList<EventI>();
		ret.add(this.lastReceived);
		this.lastReceived = null;
		return ret;
	}


	@Override
	public Duration		timeAdvance()
	{
		if (this.lastReceived != null) {
			// trigger an immediate internal transition
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			// wait until the next external event that will trigger an internal
			// transition
			return Duration.INFINITY;
		}
	}
	
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of current external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the hair dryer model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		// this will trigger an internal transition by the fact that
		// lastReceived will not be null; the internal transition does nothing
		// on the model state except to put lastReceived to null again, but
		// this will also trigger output and the sending of the event to
		// the electricity model to also change its state
		this.lastReceived = (AbstractFanEvent) currentEvents.get(0);

		// tracing
		StringBuffer message = new StringBuffer(this.uri);
		message.append(" executes the external event ");
		message.append(this.lastReceived);
		message.append('\n');
		this.logMessage(message.toString());
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


}
