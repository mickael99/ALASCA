package fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a basic
// household management systems as an example of a cyber-physical system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil.SolarPannelElectricityModel;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil.SolarPannelElectricityModel.State;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil.events.*;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>HairDryerStateModel</code> defines a simulation model
 * tracking the state changes on a hair dryer.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model receives event from the hair dryer component (corresponding to
 * calls to operations on the hair dryer in this component), keeps track of
 * the current state of the hair dryer in the simulation and then emits the
 * received events again towards another model simulating the electricity
 * consumption of the hair dryer given its current operating state (switched
 * on/off, high/low power mode).
 * </p>
 * <p>
 * This model becomes necessary in a SIL simulation of the household energy
 * management system because the electricity model must be put in the electric
 * meter component to share variables with other electricity models so this
 * state model will serve as a bridge between the models put in the hair dryer
 * component and its electricity model put in the electric meter component.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOnHairDryer},
 *   {@code SwitchOffHairDryer},
 *   {@code SetLowHairDryer},
 *   {@code SetHighHairDryer}</li>
 * <li>Exported events:
 *   {@code SwitchOnHairDryer},
 *   {@code SwitchOffHairDryer},
 *   {@code SetLowHairDryer},
 *   {@code SetHighHairDryer}</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables: none</li>
 * </ul>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2021-10-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(
	imported = {SwitchOnSolarPannel.class,SwitchOffSolarPannel.class},
	exported = {SwitchOnSolarPannel.class,SwitchOffSolarPannel.class}
	)
// -----------------------------------------------------------------------------
public class			SolarPannelStateModel
extends		AtomicModel
implements	SolarPannelOperationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for an instance model in MIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	MIL_URI = SolarPannelStateModel.class.
													getSimpleName() + "-MIL";
	/** URI for an instance model in MIL real time simulations; works as
	 *  long as only one instance is created.								*/
	public static final String	MIL_RT_URI = SolarPannelStateModel.class.
													getSimpleName() + "-MIL_RT";
	/** URI for an instance model in SIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	SIL_URI = SolarPannelStateModel.class.
													getSimpleName() + "-SIL";

	/** current state of the hair dryer.									*/
	protected State						currentState;
	/** last received event or null if none.								*/
	protected AbstractSolarPannelEvent	lastReceived;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a hair dryer state model instance.
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
	public				SolarPannelStateModel(
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

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.HairDryerOperationI#turnOn()
	 */
	@Override
	public void			turnOn()
	{
		if (this.currentState == SolarPannelElectricityModel.State.OFF) {
		}
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.HairDryerOperationI#turnOff()
	 */
	@Override
	public void			turnOff()
	{
		// a SwitchOff event can be executed when the state of the hair
		// dryer model is *not* in the state OFF
		if (this.currentState != SolarPannelElectricityModel.State.OFF) {
			// then put it in the state OFF
			this.currentState = SolarPannelElectricityModel.State.OFF;
		}
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.lastReceived = null;
		this.currentState = State.OFF;

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

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
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
		this.lastReceived = (AbstractSolarPannelEvent) currentEvents.get(0);

		// tracing
		StringBuffer message = new StringBuffer(this.uri);
		message.append(" executes the external event ");
		message.append(this.lastReceived);
		message.append('\n');
		this.logMessage(message.toString());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
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
// -----------------------------------------------------------------------------
