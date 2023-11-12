package fr.sorbonne_u.components.hem2023.equipements.fan.mil.events;


import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanElectricityModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * @author Yukhoi
 *
 */
public class SetLowFan extends AbstractFanEvent {
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	public	SetLowFan(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	@Override
	public boolean	hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOnFan) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public void	executeOn(AtomicModelI model)
	{
		assert	model instanceof FanElectricityModel;

		FanElectricityModel m = (FanElectricityModel)model;

		// a SetHigh event can only be executed when the state of the fan
		// model is in the state LOW or MEDDIUM
		if (m.getState() == FanElectricityModel.State.MEDDIUM  || 
				m.getState() == FanElectricityModel.State.HIGH) {
			// then put it in the state HIGH
			m.setState(FanElectricityModel.State.LOW);
			// trigger an internal transition by toggling the electricity
			// consumption changed boolean to true
			m.toggleConsumptionHasChanged();
		}
	}
}
//----------------------------------------------------------------------------