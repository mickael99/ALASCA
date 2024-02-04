package fr.sorbonne_u.components.hem2023.sil;

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
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentCoupledModelDescriptor;
import fr.sorbonne_u.components.hem2023.equipements.fan.Fan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanStateModel;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SetHighFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SetLowFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SetMeddiumFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SwitchOffMusicFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.events.SwitchOnMusicFan;
import fr.sorbonne_u.components.hem2023.GlobalCoordinator;
import fr.sorbonne_u.components.hem2023.GlobalSupervisor;
import fr.sorbonne_u.components.hem2023.mil.GlobalCoupledModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;

// -----------------------------------------------------------------------------
/**
 * The class <code>SILComponentSimulationArchitectures</code> defines the global
 * SIL component simulation architecture for the whole HEM application.
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
public abstract class	SILComponentSimulationArchitectures
{
	/**
	 * create the global SIL real time component simulation architecture for the
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
									createSILComponentSimulationArchitectures(
		String architectureURI,
		double accelerationFactor
		) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		// Currently, the HEM application has only two appliances: a hair dryer
		// and a heater.
		atomicModelDescriptors.put(
				FanStateModel.SIL_URI,
				RTComponentAtomicModelDescriptor.create(
						FanStateModel.SIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnFan.class,
							SwitchOffFan.class,
							SetLowFan.class,
							SetHighFan.class,
							SetMeddiumFan.class,
							SwitchOnMusicFan.class,
							SwitchOffMusicFan.class},	// appears here
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnFan.class,
							SwitchOffFan.class,
							SetLowFan.class,
							SetHighFan.class,
							SetMeddiumFan.class,
							SwitchOnMusicFan.class,
							SwitchOffMusicFan.class},	// appears here
						TimeUnit.HOURS,
						Fan.REFLECTION_INBOUND_PORT_URI
						));

//		atomicModelDescriptors.put(
//				HeaterCoupledModel.SIL_URI,
//				RTComponentAtomicModelDescriptor.create(
//						HeaterCoupledModel.SIL_URI,
//						(Class<? extends EventI>[]) new Class<?>[]{},
//						(Class<? extends EventI>[]) new Class<?>[]{
//							SetPowerHeater.class,
//							SwitchOnHeater.class,
//							SwitchOffHeater.class,
//							Heat.class,
//							DoNotHeat.class},
//						TimeUnit.HOURS,
//						Heater.REFLECTION_INBOUND_PORT_URI));

		// The electric meter also has a SIL simulation model
//		atomicModelDescriptors.put(
//				ElectricMeterCoupledModel.SIL_URI,
//				RTComponentAtomicModelDescriptor.create(
//						ElectricMeterCoupledModel.SIL_URI,
//						(Class<? extends EventI>[]) new Class<?>[]{
//							SwitchOnHairDryer.class,
//							SwitchOffHairDryer.class,
//							SetLowHairDryer.class,
//							SetHighHairDryer.class,
//							SetPowerHeater.class,
//							SwitchOnHeater.class,
//							SwitchOffHeater.class,
//							Heat.class,
//							DoNotHeat.class},
//						(Class<? extends EventI>[]) new Class<?>[]{},
//						TimeUnit.HOURS,
//						ElectricMeter.REFLECTION_INBOUND_PORT_URI));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(FanStateModel.SIL_URI);
//		submodels.add(HeaterCoupledModel.SIL_URI);
//		submodels.add(ElectricMeterCoupledModel.SIL_URI);

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();

		// first, the events going from the hair dryer to the electric meter
//		connections.put(
//			new EventSource(HairDryerStateModel.SIL_URI,
//							SwitchOnHairDryer.class),
//			new EventSink[] {
//				new EventSink(ElectricMeterCoupledModel.SIL_URI,
//							  SwitchOnHairDryer.class)
//			});
//		connections.put(
//			new EventSource(HairDryerStateModel.SIL_URI,
//							SwitchOffHairDryer.class),
//			new EventSink[] {
//				new EventSink(ElectricMeterCoupledModel.SIL_URI,
//							  SwitchOffHairDryer.class)
//			});
//		connections.put(
//			new EventSource(HairDryerStateModel.SIL_URI,
//							SetLowHairDryer.class),
//			new EventSink[] {
//				new EventSink(ElectricMeterCoupledModel.SIL_URI,
//							  SetLowHairDryer.class)
//			});
//		connections.put(
//			new EventSource(HairDryerStateModel.SIL_URI,
//							SetHighHairDryer.class),
//			new EventSink[] {
//				new EventSink(ElectricMeterCoupledModel.SIL_URI,
//							  SetHighHairDryer.class)
//			});

		// second, the events going from the heater to the electric meter
//		connections.put(
//				new EventSource(HeaterCoupledModel.SIL_URI,
//								SetPowerHeater.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.SIL_URI,
//								  SetPowerHeater.class)
//				});
//		connections.put(
//				new EventSource(HeaterCoupledModel.SIL_URI,
//								SwitchOnHeater.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.SIL_URI,
//								  SwitchOnHeater.class)
//				});
//		connections.put(
//				new EventSource(HeaterCoupledModel.SIL_URI,
//								SwitchOffHeater.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.SIL_URI,
//								  SwitchOffHeater.class)
//				});
//		connections.put(
//				new EventSource(HeaterCoupledModel.SIL_URI,
//								Heat.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.SIL_URI,
//								  Heat.class)
//				});
//		connections.put(
//				new EventSource(HeaterCoupledModel.SIL_URI,
//								DoNotHeat.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.SIL_URI,
//								  DoNotHeat.class)
//				});

		// coupled model descriptor
		coupledModelDescriptors.put(
				GlobalCoupledModel.SIL_URI,
				RTComponentCoupledModelDescriptor.create(
						GlobalCoupledModel.class,
						GlobalCoupledModel.SIL_URI,
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
						GlobalSupervisor.SIL_SIM_ARCHITECTURE_URI,
						GlobalCoupledModel.SIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}
}
// -----------------------------------------------------------------------------
