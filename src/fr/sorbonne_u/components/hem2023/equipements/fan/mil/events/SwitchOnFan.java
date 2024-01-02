/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan.mil.events;

import java.util.ArrayList;

import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanElectricityModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * @author Yukhoi
 *
 */
public class SwitchOnFan extends AbstractFanEvent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	public	SwitchOnFan(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}
	
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	@Override
	public boolean hasPriorityOver(EventI e)
	{
		return false;
	}
	
	@Override
	public void	executeOn(AtomicModelI model)
	{
		assert model instanceof FanElectricityModel;

		FanElectricityModel m = ((FanElectricityModel)model);
		if (m.getState() == FanElectricityModel.State.OFF) {
			m.setState(FanElectricityModel.State.LOW);
			m.toggleConsumptionHasChanged();
		}
	}
}
//------------------------------------------------------------------------------