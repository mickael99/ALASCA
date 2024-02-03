package fr.sorbonne_u.components.hem2023.equipements.fan.mil.events;


import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanElectricityModel;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanOperationI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * @author Yukhoi
 *
 */
public class SwitchOnMusicFan extends AbstractFanEvent {
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	public	SwitchOnMusicFan(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	@Override
	public boolean	hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOffMusicFan || e instanceof SwitchOffFan ) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void	executeOn(AtomicModelI model)
	{
		assert	model instanceof FanOperationI;

		((FanOperationI)model).turnOffMusic();
	}
}
//----------------------------------------------------------------------------