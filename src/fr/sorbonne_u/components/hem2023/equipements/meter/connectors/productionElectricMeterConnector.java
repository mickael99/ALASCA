package fr.sorbonne_u.components.hem2023.equipements.meter.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterProductionCI;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.interfaces.ProductionUnitProductionCI;

public class productionElectricMeterConnector extends AbstractConnector 
	implements ProductionUnitProductionCI {

	@Override
	public void productionToElectricMeter(double quantity) throws Exception {
		((ElectricMeterProductionCI)this.offering).addElectricProduction(quantity);
	}
}
