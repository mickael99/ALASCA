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
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2023.HEM_ReportI;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanElectricityModel;
import fr.sorbonne_u.components.hem2023.utils.Electricity;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
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

@ModelImportedVariable(name = "currentSolarIlluminance", type = Double.class)
@ModelExportedVariable(name = "currentProducingPower", type = Double.class)
//-----------------------------------------------------------------------------
public class			SolarPannelElectricityModel
extends		AtomicHIOA
{
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
	/** URI for an instance model in MIL simulations; works as long as
	 *   only one instance is created.										*/
	public static final String	MIL_URI = SolarPannelElectricityModel.class.
													getSimpleName() + "-MIL";
	/** URI for an instance model in MIL real time simulations; works as
	 *  long as  only one instance is created.								*/
	public static final String	MIL_RT_URI = SolarPannelElectricityModel.class.
													getSimpleName() + "-MIL_RT";
	/** URI for an instance model in SIL simulations; works as long as
	 *   only one instance is created.										*/
	public static final String	SIL_URI = SolarPannelElectricityModel.class.
													getSimpleName() + "-SIL";

	/** power of the solar pannel in watts.										*/
	public static double		NOT_PRODUCING_POWER = 0.0;
	/** max power of the solar pannel in watts.										*/
	public static double		MAX_PRODUCING_POWER = 500.0;
	/** nominal tension (in Volts) of the heater.							*/
	public static double		TENSION = 220.0;


	/** total production of the solar pannel during the simulation in kwh.		*/
	protected double			totalProduction;
	
	/** integration step as a duration, including the time unit.				*/
	protected final Duration integrationStep;
	/** integration step for the differential equation(assumed in hours).		*/
	protected static double	STEP = 60.0/3600.0;	// 60 seconds 
	
	/** size of the solar panel in m^2 */
	protected final double SIZE_SOLAR_PANEL = 2; 
	
	/** coefficient to turn the illuminance (lux) to power density(W/m^2)*/
	protected final double EFFICIENCY_COEFFICIENT = 0.02;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** the current producing power between 0 and
	 *  {@code HeaterElectricityModel.MAX_HEATING_POWER}.					*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentProducingPower =
														new Value<Double>(this);
	/** current intensity in amperes; intensity is power/tension.			*/
	@ImportedVariable(type = Double.class)
	protected final Value<Double>	currentSolarIlluminance = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a solar pannel MIL model instance.
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
	public SolarPannelElectricityModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			AtomicSimulatorI simulationEngine
			) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.integrationStep = new Duration(STEP, simulatedTimeUnit);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	/**
	 * return the total power produced of the Solar Panel.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	total power produced.
	 */
	public double getTotalPowerProduced(){
		return this.totalProduction;
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
		if (!this.currentProducingPower.isInitialised()) {
			// initially, the Solar Panel starts with 0 production.
			this.currentProducingPower.initialise(0.0);

			StringBuffer sb = new StringBuffer("new production: ");
			sb.append(this.currentProducingPower.getValue());
			sb.append(" amperes at ");
			sb.append(this.currentProducingPower.getTime());
			sb.append(" seconds.\n");
			this.logMessage(sb.toString());
			return new Pair<>(1, 0); 
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
		return this.integrationStep;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		// Formula: total power (Wh) = illuminance(lux) * efficiency coefficient * surface (m^2) * period(h)
		double currentPowerProduction = (this.currentSolarIlluminance.getValue().doubleValue()) *EFFICIENCY_COEFFICIENT *SIZE_SOLAR_PANEL;
		this.totalProduction += currentPowerProduction*elapsedTime.getSimulatedDuration();
		this.currentProducingPower.setNewValue(currentPowerProduction*elapsedTime.getSimulatedDuration(), this.currentSolarIlluminance.getTime());

		// Tracing
		StringBuffer message1 = new StringBuffer();	
		message1.append("Current power production: ");
		message1.append((Math.round(currentPowerProduction*elapsedTime.getSimulatedDuration() * 100.0) / 100.0) + " Wh");
		message1.append(" at " + this.currentSolarIlluminance.getTime());
		message1.append("\n");
		this.logMessage(message1.toString());
		
		StringBuffer message = new StringBuffer();	
		message.append("Total power production: ");
		message.append((Math.round(totalProduction * 100.0) / 100.0) + " Wh");
		message.append(" at " + this.currentSolarIlluminance.getTime());
		message.append("\n");
		this.logMessage(message.toString());


		super.userDefinedInternalTransition(elapsedTime);
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
									TENSION*this.currentProducingPower.getValue());

		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/** power of the heater in watts.										*/
	public static final String	NOT_PRODUCING_POWER_RUNPNAME = "NOT_PRODUCING_POWER";
	/** power of the heater in watts.										*/
	public static final String	MAX_PRODUCING_POWER_RUNPNAME = "MAX_PRODUCING_POWER";
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
			ret.append("total production in kwh = ");
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
		return new SolarPanelElectricityReport(this.getURI(), this.totalProduction);
	}
}
// -----------------------------------------------------------------------------
