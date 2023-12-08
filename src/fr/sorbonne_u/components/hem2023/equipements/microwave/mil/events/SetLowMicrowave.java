package fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events;

import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.MicrowaveElectricityModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class SetLowMicrowave extends AbstractMicrowaveEvent {

	private static final long serialVersionUID = 1L;

	public SetLowMicrowave(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if(e instanceof SwitchOnMicrowave)
			return false;
		return true;
	}
	
	@Override
	public void executeOn(AtomicModelI model) {
		MicrowaveElectricityModel m = ((MicrowaveElectricityModel)model);
		
		if(m.getState() == MicrowaveElectricityModel.State.ON && 
				m.getMode() != MicrowaveElectricityModel.Mode.LOW) {
			m.setMode(MicrowaveElectricityModel.Mode.LOW);
			m.toggleConsumptionHasChanged();
		}
	}
}
