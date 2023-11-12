/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil;


import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.events.*;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.events.SetPowerWaterHeating.PowerValue;


/**
 * @author Yukhoi
 *
 */
@ModelExternalEvents(exported = {SwitchOnWaterHeating.class,
		 SwitchOffWaterHeating.class,
		 Heat.class,
		 DoNotHeat.class,
		 SetPowerWaterHeating.class})
//-----------------------------------------------------------------------------
public class WaterHeatingUnitTestModel extends AtomicModel {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String	URI = WaterHeatingUnitTestModel.class.
															getSimpleName();

	/** steps in the test scenario.											*/
	protected int	step;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	/**
	 * create a <code>WaterHeatingUnitTestModel</code> instance.
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
	public				WaterHeatingUnitTestModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}		
	
	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);
		this.step = 1;
		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}
	
	@Override
	public ArrayList<EventI>	output()
	{
		// Simple way to implement a test scenario. Here each step generates
		// an event sent to the other models in the standard order.
		if (this.step > 0 && this.step < 10) {
			ArrayList<EventI> ret = new ArrayList<EventI>();
			switch (this.step) {
			case 1:
				ret.add(new SwitchOnWaterHeating(this.getTimeOfNextEvent()));
				break;
			case 2:
				ret.add(new Heat(this.getTimeOfNextEvent()));
				break;
			case 3:
				ret.add(new DoNotHeat(this.getTimeOfNextEvent()));
				break;
			case 4:
				ret.add(new Heat(this.getTimeOfNextEvent()));
				break;
			case 5:
				ret.add(new SetPowerWaterHeating(this.getTimeOfNextEvent(),
										   new PowerValue(2500.0)));
				break;
			case 6:
				ret.add(new SetPowerWaterHeating(this.getTimeOfNextEvent(),
										   new PowerValue(1500.0)));
				break;
			case 7:
				ret.add(new SetPowerWaterHeating(this.getTimeOfNextEvent(),
										   new PowerValue(800.0)));
				break;
			case 8:
				ret.add(new SwitchOffWaterHeating(this.getTimeOfNextEvent()));
				break;
			}
			return ret;
		} else {
			return null;
		}
	}
	
	@Override
	public Duration		timeAdvance()
	{
		if (this.step < 9) {
			return new Duration(1.0, this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}
	
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		// advance to the next step in the scenario
		this.step++;
	}
	
	@Override
	public void			endSimulation(Time endTime)
	{
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
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
