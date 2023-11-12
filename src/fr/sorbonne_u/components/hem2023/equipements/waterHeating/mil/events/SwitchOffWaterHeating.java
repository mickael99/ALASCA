/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.events;

import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.WaterHeatingElectricityModel;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.mil.WaterHeatingTemperatureModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * @author Yukhoi
 *
 */
public class SwitchOffWaterHeating extends ES_Event implements WaterHeatingEventI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 * create a <code>SwitchOffWaterHeating</code> event.
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
	public SwitchOffWaterHeating(Time timeOfOccurrence){
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
		assert	model instanceof WaterHeatingElectricityModel ||
									model instanceof WaterHeatingTemperatureModel;

		if (model instanceof WaterHeatingElectricityModel) {
			WaterHeatingElectricityModel heater = (WaterHeatingElectricityModel)model;
			assert	heater.getState() != WaterHeatingElectricityModel.State.ON :
				new AssertionError(
						"model not in the right state, should not be "
								+ "HeaterElectricityModel.State.ON but is "
								+ heater.getState());
			heater.setState(WaterHeatingElectricityModel.State.OFF,
							this.getTimeOfOccurrence());
		} else {
			WaterHeatingTemperatureModel heater = (WaterHeatingTemperatureModel)model;
			heater.setState(WaterHeatingTemperatureModel.State.NOT_HEATING);
		}
	}
}
