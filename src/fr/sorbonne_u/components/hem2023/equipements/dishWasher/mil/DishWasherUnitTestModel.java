/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.events.DoNotWash;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.events.SetPowerDishWasher;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.events.SetPowerDishWasher.PowerValue;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.events.SwitchOffDishWasher;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.events.SwitchOnDishWasher;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.events.Wash;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.events.DoNotHeat;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.events.Heat;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.events.SwitchOnWaterHeating;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

/**
 * @author Yukhoi
 *
 */
@ModelExternalEvents(imported = {SwitchOnDishWasher.class,
		SwitchOffDishWasher.class,
		SetPowerDishWasher.class,
		 Heat.class,
		 DoNotHeat.class})
//------------------------------------------------------------------------------
public class DishWasherUnitTestModel extends AtomicModel {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String	URI = DishWasherUnitTestModel.class.
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
	public				DishWasherUnitTestModel(
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
				ret.add(new Wash(this.getTimeOfNextEvent()));
				break;
			case 3:
				ret.add(new DoNotWash(this.getTimeOfNextEvent()));
				break;
			case 4:
				ret.add(new Wash(this.getTimeOfNextEvent()));
				break;
			case 5:
				ret.add(new SetPowerDishWasher(this.getTimeOfNextEvent(),
										   new PowerValue(900.0)));
				break;
			case 6:
				ret.add(new SetPowerDishWasher(this.getTimeOfNextEvent(),
										   new PowerValue(300.0)));
				break;
			case 7:
				ret.add(new SwitchOffDishWasher(this.getTimeOfNextEvent()));
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
		if (this.step < 8) {
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
