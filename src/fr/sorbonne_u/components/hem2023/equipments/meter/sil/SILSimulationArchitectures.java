package fr.sorbonne_u.components.hem2023.equipments.meter.sil;

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
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.events.SetHighHairDryer;
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.events.SetLowHairDryer;
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.events.SwitchOffHairDryer;
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.events.SwitchOnHairDryer;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.HeaterElectricityModel;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.DoNotHeat;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.Heat;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.SetPowerHeater;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.SwitchOffHeater;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.SwitchOnHeater;
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.HairDryerElectricityModel;
import fr.sorbonne_u.components.hem2023e3.equipments.meter.mil.ElectricMeterCoupledModel;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.HIOA_Composer;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;

// -----------------------------------------------------------------------------
/**
 * The class <code>SILSimulationArchitectures</code> defines the local
 * SIL simulation architectures pertaining to the electric meter components.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Compared to the MIL architectures, the SIL just replace the
 * {@code ElectricMeterElectricityModel} by the
 * {@code ElectricMeterElectricitySILModel}.
 * </p>
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
public abstract class	SILSimulationArchitectures
{
	/**
	 * create the local SIL real time simulation architecture for the
	 * {@code ElectricMeter} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param accelerationFactor	acceleration factor for this run.
	 * @return						the local SIL real time simulation architecture for the {@code ElectricMeter} component.
	 * @throws Exception			<i>to do</i>.
	 */
	public static Architecture	createElectricMeterSILArchitecture(
		double accelerationFactor
		) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		// the electric meter electricity model accumulates the electric
		// power consumption, an atomic HIOA model hence we use an
		// RTAtomicHIOA_Descriptor
		atomicModelDescriptors.put(
				ElectricMeterElectricitySILModel.SIL_URI,
				RTAtomicHIOA_Descriptor.create(
						ElectricMeterElectricitySILModel.class,
						ElectricMeterElectricitySILModel.SIL_URI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				HairDryerElectricityModel.SIL_URI,
				RTAtomicHIOA_Descriptor.create(
						HairDryerElectricityModel.class,
						HairDryerElectricityModel.SIL_URI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				HeaterElectricityModel.SIL_URI,
				RTAtomicHIOA_Descriptor.create(
						HeaterElectricityModel.class,
						HeaterElectricityModel.SIL_URI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(ElectricMeterElectricitySILModel.SIL_URI);
		submodels.add(HairDryerElectricityModel.SIL_URI);
		submodels.add(HeaterElectricityModel.SIL_URI);

		Map<Class<? extends EventI>,EventSink[]> imported = new HashMap<>();
		imported.put(
				SwitchOnHairDryer.class,
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.SIL_URI,
								  SwitchOnHairDryer.class)
				});
		imported.put(
				SwitchOffHairDryer.class,
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.SIL_URI,
								  SwitchOffHairDryer.class)
				});
		imported.put(
				SetLowHairDryer.class,
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.SIL_URI,
								  SetLowHairDryer.class)
				});
		imported.put(
				SetHighHairDryer.class,
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.SIL_URI,
								  SetHighHairDryer.class)
				});

		imported.put(
				SetPowerHeater.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.SIL_URI,
									  SetPowerHeater.class)
				});
		imported.put(
				SwitchOnHeater.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.SIL_URI,
									  SwitchOnHeater.class)
				});
		imported.put(
				SwitchOffHeater.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.SIL_URI,
									  SwitchOffHeater.class)
				});
		imported.put(
				Heat.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.SIL_URI,
									  Heat.class)
				});
		imported.put(
				DoNotHeat.class,
				new EventSink[] {
						new EventSink(HeaterElectricityModel.SIL_URI,
									  DoNotHeat.class)
				});

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();
		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   HairDryerElectricityModel.SIL_URI),
				new VariableSink[] {
					new VariableSink("currentHairDryerIntensity",
									 Double.class,
									 ElectricMeterElectricitySILModel.SIL_URI)
				});

		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   HeaterElectricityModel.SIL_URI),
				new VariableSink[] {
					new VariableSink("currentHeaterIntensity",
									 Double.class,
									 ElectricMeterElectricitySILModel.SIL_URI)
				});

		coupledModelDescriptors.put(
				ElectricMeterCoupledModel.SIL_URI,
				new RTCoupledHIOA_Descriptor(
						ElectricMeterCoupledModel.class,
						ElectricMeterCoupledModel.SIL_URI,
						submodels,
						imported,
						null,
						null,
						null,
						null,
						null,
						bindings,
						new HIOA_Composer(),
						accelerationFactor));

		Architecture architecture =
				new RTArchitecture(
						ElectricMeterCoupledModel.SIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}
}
// -----------------------------------------------------------------------------
