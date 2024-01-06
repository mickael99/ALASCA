package fr.sorbonne_u.components.hem2023.equipements.battery.interfaces;

public interface BatteryI {
	public static enum TEST_TYPE {
		PRODUCTION_UNIT,
		ALL
	}
	
	public static enum BATTERY_MODE {
		PRODUCTION,
		CONSOMATION
	}
	
	public double getElectricityQuantity() throws Exception;
	
	public boolean isFull() throws Exception;
	public boolean isEmpty() throws Exception;
	
	public void setProductionMode() throws Exception;
	public void setConsomationMode() throws Exception;
	
	public boolean sendBatteryToAModularEquipment(String uri, double quantity) throws Exception;
}
