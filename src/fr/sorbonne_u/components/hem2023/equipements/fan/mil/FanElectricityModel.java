/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan.mil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2023.utils.Electricity;
import fr.sorbonne_u.components.hem2023.HEM_ReportI;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.*;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

/**
 * @author Yukhoi
 *
 */
public class FanElectricityModel extends AtomicHIOA {

	
	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	public static enum State {
		OFF,
		LOW,			
		HIGH,
		MEDDIUM
	}
	
	public static enum MusicState {
		OFF,
		ON
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;

	/** URI for an instance model; works as long as only one instance is
	 *  created.															*/
	public static final String		URI = FanElectricityModel.class.
																getSimpleName();

	public static double			LOW_MODE_CONSUMPTION = 20.0; // Watts
	public static double			MEDDIUM_MODE_CONSUMPTION = 50.0; // Watts
	public static double			HIGH_MODE_CONSUMPTION = 100.0; // Watts
	public static double			MUSIC_MODE_CONSUMPTION = 5.0; // Watts
	public static double			TENSION = 220.0; // Volts

	protected State					currentState = State.OFF;
	protected MusicState			currentMusicState = MusicState.OFF;

	/** true when the electricity consumption of the dryer has changed
	 *  after executing an external event; the external event changes the
	 *  value of <code>currentState</code> and then an internal transition
	 *  will be triggered by putting through in this variable which will
	 *  update the variable <code>currentIntensity</code>.					*/
	protected boolean				consumptionHasChanged = false;

