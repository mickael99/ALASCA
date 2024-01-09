package fr.sorbonne_u.components.hem2023.equipements.battery.interfaces;

public interface BatteryManagementI {
	public void setProductionMode() throws Exception;
	public void setConsomationMode() throws Exception;

	public boolean sendBatteryToAModularEquipment(String uri, double quantity) throws Exception;
}
