/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.microwave.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.MicrowaveElectricityModel.Mode;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.MicrowaveElectricityModel.State;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.AbstractMicrowaveEvent;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SetHighMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SetLowMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SetMediumMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SetUnfreezeMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SwitchOffMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SwitchOnMicrowave;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

/**
 * @author Yukhoi
 *
 */
//------------------------------------------------------------
@ModelExternalEvents(
		imported = {SetHighMicrowave.class,
				 SetLowMicrowave.class,
				 SetMediumMicrowave.class,
				 SetUnfreezeMicrowave.class,
				 SwitchOnMicrowave.class,
				 SwitchOffMicrowave.class},
		exported = {SetHighMicrowave.class,
				 SetLowMicrowave.class,
				 SetMediumMicrowave.class,
				 SetUnfreezeMicrowave.class,
				 SwitchOnMicrowave.class,
				 SwitchOffMicrowave.class}
		)
//------------------------------------------------------------
public class MicrowaveStateModel
	extends AtomicHIOA
	implements MicrowaveOperationI
{

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for an instance model in MIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	MIL_URI = MicrowaveStateModel.class.
													getSimpleName() + "-MIL";
	/** URI for an instance model in MIL real time simulations; works as
	 *  long as only one instance is created.								*/
	public static final String	MIL_RT_URI = MicrowaveStateModel.class.
													getSimpleName() + "-MIL_RT";
	/** URI for an instance model in SIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	SIL_URI = MicrowaveStateModel.class.
													getSimpleName() + "-SIL";

	protected State						currentState;
	protected Mode 						currentMode;
	protected AbstractMicrowaveEvent	lastReceived;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	public				MicrowaveStateModel(
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
	public void			turnOn()
	{
		if (this.currentState == MicrowaveElectricityModel.State.OFF) {
			this.currentMode = MicrowaveElectricityModel.Mode.LOW;
		}
	}
	
	@Override
	public void			turnOff()
	{
		if (this.currentState != MicrowaveElectricityModel.State.OFF) {
			this.currentState = MicrowaveElectricityModel.State.OFF;
		}
	}
	
	@Override
	public void			setHigh()
	{
		if (this.currentMode != MicrowaveElectricityModel.Mode.HIGH) {
			this.currentMode = MicrowaveElectricityModel.Mode.HIGH;
		}
	}
	
	@Override
	public void 		setLow()
	{
		if (this.currentMode != MicrowaveElectricityModel.Mode.LOW) {
			this.currentMode = MicrowaveElectricityModel.Mode.LOW;
		}
	}
	
	@Override
	public void			setMeddium()
	{
		if (this.currentMode != MicrowaveElectricityModel.Mode.MEDDIUM) {
			this.currentMode = MicrowaveElectricityModel.Mode.MEDDIUM;
		}
	}
	
	@Override
	public void			setUnfreeze()
	{
		if (this.currentMode != MicrowaveElectricityModel.Mode.UNFREEZE) {
			this.currentMode = MicrowaveElectricityModel.Mode.UNFREEZE;
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
		this.currentMode = Mode.LOW;

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

		assert	currentEvents != null && currentEvents.size() == 1;

		// this will trigger an internal transition by the fact that
		// lastReceived will not be null; the internal transition does nothing
		// on the model state except to put lastReceived to null again, but
		// this will also trigger output and the sending of the event to
		// the electricity model to also change its state
		this.lastReceived = (AbstractMicrowaveEvent) currentEvents.get(0);

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

	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		// this gets the reference on the owner component which is required
		// to have simulation models able to make the component perform some
		// operations or tasks or to get the value of variables held by the
		// component when necessary.
		if (simParams.containsKey(
						AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			// by the following, all of the logging will appear in the owner
			// component logger
			this.getSimulationEngine().setLogger(
						AtomicSimulatorPlugin.createComponentLogger(simParams));
		}
	}

}
