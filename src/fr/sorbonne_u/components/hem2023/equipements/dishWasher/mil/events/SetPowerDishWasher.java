/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.events;

import fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.DishWasherElectricityModel;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.mil.DishWasherElectricityModel.State;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * @author Yukhoi
 *
 */
public class SetPowerDishWasher extends ES_Event implements DishWasherEventI {

	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------
	
	public static class	PowerValue implements EventInformationI{
		
		private static final long serialVersionUID = 1L;
		/* a power in watts.												*/
		protected final double	power;
	
		/**
		 * create an instance of {@code PowerValue}.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code power >= 0.0 && power <= HeaterElectricityModel.MAX_HEATING_POWER}
		 * post	{@code getPower() == power}
		 * </pre>
		 *
		 * @param power	the power in watts to put in this container.
		 */
		public	PowerValue(double power)
		{
			super();
	
			assert	power >= 0.0 &&
							power <= DishWasherElectricityModel.MAX_WASHING_POWER :
					new AssertionError(
							"Precondition violation: power >= 0.0 && "
							+ "power <= WaterHeatingElectricityModel.MAX_HEATING_POWER,"
							+ " but power = " + power);
	
			this.power = power;
		}
		
		/**
		 * return the power value in watts.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code return >= 0.0 && return <= HeaterElectricityModel.MAX_HEATING_POWER}
		 * </pre>
		 *
		 * @return	the power value in watts.
		 */
		public double	getPower()	{ return this.power; }
	
		@Override
		public String	toString()
		{
			StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
			sb.append('[');
			sb.append(this.power);
			sb.append(']');
			return sb.toString();
		}
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** the power value to be set on the heater when the event will be
	 *  executed.															*/
	protected final PowerValue	powerValue;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a {@code SetPowerWaterHeating} event which content is a
	 * {@code PowerValue}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code content instanceof PowerValue}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param timeOfOccurrence	time at which the event must be executed in simulated time.
	 * @param content			the power value to be set on the dish washer when the event will be executed.
	 */
	public SetPowerDishWasher(
		Time timeOfOccurrence,
		EventInformationI content
		)
	{
		super(timeOfOccurrence, content);

		assert	content instanceof PowerValue :
				new AssertionError(
						"Precondition violation: event content is not a "
						+ "PowerValue " + content);

		this.powerValue = (PowerValue) content;
	}
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean	hasPriorityOver(EventI e){
		if (e instanceof SwitchOffDishWasher) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void	executeOn(AtomicModelI model){
		assert	model instanceof DishWasherElectricityModel;

		DishWasherElectricityModel washer = (DishWasherElectricityModel)model;
		assert	washer.getState() == State.WASHING :
				new AssertionError(
						"model not in the right state, should be "
						+ "State.HEATING but is " + washer.getState());
		washer.setCurrentWashingPower(this.powerValue.getPower(),
									  this.getTimeOfOccurrence());
	}
}
//----------------------------------------------------------------------------------