package fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
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
import fr.sorbonne_u.components.hem2023.HEM_ReportI;
import fr.sorbonne_u.components.hem2023.utils.Electricity;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil.events.*;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.events.WaterHeatingEventI;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>HeaterElectricityModel</code> defines a simulation model
 * for the electricity consumption of the heater.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The electric power consumption (in amperes) depends upon the state and the
 * current power level i.e., {@code State.OFF => consumption == 0.0},
 * {@code State.ON => consumption == NOT_HEATING_POWER} and
 * {@code State.HEATING => consumption >= NOT_HEATING_POWER && consumption <= MAX_HEATING_POWER}).
 * The state of the heater is modified by the reception of external events
 * ({@code SwitchOnHeater}, {@code SwitchOffHeater}, {@code Heat} and
 * {@code DoNotHeat}). The power level is set through the external event
 * {@code SetPowerHeater} that has a parameter defining the required power
 * level. The electric power consumption is stored in the exported variable
 * {@code currentIntensity}.
 * </p>
 * <p>
 * Initially, the mode is in state {@code State.OFF} and the electric power
 * consumption at 0.0.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOnHeater},
 *   {@code SwitchOffHeater},
 *   {@code SetPowerHeater},
 *   {@code Heat},
 *   {@code DoNotHeat}</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables:
 *   name = {@code currentIntensity}, type = {@code Double}</li>
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
 * invariant	{@code NOT_HEATING_POWER >= 0.0}
 * invariant	{@code MAX_HEATING_POWER > NOT_HEATING_POWER}
 * invariant	{@code TENSION > 0.0}
 * </pre>
 * 
 * <p>Created on : 2023-09-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */

@ModelExternalEvents(imported = {SwitchOnSolarPanel.class,
		 SwitchOffSolarPanel.class})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
