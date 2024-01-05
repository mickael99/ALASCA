package fr.sorbonne_u.components.hem2023.equipements.production.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.equipements.consomation.interfaces.ConsomationEquipmentCI;
import fr.sorbonne_u.components.hem2023.equipements.production.interfaces.ProductionEquipmentCI;

public class ProductionEquipmentConnector extends AbstractConnector implements ConsomationEquipmentCI {

	@Override
	public boolean sendBattery(double quantity) throws Exception {
		return ((ProductionEquipmentCI)this.offering).addElectricityQuantity(quantity);
	}
}
