/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.events;

import fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.DishWasherElectricityModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * @author Yukhoi
 *
 */
public class Wash extends Event implements DishWasherEventI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>Wash</code> event.
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
	
	public	Wash(Time timeOfOccurrence)	{
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOnDishWasher || e instanceof DoNotWash) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public void executeOn(AtomicModelI model){
		assert	model instanceof DishWasherElectricityModel;

		if (model instanceof DishWasherElectricityModel) {
			DishWasherElectricityModel washer = (DishWasherElectricityModel)model;
			assert	washer.getState() == DishWasherElectricityModel.State.ON:
					new AssertionError(
							"model not in the right state, should be "
							+ "HeaterElectricityModel.State.ON but is "
							+ washer.getState());
			washer.setState(DishWasherElectricityModel.State.WASHING,
							this.getTimeOfOccurrence());			
		}
	}
}
//-----------------------------------------------------------------------------------