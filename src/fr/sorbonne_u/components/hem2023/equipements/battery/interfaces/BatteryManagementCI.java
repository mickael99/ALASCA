package fr.sorbonne_u.components.hem2023.equipements.battery.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface BatteryManagementCI extends OfferedCI, RequiredCI, BatteryManagementI {
	public void setProductionMode() throws Exception;
	public void setConsomationMode() throws Exception;

	public boolean sendBatteryToAModularEquipment(String uri, double quantity) throws Exception;
}
