package fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events;

import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.MicrowaveElectricityModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class SwitchOnMicrowave extends AbstractMicrowaveEvent {

	private static final long serialVersionUID = 1L;

	public SwitchOnMicrowave(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
	public boolean hasPriorityOver(EventI e) {
		return true;
	}
	
	@Override
	public void	executeOn(AtomicModelI model) {
		assert model instanceof MicrowaveElectricityModel;
		
		MicrowaveElectricityModel m = (MicrowaveElectricityModel)model;
		
		if(m.getState() == MicrowaveElectricityModel.State.OFF) {
			m.setState(MicrowaveElectricityModel.State.ON);
			m.setMode(MicrowaveElectricityModel.Mode.LOW);
			m.toggleConsumptionHasChanged();
		}	
	}
}
