package fr.sorbonne_u.components.hem2023.equipements.microwave.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.*;
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
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;

public class RunMicrowaveUnitaryMILSimulation {
	public static void main(String[] args) {
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);
		
		try {
			Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = 
					new HashMap<>();
			atomicModelDescriptors.put(
									MicrowaveElectricityModel.MIL_URI,
									AtomicHIOA_Descriptor.create(
											MicrowaveElectricityModel.class,
											MicrowaveElectricityModel.MIL_URI,
											TimeUnit.HOURS,
											null));
			atomicModelDescriptors.put(
									MicrowaveUserModel.MIL_URI,
									AtomicModelDescriptor.create(
									   	MicrowaveUserModel.class,
									   	MicrowaveUserModel.MIL_URI,
										TimeUnit.HOURS,
										null));
			
			atomicModelDescriptors.put(
					MicrowaveStateModel.MIL_URI,
					AtomicModelDescriptor.create(
							MicrowaveStateModel.class,
							MicrowaveStateModel.MIL_URI,
							TimeUnit.HOURS,
							null));
			
			Map<String, CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();
			
			Set<String> submodels = new HashSet<String>();
			submodels.add(MicrowaveElectricityModel.MIL_URI);
			submodels.add(MicrowaveUserModel.MIL_URI);
			submodels.add(MicrowaveStateModel.MIL_URI);

			Map<EventSource, EventSink[]> connections = 
					new HashMap<EventSource, EventSink[]>();

			connections.put(new EventSource(MicrowaveUserModel.MIL_URI, SwitchOnMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveStateModel.MIL_URI, SwitchOnMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_URI, SwitchOffMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveStateModel.MIL_URI, SwitchOffMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_URI, SetLowMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveStateModel.MIL_URI, SetLowMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_URI, SetMediumMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveStateModel.MIL_URI, SetMediumMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_URI, SetHighMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveStateModel.MIL_URI, SetHighMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_URI, SetUnfreezeMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveStateModel.MIL_URI, SetUnfreezeMicrowave.class)
							});
					
			connections.put(new EventSource(MicrowaveUserModel.MIL_URI, SwitchOnMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.MIL_URI, SwitchOnMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_URI, SwitchOffMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.MIL_URI, SwitchOffMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_URI, SetLowMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.MIL_URI, SetLowMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_URI, SetMediumMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.MIL_URI, SetMediumMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_URI, SetHighMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.MIL_URI, SetHighMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.MIL_URI, SetUnfreezeMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.MIL_URI, SetUnfreezeMicrowave.class)
							});
			
			coupledModelDescriptors.put(
					MicrowaveCoupledModel.MIL_URI,
					new CoupledModelDescriptor(
							MicrowaveCoupledModel.class,
							MicrowaveCoupledModel.MIL_URI,
							submodels,
							null,
							null,
							connections,
							null));
			
			ArchitectureI architecture = 
					new Architecture(
							MicrowaveCoupledModel.MIL_URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.HOURS);
			
			SimulatorI se = architecture.constructSimulator();
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
			se.doStandAloneSimulation(0.0, 24.0);
			System.exit(0);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
