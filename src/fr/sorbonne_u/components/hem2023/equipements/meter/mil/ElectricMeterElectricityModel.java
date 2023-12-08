package fr.sorbonne_u.components.hem2023.equipements.meter.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2023.utils.Electricity;
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
import fr.sorbonne_u.components.hem2023.HEM_ReportI;


public class ElectricMeterElectricityModel extends AtomicHIOA {
	private static final long serialVersionUID = 1L;
	public static final String URI = ElectricMeterElectricityModel.class.
																getSimpleName();
	public static final double TENSION = 220.0;

	protected static final double STEP = 60.0/3600.0;	// 60 seconds
	protected final Duration evaluationStep;

	protected ElectricMeterElectricityReport	finalReport;

	@ImportedVariable(type = Double.class)
	protected Value<Double>	currentMicrowaveIntensity;
	
	@ImportedVariable(type = Double.class)
	protected Value<Double>	currentFanIntensity;

	@ImportedVariable(type = Double.class)
	protected Value<Double>	currentDishWasherIntensity;
	
	@ImportedVariable(type = Double.class)
	protected Value<Double>	currentWaterHeaterIntensity;

	@InternalVariable(type = Double.class)
	protected final Value<Double> currentIntensity =
												new Value<Double>(this);
	@InternalVariable(type = Double.class)
	protected final Value<Double> currentConsumption =
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
	
	protected void updateConsumption(Duration d) {
		double c = this.currentConsumption.getValue();
		c += Electricity.computeConsumption(
								d, TENSION * this.currentIntensity.getValue());
		Time t = this.currentConsumption.getTime().add(d);
		this.currentConsumption.setNewValue(c, t);
	}

	protected double computeTotalIntensity() {
		double i = this.currentMicrowaveIntensity.getValue() +
				   this.currentFanIntensity.getValue() + 
				   this.currentDishWasherIntensity.getValue() +
				   this.currentWaterHeaterIntensity.getValue();

		if (this.currentIntensity.isInitialised()) {
			StringBuffer message = new StringBuffer("current total consumption: ");
			message.append(this.currentIntensity.getValue());
			message.append(" at ");
			message.append(this.getCurrentStateTime());
			message.append('\n');
			this.logMessage(message.toString());
		}

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
							this.currentWaterHeaterIntensity.isInitialised()) {
			double i = this.computeTotalIntensity();
			this.currentIntensity.initialise(i);
			this.currentConsumption.initialise(0.0);
			justInitialised += 2;
		} 
		else if(!this.currentIntensity.isInitialised()) 
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

		this.updateConsumption(elapsedTime);
		double i = this.computeTotalIntensity();
		this.currentIntensity.setNewValue(i, this.getCurrentStateTime());
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		this.updateConsumption(
						endTime.subtract(this.currentConsumption.getTime()));

		this.finalReport = new ElectricMeterElectricityReport(
											URI,
											this.currentConsumption.getValue());

		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	public static class	ElectricMeterElectricityReport
	implements	SimulationReportI, HEM_ReportI {
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption;

		public ElectricMeterElectricityReport(String modelURI, double totalConsumption) {
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
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
