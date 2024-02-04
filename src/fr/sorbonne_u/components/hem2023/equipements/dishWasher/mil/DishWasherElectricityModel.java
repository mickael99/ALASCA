/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2023.HEM_ReportI;
import fr.sorbonne_u.components.hem2023.utils.Electricity;
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
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.events.*;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.events.DoNotHeat;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.events.Heat;

/**
 * @author Yukhoi
 *
 */
@ModelExternalEvents(imported = {SwitchOnDishWasher.class,
		SwitchOffDishWasher.class,
		SetPowerDishWasher.class,
		 Wash.class,
		 DoNotWash.class})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
@ModelExportedVariable(name = "currentWashingPower", type = Double.class)
//------------------------------------------------------------------------------
public class DishWasherElectricityModel extends AtomicHIOA {

	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	public static enum	State {
		/** washer is on but not heating.									*/
		ON,
		/** washer is on and washing.										*/
		WASHING,
		/** washer is off.													*/
		OFF
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String	URI = DishWasherElectricityModel.class.
															getSimpleName();

	/** power of the washer in watts.										*/
	public static double		NOT_WASHING_POWER = 22.0;
	/** max power of the washer in watts.										*/
	public static double		MAX_WASHING_POWER = 2500.0;
	/** nominal tension (in Volts) of the washer.							*/
	public static double		TENSION = 220.0;

	/** current state of the water washer.										*/
	protected State				currentState = State.OFF;

	protected boolean			consumptionHasChanged = false;

	/** total consumption of the washer during the simulation in kwh.		*/
	protected double			totalConsumption;
	
	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** the current washing power between 0 and
	 *  {@code HeaterElectricityModel.MAX_HEATING_POWER}.					*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentWashingPower =
														new Value<Double>(this);
	/** current intensity in amperes; intensity is power/tension.			*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity = new Value<Double>(this);
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a washer MIL model instance.
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
	public				DishWasherElectricityModel(
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
	 * set the state of the washer.
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
	{	//erreur ici
		State old = this.currentState;
		this.currentState = s;
		if (old != s) {
			this.consumptionHasChanged = true;					
		}
	}

	/**
	 * return the state of the washer.
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
	 * set the current heating power of the water heater to {@code newPower}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code newPower >= 0.0 && newPower <= MAX_HEATING_POWER}
	 * post	{@code getCurrentHeatingPower() == newPower}
	 * </pre>
	 *
	 * @param newPower	the new power in watts to be set on the water heater.
	 * @param t			time at which the new power is set.
	 */
	public void			setCurrentWashingPower(double newPower, Time t)
	{
		assert	newPower >= 0.0 &&
				newPower <= DishWasherElectricityModel.MAX_WASHING_POWER :
			new AssertionError(
					"Precondition violation: newPower >= 0.0 && "
					+ "newPower <= HeaterElectricityModel.MAX_WASHING_POWER,"
					+ " but newPower = " + newPower);

		double oldPower = this.currentWashingPower.getValue();
		this.currentWashingPower.setNewValue(newPower, t);
		if (newPower != oldPower) {
			this.consumptionHasChanged = true;
		}
	}
	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.currentState = State.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}
	
	@Override
	public boolean		useFixpointInitialiseVariables()
	{
		return true;
	}
	
	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables()
	{
		if (!this.currentIntensity.isInitialised() ||
								!this.currentWashingPower.isInitialised()) {
			// initially, the heater is off, so its consumption is zero.
			this.currentIntensity.initialise(0.0);
			this.currentWashingPower.initialise(MAX_WASHING_POWER);

			StringBuffer sb = new StringBuffer("initial new consumption: ");
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
	
	@Override
	public ArrayList<EventI>	output()
	{
		return null;
	}
	
	@Override
	public Duration		timeAdvance()
	{
		if (this.consumptionHasChanged) {
			// When the consumption has changed, an immediate (delay = 0.0)
			// internal transition must be made to update the electricity
			// consumption.
			this.consumptionHasChanged = false;
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			// As long as the state does not change, no internal transition
			// is made (delay = infinity).
			return Duration.INFINITY;
		}
	}
	
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		Time t = this.getCurrentStateTime();
		if (this.currentState == State.ON) {
			this.currentIntensity.setNewValue(
					DishWasherElectricityModel.NOT_WASHING_POWER/
					DishWasherElectricityModel.TENSION,
					t);
		} else if (this.currentState == State.WASHING) {
			System.out.println(this.getState());
			this.currentIntensity.setNewValue(
								this.currentWashingPower.getValue()/
								DishWasherElectricityModel.TENSION,
								t);
		} else {
			assert	this.currentState == State.OFF;
			this.currentIntensity.setNewValue(0.0, t);
		}

		StringBuffer sb = new StringBuffer("new consumption: ");
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
		// and for the wahser model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof DishWasherEventI;

		// compute the total consumption for the simulation report.
		this.totalConsumption +=
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
		System.out.println(this.getState());
	}
	
	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/** power of the heater in watts.										*/
	public static final String	NOT_WASHING_POWER_RUNPNAME = "NOT_WASHING_POWER";
	/** power of the heater in watts.										*/
	public static final String	MAX_WASHING_POWER_RUNPNAME = "MAX_WASHING_POWER";
	/** nominal tension (in Volts) of the heater.							*/
	public static final String	TENSION_RUNPNAME = "TENSION";
	
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String notWashingName =
			ModelI.createRunParameterName(getURI(), NOT_WASHING_POWER_RUNPNAME);
		if (simParams.containsKey(notWashingName)) {
			NOT_WASHING_POWER = (double) simParams.get(notWashingName);
		}
		String washingName =
			ModelI.createRunParameterName(getURI(), MAX_WASHING_POWER_RUNPNAME);
		if (simParams.containsKey(washingName)) {
			MAX_WASHING_POWER = (double) simParams.get(washingName);
		}
		String tensionName =
			ModelI.createRunParameterName(getURI(), TENSION_RUNPNAME);
		if (simParams.containsKey(tensionName)) {
			TENSION = (double) simParams.get(tensionName);
		}
	}
	
	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	public static class	DishWasherElectricityReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption; // in kwh


		public DishWasherElectricityReport(
			String modelURI,
			double totalConsumption
			)
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
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
			ret.append(this.totalConsumption);
			ret.append(".\n");
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}	
	}

	@Override
	public SimulationReportI	getFinalReport()
	{
		return new DishWasherElectricityReport(URI, this.totalConsumption);
	}


}
