package fr.sorbonne_u.components.hem2023.equipements.microwave.mil;

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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2023.utils.ExecutionType;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;

// -----------------------------------------------------------------------------
/**
 * The class <code>MILSimulationArchitectures</code> defines the local
 * MIL simulation architectures pertaining to the hair dryer components.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The class provides four static methods that create the local MIL and the MIL
 * real time simulation architectures for the {@code HairDryer} and the
 * {@code HairDryerUser} components.
 * </p>
 * <p>
 * These architectures are local in the sense that they define the simulators
 * that are internal to the components. These are meant to be integrated in a
 * global component simulation architecture where they are seen as atomic
 * models that are composed into coupled models that will reside in coordinator
 * components.
 * </p>
 * <p>
 * As there is nothing to change to the simulation architectures of the hair
 * dryer for SIL simulations, the MIL real time architectures can be used to
 * execute SIL simulations.
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
 * <p>Created on : 2023-11-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	MILSimulationArchitectures
{
	/**
	 * create the local MIL simulation architecture for the {@code HairDryer}
	 * component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the local MIL simulation architecture for the {@code HairDryer} component.
	 * @throws Exception	<i>to do</i>.
	 */
	public static Architecture	createMicrowaveMILArchitecture()
	throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		// the hair dyer model simulating its electricity consumption, an
		// atomic HIOA model hence we use an AtomicHIOA_Descriptor
		atomicModelDescriptors.put(
				MicrowaveStateModel.MIL_URI,
				AtomicModelDescriptor.create(
						MicrowaveStateModel.class,
						MicrowaveStateModel.MIL_URI,
						TimeUnit.HOURS,
						null));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// simulation architecture
		Architecture architecture =
				new Architecture(
						MicrowaveStateModel.MIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}

	/**
	 * create the local MIL simulation architecture for the {@code HairDryerUser}
	 * component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the local MIL simulation architecture for the {@code HairDryerUser} component.
	 * @throws Exception	<i>to do</i>.
	 */
	public static Architecture	createMicrowaveUserMILArchitecture()
	throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		// for atomic model, we use an AtomicModelDescriptor
		atomicModelDescriptors.put(
				MicrowaveUserModel.MIL_URI,
				AtomicModelDescriptor.create(
						MicrowaveUserModel.class,
						MicrowaveUserModel.MIL_URI,
						TimeUnit.HOURS,
						null));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// simulation architecture
		Architecture architecture =
				new Architecture(
						MicrowaveUserModel.MIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}

	/**
	 * create the local MIL real time simulation architecture for the
	 * {@code HairDryer} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param currentExecutionType	current execution type for the next run.
	 * @param accelerationFactor	acceleration factor used in this run.
	 * @return						the local MIL real time simulation architecture for the {@code HairDryer} component.
	 * @throws Exception			<i>to do</i>.
	 */
	public static Architecture	createMicrowaveRTArchitecture(
		ExecutionType currentExecutionType,
		double accelerationFactor
		) throws Exception
	{
		String modelURI = null;
		switch (currentExecutionType) {
		case MIL_RT_SIMULATION:
			modelURI = MicrowaveStateModel.MIL_RT_URI;
			break;
		case SIL_SIMULATION:
			modelURI = MicrowaveStateModel.SIL_URI;
			break;
		default:
			throw new RuntimeException("incorrect executiontype: " +
													currentExecutionType + "!");
		}

		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		// the hair dyer model simulating its electricity consumption, an
		// atomic HIOA model hence we use an AtomicHIOA_Descriptor
		atomicModelDescriptors.put(
				modelURI,
				RTAtomicModelDescriptor.create(
						MicrowaveStateModel.class,
						modelURI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// simulation architecture
		Architecture architecture =
				new RTArchitecture(
						modelURI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}


	/**
	 * create the local MIL real time simulation architecture for the
	 * {@code HairDryerUser} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param accelerationFactor	acceleration factor used in this run.
	 * @return						the local MIL simulation architecture for the {@code HairDryer} component.
	 * @throws Exception			<i>to do</i>.
	 */
	public static Architecture	createMicrowaveUserMILRTArchitecture(
		double accelerationFactor
		) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		// for atomic model, we use an AtomicModelDescriptor
		atomicModelDescriptors.put(
				MicrowaveUserModel.MIL_RT_URI,
				RTAtomicModelDescriptor.create(
						MicrowaveUserModel.class,
						MicrowaveUserModel.MIL_RT_URI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// simulation architecture
		Architecture architecture =
				new RTArchitecture(
						MicrowaveUserModel.MIL_RT_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}
}
// -----------------------------------------------------------------------------
