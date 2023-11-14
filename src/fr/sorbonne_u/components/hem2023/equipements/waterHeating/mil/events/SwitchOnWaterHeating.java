/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.events;

import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.WaterHeatingElectricityModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * @author Yukhoi
 *
 */
public class SwitchOnWaterHeating extends ES_Event implements WaterHeatingEventI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>SwitchOnWaterHeating</code> event.
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
	public	SwitchOnWaterHeating(Time timeOfOccurrence){
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		return true;
	}

	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert	model instanceof WaterHeatingElectricityModel;

		WaterHeatingElectricityModel heater = (WaterHeatingElectricityModel)model;
		assert	heater.getState() == WaterHeatingElectricityModel.State.OFF :
				new AssertionError(
						"model not in the right state, should be "
						+ "HeaterElectricityModel.State.OFF but is "
						+ heater.getState());
		heater.setState(WaterHeatingElectricityModel.State.ON,
						this.getTimeOfOccurrence());
	}
}
//--------------------------------------------------------------------------------------