@ModelExportedVariable(name = "currentHeatingPower", type = Double.class)
//-----------------------------------------------------------------------------
public class			SolarPanelElectricityModel
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>State</code> defines the state in which the
	 * heater can be from the electric power consumption perspective.
	 *
	 * <p>Created on : 2021-09-24</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static enum	State {
		/** heater is on and heating.										*/
		ON,
		/** heater is off.													*/
		OFF
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String	URI = SolarPanelElectricityModel.class.
															getSimpleName();

	/** power of the heater in watts.										*/
	public static double		NOT_PRODUCING_POWER = 0.0;
	/** max power of the heater in watts.										*/
	public static double		MAX_PRODUCING_POWER = 2000.0;
	/** nominal tension (in Volts) of the heater.							*/
	public static double		TENSION = 220.0;

	/** current state of the heater.										*/
	protected State				currentState = State.OFF;
	/** true when the electricity consumption of the heater has changed
	 *  after executing an external event; the external event changes the
	 *  value of <code>currentState</code> and then an internal transition
	 *  will be triggered by putting through in this variable which will
	 *  update the variable <code>currentIntensity</code>.					*/
	protected boolean			productionHasChanged = false;

	/** total consumption of the heater during the simulation in kwh.		*/
	protected double			totalProduction;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** the current heating power between 0 and
	 *  {@code HeaterElectricityModel.MAX_HEATING_POWER}.					*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentProducingPower =
														new Value<Double>(this);
	/** current intensity in amperes; intensity is power/tension.			*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a heater MIL model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition
	 * post	{@code true}	// no more postcondition
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do</i>.
	 */
	public				SolarPanelElectricityModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * set the state of the heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param s		the new state.
	 * @param t		time at which the state {@code s} is set.
	 */
	public void			setState(State s, Time t)
	{
		State old = this.currentState;
		this.currentState = s;
		if (old != s) {
			this.productionHasChanged = true;					
		}
	}

	/**
	 * return the state of the heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the current state.
	 */
	public State		getState()
	{
		return this.currentState;
	}

	/**
	 * set the current heating power of the heater to {@code newPower}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code newPower >= 0.0 && newPower <= MAX_HEATING_POWER}
	 * post	{@code getCurrentHeatingPower() == newPower}
	 * </pre>
	 *
	 * @param newPower	the new power in watts to be set on the heater.
	 * @param t			time at which the new power is set.
	 */
	public void			setCurrentProducingPower(double newPower, Time t)
	{
		assert	newPower >= 0.0 &&
				newPower <= SolarPanelElectricityModel.MAX_PRODUCING_POWER :
			new AssertionError(
					"Precondition violation: newPower >= 0.0 && "
					+ "newPower <= HeaterElectricityModel.MAX_PRODUCING_POWER,"
					+ " but newPower = " + newPower);

		double oldPower = this.currentProducingPower.getValue();
		this.currentProducingPower.setNewValue(newPower, t);
		if (newPower != oldPower) {
			this.productionHasChanged = true;
		}
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.currentState = State.OFF;
		this.productionHasChanged = false;
		this.totalProduction = 0.0;

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean		useFixpointInitialiseVariables()
	{
		return true;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables()
	{
		if (!this.currentIntensity.isInitialised() ||
								!this.currentProducingPower.isInitialised()) {
			// initially, the heater is off, so its consumption is zero.
			this.currentIntensity.initialise(0.0);
			this.currentProducingPower.initialise(MAX_PRODUCING_POWER);

			StringBuffer sb = new StringBuffer("new production: ");
			sb.append(this.currentIntensity.getValue());
			sb.append(" amperes at ");
			sb.append(this.currentIntensity.getTime());
			sb.append(" seconds.\n");
			this.logMessage(sb.toString());
			return new Pair<>(2, 0);
		} else {
			return new Pair<>(0, 0);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.productionHasChanged) {
			// When the consumption has changed, an immediate (delay = 0.0)
			// internal transition must be made to update the electricity
			// consumption.
			this.productionHasChanged = false;
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			// As long as the state does not change, no internal transition
			// is made (delay = infinity).
			return Duration.INFINITY;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		Time t = this.getCurrentStateTime();
		if (this.currentState == State.ON) {
			this.currentIntensity.setNewValue(
								this.currentProducingPower.getValue()/
								SolarPanelElectricityModel.TENSION,
								t);
		} else {
			assert	this.currentState == State.OFF;
			this.currentIntensity.setNewValue(0.0, t);
		}

		StringBuffer sb = new StringBuffer("new production: ");
		sb.append(this.currentIntensity.getValue());
		sb.append(" amperes at ");
		sb.append(this.currentIntensity.getTime());
		sb.append(" seconds.\n");
		this.logMessage(sb.toString());
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of current external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the heater model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof WaterHeatingEventI;

		// compute the total consumption for the simulation report.
		this.totalProduction +=
				Electricity.computeConsumption(
									elapsedTime,
									TENSION*this.currentIntensity.getValue());

		StringBuffer sb = new StringBuffer("execute the external event: ");
		sb.append(ce.eventAsString());
		sb.append(".\n");
		this.logMessage(sb.toString());

		// the next call will update the current state of the heater and if
		// this state has changed, it put the boolean consumptionHasChanged
		// at true, which in turn will trigger an immediate internal transition
		// to update the current intensity of the heater electricity
		// consumption.
		ce.executeOn(this);
	}
	
	

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		Duration d = endTime.subtract(this.getCurrentStateTime());
		this.totalProduction +=
				Electricity.computeConsumption(
									d,
									TENSION*this.currentIntensity.getValue());

		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/** power of the heater in watts.										*/
	public static final String	NOT_HEATING_POWER_RUNPNAME = "NOT_HEATING_POWER";
	/** power of the heater in watts.										*/
	public static final String	MAX_HEATING_POWER_RUNPNAME = "MAX_HEATING_POWER";
	/** nominal tension (in Volts) of the heater.							*/
	public static final String	TENSION_RUNPNAME = "TENSION";


	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * The class <code>HeaterElectricityReport</code> implements the
	 * simulation report for the <code>HeaterElectricityModel</code>.
	 *
	 * <p><strong>Description</strong></p>
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
	 * <p>Created on : 2023-09-29</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class		SolarPanelElectricityReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalProduction; // in kwh


		public			SolarPanelElectricityReport(
			String modelURI,
			double totalConsumption
			)
		{
			super();
			this.modelURI = modelURI;
			this.totalProduction = totalConsumption;
		}

		@Override
		public String	getModelURI()
		{
			return this.modelURI;
		}

		@Override
		public String	printout(String indent)
		{
			StringBuffer ret = new StringBuffer(indent);
			ret.append("---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			ret.append("total consumption in kwh = ");
			ret.append(this.totalProduction);
			ret.append(".\n");
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}		
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return new SolarPanelElectricityReport(URI, this.totalProduction);
	}
}
// -----------------------------------------------------------------------------