	/** total consumption of the hair dryer during the simulation in kwh.	*/
	protected double				totalConsumption;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** current intensity in amperes; intensity is power/tension.			*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	public FanElectricityModel(
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
	
	public void	setState(State s)
	{
		this.currentState = s;
	}
	
	public State getState()
	{
		return this.currentState;
	}
	
	public void	setMusicState(MusicState s)
	{
		this.currentMusicState = s;
	}
	
	public MusicState getMusicState()
	{
		return this.currentMusicState;
	}
	
	public void	toggleConsumptionHasChanged()
	{
		if (this.consumptionHasChanged) {
			this.consumptionHasChanged = false;
		} else {
			this.consumptionHasChanged = true;
		}
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void	initialiseState(Time startTime)
	{
		super.initialiseState(startTime);

		// initially the hair dryer is off and its electricity consumption is
		// not about to change.
		this.currentState = State.OFF;
		this.currentMusicState = MusicState.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}
	
	@Override
	public void	initialiseVariables()
	{
		super.initialiseVariables();

		// initially, the hair dryer is off, so its consumption is zero.
		this.currentIntensity.initialise(0.0);
	}
	
	@Override
	public ArrayList<EventI>	output()
	{
		return null;
	}
	
	@Override
	public Duration	timeAdvance()
	{
		// to trigger an internal transition after an external transition, the
		// variable consumptionHasChanged is set to true, hence when it is true
		// return a zero delay otherwise return an infinite delay (no internal
		// transition expected)
		if (this.consumptionHasChanged) {
			// after triggering the internal transition, toggle the boolean
			// to prepare for the next internal transition.
			this.toggleConsumptionHasChanged();
			return new Duration(0.0, this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	@Override
	public void	userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		// set the current electricity consumption from the current state
		Time t = this.getCurrentStateTime();
		switch (this.currentState)
		{
			case OFF : this.currentIntensity.setNewValue(0.0, t); break;
			case LOW :
				switch (this.currentMusicState)
				{
					case ON: 
						this.currentIntensity.
							setNewValue((LOW_MODE_CONSUMPTION+MUSIC_MODE_CONSUMPTION)
									/TENSION, t);
						break;
					case OFF:
						this.currentIntensity.
							setNewValue(LOW_MODE_CONSUMPTION/TENSION, t);
				}
				break;
			case HIGH :
				switch (this.currentMusicState)
				{
					case ON: 
						this.currentIntensity.
							setNewValue((HIGH_MODE_CONSUMPTION + MUSIC_MODE_CONSUMPTION)
									/TENSION, t);
						break;
					case OFF:
						this.currentIntensity.
						setNewValue(HIGH_MODE_CONSUMPTION/TENSION, t);
				}
				break;
			case MEDDIUM :
				switch (this.currentMusicState)
				{
					case ON: 
						this.currentIntensity.
							setNewValue((MEDDIUM_MODE_CONSUMPTION + MUSIC_MODE_CONSUMPTION)
									/TENSION, t);
						break;
					case OFF:
						this.currentIntensity.
							setNewValue(MEDDIUM_MODE_CONSUMPTION/TENSION, t);
				}
		}
		// Tracing
				StringBuffer message =
						new StringBuffer("executes an internal transition ");
				message.append("with current consumption ");
				message.append(this.currentIntensity.getValue());
				message.append(" at ");
				message.append(this.currentIntensity.getTime());
				message.append(".\n");
				this.logMessage(message.toString());
	}
	
	@Override
	public void	userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of currently received external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the current hair dryer model, there must be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);

		// compute the total consumption (in kwh) for the simulation report.
		this.totalConsumption +=
				Electricity.computeConsumption(
									elapsedTime,
									TENSION*this.currentIntensity.getValue());

		// Tracing
		StringBuffer message =
				new StringBuffer("executes an external transition ");
		message.append(ce.toString());
		message.append(")\n");
		this.logMessage(message.toString());

		assert	ce instanceof AbstractFanEvent;

		ce.executeOn(this);
	}

	@Override
	public void	endSimulation(Time endTime)
	{
		Duration d = endTime.subtract(this.getCurrentStateTime());
		this.totalConsumption +=
				Electricity.computeConsumption(
									d,
									TENSION*this.currentIntensity.getValue());

		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}
	
	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/** run parameter name for {@code LOW_MODE_CONSUMPTION}.				*/
	public static final String		LOW_MODE_CONSUMPTION_RPNAME =
												URI + ":LOW_MODE_CONSUMPTION";
	/** run parameter name for {@code HIGH_MODE_CONSUMPTION}.				*/
	public static final String		HIGH_MODE_CONSUMPTION_RPNAME =
												URI + ":HIGH_MODE_CONSUMPTION";
	/** run parameter name for {@code MEDDIUM_MODE_CONSUMPTION}.				*/
	public static final String		MEDDIUM_MODE_CONSUMPTION_RPNAME =
												URI + ":MEDDIUM_MODE_CONSUMPTION";
	/** run parameter name for {@code MUSIC_MODE_CONSUMPTION}.				*/
	public static final String		MUSIC_MODE_CONSUMPTION_RPNAME =
												URI + ":MUSIC_MODE_CONSUMPTION";
	/** run parameter name for {@code TENSION}.								*/
	public static final String		TENSION_RPNAME = URI + ":TENSION";
	
	@Override
	public void			setSimulationRunParameters(
		Map<String, Serializable> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String lowName =
			ModelI.createRunParameterName(getURI(),
										  LOW_MODE_CONSUMPTION_RPNAME);
		if (simParams.containsKey(lowName)) {
			LOW_MODE_CONSUMPTION = (double) simParams.get(lowName);
		}
		String highName =
			ModelI.createRunParameterName(getURI(),
										  HIGH_MODE_CONSUMPTION_RPNAME);
		if (simParams.containsKey(highName)) {
			HIGH_MODE_CONSUMPTION = (double) simParams.get(highName);
		}
		String meddiumName =
				ModelI.createRunParameterName(getURI(),
											  MEDDIUM_MODE_CONSUMPTION_RPNAME);
			if (simParams.containsKey(meddiumName)) {
				MEDDIUM_MODE_CONSUMPTION = (double) simParams.get(meddiumName);
			}
		String musicName =
				ModelI.createRunParameterName(getURI(),
											  MUSIC_MODE_CONSUMPTION_RPNAME);
			if (simParams.containsKey(musicName)) {
				MUSIC_MODE_CONSUMPTION = (double) simParams.get(musicName);
			}
		String tensionName =
				ModelI.createRunParameterName(getURI(), TENSION_RPNAME);
		if (simParams.containsKey(tensionName)) {
			TENSION = (double) simParams.get(tensionName);
		}
	}
	
	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------
	
	public static class	FanElectricityReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption; // in kwh

		public FanElectricityReport(
			String modelURI,
			double totalConsumption
			)
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
		}

		@Override
		public String getModelURI()
		{
			return null;
		}

		@Override
		public String printout(String indent)
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
		return new FanElectricityReport(URI, this.totalConsumption);
	}
}
//---------------------------------------------------------------------------