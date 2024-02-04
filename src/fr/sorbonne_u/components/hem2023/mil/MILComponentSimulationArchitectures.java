package fr.sorbonne_u.components.hem2023.mil;

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
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentCoupledModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentCoupledModelDescriptor;
import fr.sorbonne_u.components.hem2023.equipements.fan.Fan;
import fr.sorbonne_u.components.hem2023.equipements.fan.FanUser;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanStateModel;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanUserModel;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SetHighFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SetLowFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SetMeddiumFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SwitchOffMusicFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SwitchOnMusicFan;
import fr.sorbonne_u.components.hem2023.GlobalCoordinator;
import fr.sorbonne_u.components.hem2023.GlobalSupervisor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;

// -----------------------------------------------------------------------------
/**
 * The class <code>MILComponentSimulationArchitectures</code> defines the global
 * MIL component simulation architecture for the whole HEM application.
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
 * <p>Created on : 2023-11-16</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	MILComponentSimulationArchitectures
{
	/**
	 * create the global MIL component simulation architecture for the HEM
	 * application.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI	URI of the component model architecture to be created.
	 * @return					the global MIL simulation  architecture for the HEM application.
	 * @throws Exception		<i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	public static ComponentModelArchitecture
									createMILComponentSimulationArchitectures(
		String architectureURI
		) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		atomicModelDescriptors.put(
				FanStateModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						FanStateModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnFan.class,
							SwitchOffFan.class,
							SetLowFan.class,
							SetHighFan.class,
							SetMeddiumFan.class,
							SwitchOnMusicFan.class,
							SwitchOffMusicFan.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SwitchOnFan.class,
								SwitchOffFan.class,
								SetLowFan.class,
								SetHighFan.class,
								SetMeddiumFan.class,
								SwitchOnMusicFan.class,
								SwitchOffMusicFan.class},
						TimeUnit.HOURS,
						Fan.REFLECTION_INBOUND_PORT_URI
						));
		atomicModelDescriptors.put(
				FanUserModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						FanUserModel.MIL_URI,
						null,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnFan.class,
							SwitchOffFan.class,
							SetLowFan.class,
							SetHighFan.class,
							SetMeddiumFan.class,
							SwitchOnMusicFan.class,
							SwitchOffMusicFan.class},
						TimeUnit.HOURS,
						Fan.REFLECTION_INBOUND_PORT_URI));
		
		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(FanStateModel.MIL_URI);
		submodels.add(FanUserModel.MIL_URI);

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();
		connections.put(
			new EventSource(FanUserModel.MIL_URI,
							SwitchOnFan.class),
			new EventSink[] {
				new EventSink(FanStateModel.MIL_URI,
							  SwitchOnFan.class)
			});
		connections.put(
			new EventSource(FanUserModel.MIL_URI,
							SwitchOffFan.class),
			new EventSink[] {
				new EventSink(FanStateModel.MIL_URI,
							  SwitchOffFan.class)
			});
		connections.put(
			new EventSource(FanUserModel.MIL_URI,
							SetLowFan.class),
				new EventSink[] {
				new EventSink(FanStateModel.MIL_URI,
							  SetLowFan.class)
			});
		connections.put(
			new EventSource(FanUserModel.MIL_URI,
							SetHighFan.class),
			new EventSink[] {
				new EventSink(FanStateModel.MIL_URI,
							  SetHighFan.class)
			});
		connections.put(
				new EventSource(FanUserModel.MIL_URI,
								SetMeddiumFan.class),
				new EventSink[] {
					new EventSink(FanStateModel.MIL_URI,
								SetMeddiumFan.class)
				});
		connections.put(
				new EventSource(FanUserModel.MIL_URI,
								SwitchOnMusicFan.class),
				new EventSink[] {
					new EventSink(FanStateModel.MIL_URI,
								  SwitchOnMusicFan.class)
				});
			connections.put(
				new EventSource(FanUserModel.MIL_URI,
								SwitchOffMusicFan.class),
				new EventSink[] {
					new EventSink(FanStateModel.MIL_URI,
								  SwitchOffMusicFan.class)
				});

//		connections.put(
//			new EventSource(HairDryerStateModel.MIL_URI,
//							SwitchOnHairDryer.class),
//			new EventSink[] {
//				new EventSink(ElectricMeterCoupledModel.MIL_URI,
//							  SwitchOnHairDryer.class)
//			});
//		connections.put(
//			new EventSource(HairDryerStateModel.MIL_URI,
//							SwitchOffHairDryer.class),
//			new EventSink[] {
//				new EventSink(ElectricMeterCoupledModel.MIL_URI,
//							  SwitchOffHairDryer.class)
//			});
//		connections.put(
//			new EventSource(HairDryerStateModel.MIL_URI,
//							SetLowHairDryer.class),
//			new EventSink[] {
//				new EventSink(ElectricMeterCoupledModel.MIL_URI,
//							  SetLowHairDryer.class)
//			});
//		connections.put(
//			new EventSource(HairDryerStateModel.MIL_URI,
//							SetHighHairDryer.class),
//			new EventSink[] {
//				new EventSink(ElectricMeterCoupledModel.MIL_URI,
//							  SetHighHairDryer.class)
//			});
//
//		connections.put(
//				new EventSource(HeaterCoupledModel.MIL_URI,
//								SetPowerHeater.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.MIL_URI,
//								  SetPowerHeater.class)
//				});
//		connections.put(
//				new EventSource(HeaterCoupledModel.MIL_URI,
//								SwitchOnHeater.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.MIL_URI,
//								  SwitchOnHeater.class)
//				});
//		connections.put(
//				new EventSource(HeaterCoupledModel.MIL_URI,
//								SwitchOffHeater.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.MIL_URI,
//								  SwitchOffHeater.class)
//				});
//		connections.put(
//				new EventSource(HeaterCoupledModel.MIL_URI,
//								Heat.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.MIL_URI,
//								  Heat.class)
//				});
//		connections.put(
//				new EventSource(HeaterCoupledModel.MIL_URI,
//								DoNotHeat.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.MIL_URI,
//								  DoNotHeat.class)
//				});

		// coupled model descriptor
		coupledModelDescriptors.put(
				GlobalCoupledModel.MIL_URI,
				ComponentCoupledModelDescriptor.create(
						GlobalCoupledModel.class,
						GlobalCoupledModel.MIL_URI,
						submodels,
						null,
						null,
						connections,
						null,
						GlobalCoordinator.REFLECTION_INBOUND_PORT_URI,
						CoordinatorPlugin.class,
						null));

		ComponentModelArchitecture architecture =
				new ComponentModelArchitecture(
						GlobalSupervisor.MIL_SIM_ARCHITECTURE_URI,
						GlobalCoupledModel.MIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}

	/**
	 * create the global MIL real time component simulation architecture for the
	 * HEM application.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI		URI of the component model architecture to be created.
	 * @param accelerationFactor	acceleration factor for this run.
	 * @return						the global MIL real time simulation  architecture for the HEM application.
	 * @throws Exception			<i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	public static ComponentModelArchitecture
									createMILRTComponentSimulationArchitectures(
		String architectureURI,
		double accelerationFactor
		) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		atomicModelDescriptors.put(
				FanStateModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						FanStateModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnFan.class,
							SwitchOffFan.class,
							SetLowFan.class,
							SetHighFan.class,
							SetMeddiumFan.class,
							SwitchOnMusicFan.class,
							SwitchOffMusicFan.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SwitchOnFan.class,
								SwitchOffFan.class,
								SetLowFan.class,
								SetHighFan.class,
								SetMeddiumFan.class,
								SwitchOnMusicFan.class,
								SwitchOffMusicFan.class},
						TimeUnit.HOURS,
						Fan.REFLECTION_INBOUND_PORT_URI
						));
		atomicModelDescriptors.put(
				FanUserModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						FanUserModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnFan.class,
							SwitchOffFan.class,
							SetLowFan.class,
							SetHighFan.class,
							SetMeddiumFan.class,
							SwitchOnMusicFan.class,
							SwitchOffMusicFan.class},
						TimeUnit.HOURS,
						FanUser.REFLECTION_INBOUND_PORT_URI));

//		atomicModelDescriptors.put(
//				HeaterCoupledModel.MIL_RT_URI,
//				RTComponentAtomicModelDescriptor.create(
//						HeaterCoupledModel.MIL_RT_URI,
//						(Class<? extends EventI>[]) new Class<?>[]{},
//						(Class<? extends EventI>[]) new Class<?>[]{
//							SetPowerHeater.class,
//							SwitchOnHeater.class,
//							SwitchOffHeater.class,
//							Heat.class,
//							DoNotHeat.class},
//						TimeUnit.HOURS,
//						Heater.REFLECTION_INBOUND_PORT_URI));

//		atomicModelDescriptors.put(
//				ElectricMeterCoupledModel.MIL_RT_URI,
//				RTComponentAtomicModelDescriptor.create(
//						ElectricMeterCoupledModel.MIL_RT_URI,
//						(Class<? extends EventI>[]) new Class<?>[]{
//							SwitchOnHairDryer.class,
//							SwitchOffHairDryer.class,
//							SetLowHairDryer.class,
//							SetHighHairDryer.class},
//						(Class<? extends EventI>[]) new Class<?>[]{},
//						TimeUnit.HOURS,
//						ElectricMeter.REFLECTION_INBOUND_PORT_URI));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(FanStateModel.MIL_RT_URI);
		submodels.add(FanUserModel.MIL_RT_URI);
//		submodels.add(HeaterCoupledModel.MIL_RT_URI);
//		submodels.add(ElectricMeterCoupledModel.MIL_RT_URI);

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
	new HashMap<EventSource,EventSink[]>();
	connections.put(
			new EventSource(FanUserModel.MIL_URI,
							SwitchOnFan.class),
			new EventSink[] {
				new EventSink(FanStateModel.MIL_URI,
							  SwitchOnFan.class)
			});
		connections.put(
			new EventSource(FanUserModel.MIL_URI,
							SwitchOffFan.class),
			new EventSink[] {
				new EventSink(FanStateModel.MIL_URI,
							  SwitchOffFan.class)
			});
		connections.put(
			new EventSource(FanUserModel.MIL_URI,
							SetLowFan.class),
				new EventSink[] {
				new EventSink(FanStateModel.MIL_URI,
							  SetLowFan.class)
			});
		connections.put(
			new EventSource(FanUserModel.MIL_URI,
							SetHighFan.class),
			new EventSink[] {
				new EventSink(FanStateModel.MIL_URI,
							  SetHighFan.class)
			});
		connections.put(
				new EventSource(FanUserModel.MIL_URI,
								SetMeddiumFan.class),
				new EventSink[] {
					new EventSink(FanStateModel.MIL_URI,
								SetMeddiumFan.class)
				});
		connections.put(
				new EventSource(FanUserModel.MIL_URI,
								SwitchOnMusicFan.class),
				new EventSink[] {
					new EventSink(FanStateModel.MIL_URI,
								  SwitchOnMusicFan.class)
				});
		connections.put(
			new EventSource(FanUserModel.MIL_URI,
							SwitchOffMusicFan.class),
			new EventSink[] {
				new EventSink(FanStateModel.MIL_URI,
							  SwitchOffMusicFan.class)
			});

//		connections.put(
//			new EventSource(HairDryerStateModel.MIL_RT_URI,
//							SwitchOnHairDryer.class),
//			new EventSink[] {
//				new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
//							  SwitchOnHairDryer.class)
//			});
//		connections.put(
//			new EventSource(HairDryerStateModel.MIL_RT_URI,
//							SwitchOffHairDryer.class),
//			new EventSink[] {
//				new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
//							  SwitchOffHairDryer.class)
//			});
//		connections.put(
//			new EventSource(HairDryerStateModel.MIL_RT_URI,
//							SetLowHairDryer.class),
//			new EventSink[] {
//				new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
//							  SetLowHairDryer.class)
//			});
//		connections.put(
//			new EventSource(HairDryerStateModel.MIL_RT_URI,
//							SetHighHairDryer.class),
//			new EventSink[] {
//				new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
//							  SetHighHairDryer.class)
//			});
//
//		connections.put(
//				new EventSource(HeaterCoupledModel.MIL_RT_URI,
//								SetPowerHeater.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
//								  SetPowerHeater.class)
//				});
//		connections.put(
//				new EventSource(HeaterCoupledModel.MIL_RT_URI,
//								SwitchOnHeater.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
//								  SwitchOnHeater.class)
//				});
//		connections.put(
//				new EventSource(HeaterCoupledModel.MIL_RT_URI,
//								SwitchOffHeater.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
//								  SwitchOffHeater.class)
//				});
//		connections.put(
//				new EventSource(HeaterCoupledModel.MIL_RT_URI,
//								Heat.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
//								  Heat.class)
//				});
//		connections.put(
//				new EventSource(HeaterCoupledModel.MIL_RT_URI,
//								DoNotHeat.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
//								  DoNotHeat.class)
//				});

		// coupled model descriptor
		coupledModelDescriptors.put(
				GlobalCoupledModel.MIL_RT_URI,
				RTComponentCoupledModelDescriptor.create(
						GlobalCoupledModel.class,
						GlobalCoupledModel.MIL_RT_URI,
						submodels,
						null,
						null,
						connections,
						null,
						GlobalCoordinator.REFLECTION_INBOUND_PORT_URI,
						CoordinatorPlugin.class,
						null,
						accelerationFactor));

		ComponentModelArchitecture architecture =
				new ComponentModelArchitecture(
						GlobalSupervisor.MIL_RT_SIM_ARCHITECTURE_URI,
						GlobalCoupledModel.MIL_RT_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}
}
// -----------------------------------------------------------------------------
