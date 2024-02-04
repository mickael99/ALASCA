package fr.sorbonne_u.components.hem2023.equipements.meter.mil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a basic
// household management systems as an example of a cyber-physical system.
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

import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>ElectricMeterCoupledModel</code> defines a simple coupled
 * model for the electric meter simulator included in the household energy
 * manager simulator.
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
 * invariant	{@code URI != null && !URI.isEmpty()}
 * </pre>
 * 
 * <p>Created on : 2021-09-24</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ElectricMeterCoupledModel
extends		CoupledModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** URI for an instance model in MIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	MIL_URI = ElectricMeterCoupledModel.class.
													getSimpleName() + "-MIL";
	/** URI for an instance model in MIL real time simulations; works as
	 *  long as only one instance is created.										*/
	public static final String	MIL_RT_URI = ElectricMeterCoupledModel.class.
													getSimpleName() + "-MIL_RT";
	/** URI for an instance model in SIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	SIL_URI = ElectricMeterCoupledModel.class.
													getSimpleName() + "-SIL";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * creating the coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the coupled model to be created.
	 * @param simulatedTimeUnit	time unit used in the simulation by the model.
	 * @param simulationEngine	simulation engine enacting the model.
	 * @param submodels			array of submodels of the new coupled model.
	 * @param imported			map from imported event types to submodels consuming them.
	 * @param reexported		map from event types exported by submodels that are reexported by this coupled model.
	 * @param connections		map connecting event sources to arrays of event sinks among submodels.
	 * @param importedVars		variables imported by the coupled model that are consumed by submodels.
	 * @param reexportedVars	variables exported by submodels that are reexported by the coupled model.
	 * @param bindings			bindings between exported and imported variables among submodels.
	 * @throws Exception		<i>to do</i>.
	 */
	public				ElectricMeterCoupledModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		CoordinatorI simulationEngine,
		ModelI[] submodels,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
		Map<VariableSource, VariableSink[]> bindings
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine, submodels,
			  imported, reexported, connections,
			  importedVars, reexportedVars, bindings);
	}
}
// -----------------------------------------------------------------------------
