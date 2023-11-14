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
public class DoNotWash extends Event implements DishWasherEventI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>DoNotWash</code> event.
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
	public	DoNotWash(Time timeOfOccurrence){
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	@Override
	public boolean	hasPriorityOver(EventI e){
		if (e instanceof SwitchOnDishWasher) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public void	executeOn(AtomicModelI model){
		assert	model instanceof DishWasherElectricityModel;

		if (model instanceof DishWasherElectricityModel) {
			DishWasherElectricityModel heater = (DishWasherElectricityModel)model;
			assert	heater.getState() == DishWasherElectricityModel.State.WASHING:
				new AssertionError(
						"model not in the right state, should be "
								+ "HeaterElectricityModel.State.HEATING but is "
								+ heater.getState());
			heater.setState(DishWasherElectricityModel.State.ON,
							this.getTimeOfOccurrence());
		}
	}
}
//--------------------------------------------------------------------------------------