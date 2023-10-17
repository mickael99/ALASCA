package fr.sorbonne_u.components.hem2023.equipements.meter.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterCI;

public class ElectricMeterConnector extends	AbstractConnector implements ElectricMeterCI {
	
	@Override
	public double getCurrentConsumption() throws Exception {
		return ((ElectricMeterCI)this.offering).getCurrentConsumption();
	}

	
	@Override
	public double getCurrentProduction() throws Exception {
		return ((ElectricMeterCI)this.offering).getCurrentProduction();
	}
}