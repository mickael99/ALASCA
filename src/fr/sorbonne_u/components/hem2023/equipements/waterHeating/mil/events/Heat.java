/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.events;

import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.WaterHeatingElectricityModel;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.WaterHeatingTemperatureModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * @author Yukhoi
 *
 */
public class Heat extends Event implements WaterHeatingEventI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>Heat</code> event.
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
	public Heat(Time timeOfOccurrence){
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOnWaterHeating || e instanceof DoNotHeat) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert	model instanceof WaterHeatingElectricityModel ||
									model instanceof WaterHeatingTemperatureModel;

		if (model instanceof WaterHeatingElectricityModel) {
			WaterHeatingElectricityModel heater = (WaterHeatingElectricityModel)model;
			assert	heater.getState() == WaterHeatingElectricityModel.State.ON:
					new AssertionError(
							"model not in the right state, should be "
							+ "HeaterElectricityModel.State.ON but is "
							+ heater.getState());
			heater.setState(WaterHeatingElectricityModel.State.HEATING,
							this.getTimeOfOccurrence());
		} else {
			WaterHeatingTemperatureModel heater = (WaterHeatingTemperatureModel)model;
			assert	heater.getState() == WaterHeatingTemperatureModel.State.NOT_HEATING:
					new AssertionError(
							"model not in the right state, should be "
							+ "HeaterTemperatureModel.State.NOT_HEATING but is "
							+ heater.getState());
			heater.setState(WaterHeatingTemperatureModel.State.HEATING);
		}
	}
}
