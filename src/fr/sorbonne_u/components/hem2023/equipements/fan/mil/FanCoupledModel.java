/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan.mil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;

/**
 * @author Yukhoi
 *
 */
public class FanCoupledModel extends CoupledModel {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for an instance model in MIL simulations; works as long as
	 *  only one instance is created.															*/
	public static final String	MIL_URI = FanCoupledModel.class.
													getSimpleName() + "-MIL";
	/** URI for an instance model in MIL simulations; works as long as
	 *  only one instance is created.															*/
	public static final String	MIL_RT_URI = FanCoupledModel.class.
													getSimpleName() + "-MIL_RT";
	/** URI for an instance model in MIL simulations; works as long as
	 *  only one instance is created.															*/
	public static final String	SIL_URI = FanCoupledModel.class.
													getSimpleName() + "-SIL";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * creating the coupled model instance.
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
	 * @throws Exception		<i>to do</i>.
	 */
	public	FanCoupledModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		CoordinatorI simulationEngine,
		ModelI[] submodels,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine, submodels,
			  imported, reexported, connections);
	}
}
