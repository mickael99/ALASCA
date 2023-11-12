/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.events;

import fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.DishWasherElectricityModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * @author Yukhoi
 *
 */
public class SwitchOffDishWasher extends ES_Event implements DishWasherEventI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 * create a <code>SwitchOffDishWasher</code> event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 */
	public SwitchOffDishWasher(Time timeOfOccurrence){
		super(timeOfOccurrence, null);
	}
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean hasPriorityOver(EventI e){
		return false;
	}

	@Override
	public void	executeOn(AtomicModelI model)
	{
		assert	model instanceof DishWasherElectricityModel;

		if (model instanceof DishWasherElectricityModel) {
			DishWasherElectricityModel washer = (DishWasherElectricityModel)model;
			assert	washer.getState() != DishWasherElectricityModel.State.ON :
				new AssertionError(
						"model not in the right state, should not be "
								+ "DishWasherElectricityModel.State.ON but is "
								+ washer.getState());
			washer.setState(DishWasherElectricityModel.State.OFF,
							this.getTimeOfOccurrence());
		}
	}
}
