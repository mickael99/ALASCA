/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.events.*;


/**
 * @author Yukhoi
 *
 */
public class RunWaterHeatingUnitarySimulation {
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
					WaterHeatingElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							WaterHeatingElectricityModel.class,
							WaterHeatingElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					WaterHeatingTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							WaterHeatingTemperatureModel.class,
							WaterHeatingTemperatureModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					ExternalTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							ExternalTemperatureModel.class,
							ExternalTemperatureModel.URI,
							TimeUnit.HOURS,
							null));
			// the heater unit tester model only exchanges event, an
			// atomic model hence we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					WaterHeatingUnitTestModel.URI,
					AtomicModelDescriptor.create(
							WaterHeatingUnitTestModel.class,
							WaterHeatingUnitTestModel.URI,
							TimeUnit.HOURS,
							null));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(WaterHeatingElectricityModel.URI);
			submodels.add(WaterHeatingTemperatureModel.URI);
			submodels.add(ExternalTemperatureModel.URI);
			submodels.add(WaterHeatingUnitTestModel.URI);
			
			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(WaterHeatingUnitTestModel.URI,
							SetPowerWaterHeating.class),
					new EventSink[] {
							new EventSink(WaterHeatingElectricityModel.URI,
									SetPowerWaterHeating.class)
					});
			connections.put(
					new EventSource(WaterHeatingUnitTestModel.URI,
							SwitchOnWaterHeating.class),
					new EventSink[] {
							new EventSink(WaterHeatingElectricityModel.URI,
									SwitchOnWaterHeating.class)
					});
			connections.put(
					new EventSource(WaterHeatingUnitTestModel.URI,
							SwitchOffWaterHeating.class),
					new EventSink[] {
							new EventSink(WaterHeatingElectricityModel.URI,
									SwitchOffWaterHeating.class),
							new EventSink(WaterHeatingTemperatureModel.URI,
									SwitchOffWaterHeating.class)
					});
			connections.put(
					new EventSource(WaterHeatingUnitTestModel.URI, Heat.class),
					new EventSink[] {
							new EventSink(WaterHeatingElectricityModel.URI,
										  Heat.class),
							new EventSink(WaterHeatingTemperatureModel.URI,
										  Heat.class)
					});
			connections.put(
					new EventSource(WaterHeatingUnitTestModel.URI, DoNotHeat.class),
					new EventSink[] {
							new EventSink(WaterHeatingElectricityModel.URI,
										  DoNotHeat.class),
							new EventSink(WaterHeatingTemperatureModel.URI,
										  DoNotHeat.class)
					});

			// variable bindings between exporting and importing models
			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

			bindings.put(new VariableSource("externalTemperature",
											Double.class,
											ExternalTemperatureModel.URI),
						 new VariableSink[] {
								 new VariableSink("externalTemperature",
										 		  Double.class,
										 		 WaterHeatingTemperatureModel.URI)
						 });
			bindings.put(new VariableSource("currentHeatingPower",
											Double.class,
											WaterHeatingElectricityModel.URI),
						 new VariableSink[] {
								 new VariableSink("currentHeatingPower",
										 		  Double.class,
										 		 WaterHeatingTemperatureModel.URI)
						 });

			// coupled model descriptor
			coupledModelDescriptors.put(
					WaterHeatingCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							WaterHeatingCoupledModel.class,
							WaterHeatingCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							null,
							null,
							bindings));

			// simulation architecture
			ArchitectureI architecture =
					new Architecture(
							WaterHeatingCoupledModel.URI,
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
