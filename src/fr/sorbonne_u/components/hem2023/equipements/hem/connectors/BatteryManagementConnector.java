package fr.sorbonne_u.components.hem2023.equipements.hem.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.equipements.battery.interfaces.BatteryManagementCI;


public class BatteryManagementConnector extends AbstractConnector implements BatteryManagementCI  {

	@Override
	public void setProductionMode() throws Exception {
		((BatteryManagementCI)this.offering).setProductionMode();
	}

	@Override
	public void setConsomationMode() throws Exception {
		((BatteryManagementCI)this.offering).setConsomationMode();
	}

	@Override
	public boolean sendBatteryToAModularEquipment(String uri, double quantity) throws Exception {
		return ((BatteryManagementCI)this.offering).sendBatteryToAModularEquipment(uri, quantity);
	}

}
