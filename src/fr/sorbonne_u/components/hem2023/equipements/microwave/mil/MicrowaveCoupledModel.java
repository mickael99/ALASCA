package fr.sorbonne_u.components.hem2023.equipements.microwave.mil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;

public class MicrowaveCoupledModel extends CoupledModel {

	private static final long serialVersionUID = 1L;
	/** URI for an instance model in MIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	MIL_URI = MicrowaveCoupledModel.class.
													getSimpleName() + "-MIL";
	/** URI for an instance model in MIL real time simulations; works as
	 *  long as only one instance is created.								*/
	public static final String	MIL_RT_URI = MicrowaveCoupledModel.class.
													getSimpleName() + "-MIL_RT";
	/** URI for an instance model in SIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	SIL_URI = MicrowaveCoupledModel.class.
													getSimpleName() + "-SIL";

	public MicrowaveCoupledModel(
			String uri, 
			TimeUnit simulatedTimeUnit, 
			CoordinatorI simulationEngine,
			ModelI[] submodels, 
			Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported, 
			Map<EventSource, EventSink[]> connections) {
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections);
	}

	
}
