/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan.mil.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * @author Yukhoi
 *
 */
public class AbstractFanEvent extends ES_Event {
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	public	AbstractFanEvent(
			Time timeOfOccurrence,
			EventInformationI content
			)
		{
			super(timeOfOccurrence, content);
		}

}
//-----------------------------------------------------------------------------