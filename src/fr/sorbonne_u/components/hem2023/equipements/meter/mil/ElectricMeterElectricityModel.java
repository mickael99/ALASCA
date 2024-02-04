package fr.sorbonne_u.components.hem2023.equipements.meter.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2023.utils.Electricity;
import fr.sorbonne_u.components.hem2023.equipements.meter.ElectricMeter;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2023.HEM_ReportI;


public class ElectricMeterElectricityModel extends AtomicHIOA {
	private static final long serialVersionUID = 1L;
	/** URI for an instance model in MIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	MIL_URI = ElectricMeterElectricityModel.class.
													getSimpleName() + "-MIL";
	/** URI for an instance model in MIL real time simulations; works as
	 *  long as only one instance is created.										*/
	public static final String	MIL_RT_URI = ElectricMeterElectricityModel.class.
													getSimpleName() + "-MIL_RT";
	public static final double TENSION = 220.0;

	protected static final double STEP = 60.0/3600.0;	// 60 seconds
	protected final Duration evaluationStep;
	protected ElectricMeter						ownerComponent;

	protected ElectricMeterElectricityReport	finalReport;
	


	@ImportedVariable(type = Double.class)
	protected Value<Double>	currentMicrowaveIntensity;
	
	@ImportedVariable(type = Double.class)
	protected Value<Double>	currentFanIntensity;

	@ImportedVariable(type = Double.class)
	protected Value<Double>	currentDishWasherIntensity;
	
	@ImportedVariable(type = Double.class)
	protected Value<Double>	currentWaterHeaterIntensity;
	
	@ImportedVariable(type = Double.class)
	protected Value<Double>	currentSolarPannelIntensity;

	@InternalVariable(type = Double.class)
	protected final Value<Double> currentIntensity =
												new Value<Double>(this);
	@InternalVariable(type = Double.class)
	protected final Value<Double> currentPowerConsumption =
												new Value<Double>(this);
	@InternalVariable(type = Double.class)
	protected final Value<Double> currentPowerProduction =
												new Value<Double>(this);
	/** current total consumption of the house in kwh.						*/
	@InternalVariable(type = Double.class)
	protected final Value<Double>	currentCumulativeConsumption =
												new Value<Double>(this);
	@InternalVariable(type = Double.class)
	protected final Value<Double>	currentCumulativeProduction =
												new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public ElectricMeterElectricityModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.evaluationStep = new Duration(STEP, this.getSimulatedTimeUnit());
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	protected void		updateCumulativeConsumption(Duration d)
	{
		double c = this.currentCumulativeConsumption.getValue();
		c += Electricity.computeConsumption(
							d, TENSION*this.currentPowerConsumption.getValue());
		Time t = this.currentCumulativeConsumption.getTime().add(d);
		this.currentCumulativeConsumption.setNewValue(c, t);
	}
	
	protected void		updateCumulativeProduction(Duration d)
	{
		double c = this.currentCumulativeProduction.getValue();
		c += Electricity.computeConsumption(
							d, TENSION*this.currentCumulativeProduction.getValue());
		Time t = this.currentCumulativeProduction.getTime().add(d);
		this.currentCumulativeProduction.setNewValue(c, t);
	}
	
	/**
	 * compute the current total intensity.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return the current total intensity of electric consumption.
	 */
	protected double		computePowerConsumption()
	{
		// simple sum of all incoming intensities
		double i = this.currentFanIntensity.getValue()
				   + this.currentWaterHeaterIntensity.getValue();

		return i;
	}
	
	/**
	 * compute the current total intensity.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return the current total intensity of electric consumption.
	 */
	protected double		computePowerProduction()
	{
		// simple sum of all incoming intensities
		double i = this.currentSolarPannelIntensity.getValue();

		return i;
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public boolean useFixpointInitialiseVariables() {
		return true;
	}

	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables() {
		int justInitialised = 0;
		int notInitialisedYet = 0;

		if(!this.currentIntensity.isInitialised() &&
							this.currentMicrowaveIntensity.isInitialised() &&
							this.currentFanIntensity.isInitialised() &&
							this.currentDishWasherIntensity.isInitialised() &&
							this.currentWaterHeaterIntensity.isInitialised() &&
							this.currentSolarPannelIntensity.isInitialised()) {
			double i = this.computePowerConsumption();
			this.currentPowerConsumption.initialise(i);
			this.currentCumulativeConsumption.initialise(0.0);
			double p = this.computePowerProduction();
			this.currentPowerProduction.initialise(p);
			this.currentCumulativeProduction.initialise(0.0);
			justInitialised += 2;
		} 
		else if(!this.currentPowerConsumption.isInitialised()) 
			notInitialisedYet += 2;
		
		return new Pair<>(justInitialised, notInitialisedYet);
	}

	@Override
	public ArrayList<EventI> output() {
		return null;
	}

	public Duration timeAdvance() {
		return this.evaluationStep;
	}

	@Override
	public void	 userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		// update the current consumption since the last consumption update.
		// must be done before recomputing the instantaneous intensity.
		this.updateCumulativeConsumption(elapsedTime);
		this.updateCumulativeProduction(elapsedTime);
		// recompute the current total intensity
		double old = this.currentPowerConsumption.getValue();
		double i = this.computePowerConsumption();
		this.currentPowerConsumption.setNewValue(i, this.getCurrentStateTime());
		
		double old_p = this.currentPowerProduction.getValue();
		double i_p = this.computePowerProduction();
		this.currentPowerProduction.setNewValue(i_p, this.getCurrentStateTime());
		
		if (Math.abs(old - i) > 0.000001) {
			// Tracing
			StringBuffer message =
						new StringBuffer("current power consumption: ");
			message.append(this.currentPowerConsumption.getValue());
			message.append(" at ");
			message.append(this.getCurrentStateTime());
			message.append('\n');
			this.logMessage(message.toString());
		}
		
		if (Math.abs(old_p - i_p) > 0.000001) {
			// Tracing
			StringBuffer message =
						new StringBuffer("current power prodection: ");
			message.append(this.currentPowerProduction.getValue());
			message.append(" at ");
			message.append(this.getCurrentStateTime());
			message.append('\n');
			this.logMessage(message.toString());
		}
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		this.updateCumulativeConsumption(
						endTime.subtract(this.currentCumulativeConsumption.getTime()));
		
		this.updateCumulativeConsumption(
				endTime.subtract(this.currentCumulativeProduction.getTime()));

		this.finalReport = new ElectricMeterElectricityReport(
											this.getURI(),
											this.currentCumulativeConsumption.getValue(),
											this.currentCumulativeProduction.getValue());
		


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

		assert	simParams != null && !simParams.isEmpty() :
				new PreconditionException(
								"simParams != null && !simParams.isEmpty()");

		if (simParams.containsKey(
						AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			this.ownerComponent = 
				(ElectricMeter) simParams.get(
						AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME);
			this.getSimulationEngine().setLogger(
						AtomicSimulatorPlugin.createComponentLogger(simParams));
		}
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	public static class	ElectricMeterElectricityReport
	implements	SimulationReportI, HEM_ReportI {
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption;
		protected double 	totalProduction;

		public ElectricMeterElectricityReport(String modelURI, 
												double totalConsumption,
												double totalProduction) 
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
			this.totalProduction = totalProduction;
		}

		@Override
		public String getModelURI() {
			return this.modelURI;
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
			ret.append("and total production in kwh = ");
			ret.append(this.totalProduction);
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}		
	}

	@Override
	public SimulationReportI getFinalReport() {
		return this.finalReport;
	}

	




}
