/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.microwave.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SetHighMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SetLowMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SetMediumMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SetUnfreezeMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SwitchOffMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SwitchOnMicrowave;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

/**
 * @author Yukhoi
 *
 */
public class RunMicrowaveUnitaryMILRTSimulation {
	
	public static final double			ACCELERATION_FACTOR = 3600.0;

	public static void main(String[] args) {
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);
		
		try {
			Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = 
					new HashMap<>();
			atomicModelDescriptors.put(
									MicrowaveElectricityModel.MIL_RT_URI,
									AtomicHIOA_Descriptor.create(
											MicrowaveElectricityModel.class,
											MicrowaveElectricityModel.MIL_RT_URI,
											TimeUnit.HOURS,
											null));
			
			atomicModelDescriptors.put(
									MicrowaveStateModel.MIL_RT_URI,
									AtomicModelDescriptor.create(
											MicrowaveStateModel.class,
											MicrowaveStateModel.MIL_RT_URI,
											TimeUnit.HOURS,
											null));
			
			atomicModelDescriptors.put(
									MicrowaveUserModel.MIL_RT_URI,
									AtomicModelDescriptor.create(
										   	MicrowaveUserModel.class,
										   	MicrowaveUserModel.MIL_RT_URI,
											TimeUnit.HOURS,
											null));
				
			Map<String, CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();
			
			Set<String> submodels = new HashSet<String>();
			submodels.add(MicrowaveElectricityModel.MIL_RT_URI);
			submodels.add(MicrowaveStateModel.MIL_RT_URI);
			submodels.add(MicrowaveUserModel.MIL_RT_URI);
			
			Map<EventSource, EventSink[]> connections = 
					new HashMap<EventSource, EventSink[]>();
			
			connections.put(new EventSource(MicrowaveUserModel.MIL_RT_URI, SwitchOnMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveStateModel.MIL_RT_URI, SwitchOnMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_RT_URI, SwitchOffMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveStateModel.MIL_RT_URI, SwitchOffMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_RT_URI, SetLowMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveStateModel.MIL_RT_URI, SetLowMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_RT_URI, SetMediumMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveStateModel.MIL_RT_URI, SetMediumMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_RT_URI, SetHighMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveStateModel.MIL_RT_URI, SetHighMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_RT_URI, SetUnfreezeMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveStateModel.MIL_RT_URI, SetUnfreezeMicrowave.class)
							});
			
			connections.put(new EventSource(MicrowaveUserModel.MIL_RT_URI, SwitchOnMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.MIL_RT_URI, SwitchOnMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_RT_URI, SwitchOffMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.MIL_RT_URI, SwitchOffMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_RT_URI, SetLowMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.MIL_RT_URI, SetLowMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_RT_URI, SetMediumMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.MIL_RT_URI, SetMediumMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_RT_URI, SetHighMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.MIL_RT_URI, SetHighMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_RT_URI, SetUnfreezeMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.MIL_RT_URI, SetUnfreezeMicrowave.class)
							});
			
			coupledModelDescriptors.put(
					MicrowaveCoupledModel.MIL_RT_URI,
					new RTCoupledModelDescriptor(
							MicrowaveCoupledModel.class,
							MicrowaveCoupledModel.MIL_RT_URI,
							submodels,
							null,
							null,
							connections,
							null,
							ACCELERATION_FACTOR));
			
			ArchitectureI architecture = 
					new Architecture(
							MicrowaveCoupledModel.MIL_RT_URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.HOURS);
			
			SimulatorI se = architecture.constructSimulator();
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

			long start = System.currentTimeMillis() + 100;
			double simulationDuration = 24.0;
			se.startRTSimulation(start, 0.0, simulationDuration);
			long executionDuration =					
				new Double(TimeUnit.HOURS.toMillis(1)*
									(simulationDuration/ACCELERATION_FACTOR)).
																	longValue();
			Thread.sleep(executionDuration + 2000L);
			SimulationReportI sr = se.getSimulatedModel().getFinalReport();
			System.out.println(sr);
			Thread.sleep(10000L);
			System.exit(0);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
