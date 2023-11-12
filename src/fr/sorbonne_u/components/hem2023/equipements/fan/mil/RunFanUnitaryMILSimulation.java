/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.*;

/**
 * @author Yukhoi
 *
 */
public class RunFanUnitaryMILSimulation {
	public static void	main(String[] args)
	{
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
																new HashMap<>();

			atomicModelDescriptors.put(
					FanElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							FanElectricityModel.class,
							FanElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					FanUserModel.URI,
					AtomicModelDescriptor.create(
							FanUserModel.class,
							FanUserModel.URI,
							TimeUnit.HOURS,
							null));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(FanElectricityModel.URI);
			submodels.add(FanUserModel.URI);

			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(FanUserModel.URI, SwitchOnFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI,
										  SwitchOnFan.class)
					});
			connections.put(
					new EventSource(FanUserModel.URI, SwitchOffFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI,
										  SwitchOffFan.class)
					});
			connections.put(
					new EventSource(FanUserModel.URI, SetHighFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI,
										  SetHighFan.class)
					});
			connections.put(
					new EventSource(FanUserModel.URI, SetLowFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI,
										  SetLowFan.class)
					});
			connections.put(
					new EventSource(FanUserModel.URI, SetMeddiumFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI,
									SetMeddiumFan.class)
					});
			connections.put(
					new EventSource(FanUserModel.URI, SwitchOnMusicFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI,
									SwitchOnMusicFan.class)
					});
			connections.put(
					new EventSource(FanUserModel.URI, SwitchOffMusicFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI,
									SwitchOffMusicFan.class)
					});

			// coupled model descriptor
			coupledModelDescriptors.put(
					FanCoupledModel.URI,
					new CoupledModelDescriptor(
							FanCoupledModel.class,
							FanCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null));

			// simulation architecture
			ArchitectureI architecture =
					new Architecture(
							FanCoupledModel.URI,
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
