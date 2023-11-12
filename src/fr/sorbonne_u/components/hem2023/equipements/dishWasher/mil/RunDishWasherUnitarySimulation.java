/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.events.*;


/**
 * @author Yukhoi
 *
 */
public class RunDishWasherUnitarySimulation {
	public static void main(String[] args)
	{
		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			// the heater models simulating its electricity consumption, its
			// temperatures and the external temperature are atomic HIOA models
			// hence we use an AtomicHIOA_Descriptor(s)
			atomicModelDescriptors.put(
					DishWasherElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							DishWasherElectricityModel.class,
							DishWasherElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			// the heater unit tester model only exchanges event, an
			// atomic model hence we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					DishWasherUnitTestModel.URI,
					AtomicModelDescriptor.create(
							DishWasherUnitTestModel.class,
							DishWasherUnitTestModel.URI,
							TimeUnit.HOURS,
							null));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(DishWasherElectricityModel.URI);
			submodels.add(DishWasherUnitTestModel.URI);
			
			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(DishWasherUnitTestModel.URI,
							SetPowerDishWasher.class),
					new EventSink[] {
							new EventSink(DishWasherElectricityModel.URI,
									SetPowerDishWasher.class)
					});
			connections.put(
					new EventSource(DishWasherUnitTestModel.URI,
							SwitchOnDishWasher.class),
					new EventSink[] {
							new EventSink(DishWasherElectricityModel.URI,
									SwitchOnDishWasher.class)
					});
			connections.put(
					new EventSource(DishWasherUnitTestModel.URI,
							SwitchOffDishWasher.class),
					new EventSink[] {
							new EventSink(DishWasherElectricityModel.URI,
									SwitchOffDishWasher.class)
					});
			connections.put(
					new EventSource(DishWasherUnitTestModel.URI, Wash.class),
					new EventSink[] {
							new EventSink(DishWasherElectricityModel.URI,
										  Wash.class)
					});
			connections.put(
					new EventSource(DishWasherUnitTestModel.URI, DoNotWash.class),
					new EventSink[] {
							new EventSink(DishWasherElectricityModel.URI,
										  DoNotWash.class)
					});

			// coupled model descriptor
			coupledModelDescriptors.put(
					DishWasherCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							DishWasherCoupledModel.class,
							DishWasherCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							null,
							null,
							null));

			// simulation architecture
			ArchitectureI architecture =
					new Architecture(
							DishWasherCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.HOURS);

			// create the simulator from the simulation architecture
			SimulatorI se = architecture.constructSimulator();
			// this add additional time at each simulation step in
			// standard simulations (useful when debugging)
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
			// run a simulation with the simulation beginning at 0.0 and
			// ending at 24.0
			se.doStandAloneSimulation(0.0, 24.0);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
