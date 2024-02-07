package fr.sorbonne_u.components.hem2023.equipements.fan.mil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// real time distributed applications in the Java programming language.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SetHighFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SetLowFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SetMeddiumFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SwitchOffMusicFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SwitchOnMusicFan;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunHairDryerUnitaryMILRTSimulation</code> tests the MIL
 * real time simulation architecture for the hair dryer by executing the
 * simulation in a stand alone way.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// TODO	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// TODO	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-11-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RunFanUnitaryMILRTSimulation
{
	public static final double			ACCELERATION_FACTOR = 3600.0;

	public static void	main(String[] args)
	{
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
																new HashMap<>();

			// the hair dyer model simulating its electricity consumption, an
			// atomic HIOA model hence we use an AtomicHIOA_Descriptor
			atomicModelDescriptors.put(
					FanElectricityModel.MIL_RT_URI,
					RTAtomicHIOA_Descriptor.create(
							FanElectricityModel.class,
							FanElectricityModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			// for atomic models, we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					FanStateModel.MIL_RT_URI,
					RTAtomicModelDescriptor.create(
							FanStateModel.class,
							FanStateModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));

			atomicModelDescriptors.put(
					FanUserModel.MIL_RT_URI,
					RTAtomicModelDescriptor.create(
							FanUserModel.class,
							FanUserModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(FanElectricityModel.MIL_RT_URI);
			submodels.add(FanStateModel.MIL_RT_URI);
			submodels.add(FanUserModel.MIL_RT_URI);

			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
				new EventSource(FanUserModel.MIL_RT_URI,
								SwitchOnFan.class),
				new EventSink[] {
					new EventSink(FanStateModel.MIL_RT_URI,
								  SwitchOnFan.class)
				});
			connections.put(
				new EventSource(FanUserModel.MIL_RT_URI,
								SwitchOffFan.class),
				new EventSink[] {
					new EventSink(FanStateModel.MIL_RT_URI,
								  SwitchOffFan.class)
				});
			connections.put(
				new EventSource(FanUserModel.MIL_RT_URI,
								SetHighFan.class),
				new EventSink[] {
					new EventSink(FanStateModel.MIL_RT_URI,
								  SetHighFan.class)
				});
			connections.put(
				new EventSource(FanUserModel.MIL_RT_URI,
								SetLowFan.class),
				new EventSink[] {
					new EventSink(FanStateModel.MIL_RT_URI,
								SetLowFan.class)
				});
			connections.put(
					new EventSource(FanUserModel.MIL_RT_URI,
									SetMeddiumFan.class),
					new EventSink[] {
						new EventSink(FanStateModel.MIL_RT_URI,
									SetMeddiumFan.class)
					});
			connections.put(
					new EventSource(FanUserModel.MIL_RT_URI,
									SwitchOnMusicFan.class),
					new EventSink[] {
						new EventSink(FanStateModel.MIL_RT_URI,
									SwitchOnMusicFan.class)
					});
			connections.put(
					new EventSource(FanUserModel.MIL_RT_URI,
									SwitchOffMusicFan.class),
					new EventSink[] {
						new EventSink(FanStateModel.MIL_RT_URI,
									SwitchOffMusicFan.class)
					});

			connections.put(
				new EventSource(FanStateModel.MIL_RT_URI,
								SwitchOnFan.class),
				new EventSink[] {
					new EventSink(FanElectricityModel.MIL_RT_URI,
								  SwitchOnFan.class)
				});
			connections.put(
				new EventSource(FanStateModel.MIL_RT_URI,
								SwitchOffFan.class),
				new EventSink[] {
					new EventSink(FanElectricityModel.MIL_RT_URI,
								  SwitchOffFan.class)
				});
			connections.put(
				new EventSource(FanStateModel.MIL_RT_URI,
								SetHighFan.class),
				new EventSink[] {
					new EventSink(FanElectricityModel.MIL_RT_URI,
								  SetHighFan.class)
				});
			connections.put(
				new EventSource(FanStateModel.MIL_RT_URI,
								SetLowFan.class),
				new EventSink[] {
					new EventSink(FanElectricityModel.MIL_RT_URI,
								  SetLowFan.class)
				});
			connections.put(
					new EventSource(FanStateModel.MIL_RT_URI,
									SetMeddiumFan.class),
					new EventSink[] {
						new EventSink(FanElectricityModel.MIL_RT_URI,
									SetMeddiumFan.class)
					});
			connections.put(
					new EventSource(FanStateModel.MIL_RT_URI,
									SwitchOnMusicFan.class),
					new EventSink[] {
						new EventSink(FanElectricityModel.MIL_RT_URI,
									SwitchOnMusicFan.class)
					});
			connections.put(
					new EventSource(FanStateModel.MIL_RT_URI,
									SwitchOffMusicFan.class),
					new EventSink[] {
						new EventSink(FanElectricityModel.MIL_RT_URI,
									SwitchOffMusicFan.class)
					});

			// coupled model descriptor
			coupledModelDescriptors.put(
					FanCoupledModel.MIL_RT_URI,
					new RTCoupledModelDescriptor(
							FanCoupledModel.class,
							FanCoupledModel.MIL_RT_URI,
							submodels,
							null,
							null,
							connections,
							null,
							ACCELERATION_FACTOR));

			// simulation architecture
			ArchitectureI architecture =
					new RTArchitecture(
							FanCoupledModel.MIL_RT_URI,
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
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
