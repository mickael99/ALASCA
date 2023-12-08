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
									MicrowaveElectricityModel.URI,
									AtomicHIOA_Descriptor.create(
											MicrowaveElectricityModel.class,
											MicrowaveElectricityModel.URI,
											TimeUnit.HOURS,
											null));
			atomicModelDescriptors.put(
									MicrowaveUserModel.URI,
									AtomicModelDescriptor.create(
									   	MicrowaveUserModel.class,
									   	MicrowaveUserModel.URI,
										TimeUnit.HOURS,
										null));
			
			Map<String, CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();
			
			Set<String> submodels = new HashSet<String>();
			submodels.add(MicrowaveElectricityModel.URI);
			submodels.add(MicrowaveUserModel.URI);
			
			Map<EventSource, EventSink[]> connections = 
					new HashMap<EventSource, EventSink[]>();
			connections.put(new EventSource(MicrowaveUserModel.URI, SwitchOnMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.URI, SwitchOnMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.URI, SwitchOffMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.URI, SwitchOffMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.URI, SetLowMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.URI, SetLowMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.URI, SetMediumMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.URI, SetMediumMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.URI, SetHighMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.URI, SetHighMicrowave.class)
							});
			connections.put(new EventSource(MicrowaveUserModel.URI, SetUnfreezMicrowave.class),
							new EventSink[] {
									new EventSink(MicrowaveElectricityModel.URI, SetUnfreezMicrowave.class)
							});
			
			coupledModelDescriptors.put(
					MicrowaveCoupledModel.URI,
					new CoupledModelDescriptor(
							MicrowaveCoupledModel.class,
							MicrowaveCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null));
			
			ArchitectureI architecture = 
					new Architecture(
							MicrowaveCoupledModel.URI,
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
