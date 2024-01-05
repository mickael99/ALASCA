package fr.sorbonne_u.components.hem2023.equipements.battery.interfaces;

import fr.sorbonne_u.components.hem2023.equipements.ModularEquipementI;

public interface BatteryI {
	public static enum BATTERY_MODE {
		PRODUCTION,
		CONSOMATION
	}
	
	public double getElectricityQuantity() throws Exception;
	public boolean giveElectrictyToAModularEquipement(ModularEquipementI e, double quantity) 
			throws Exception;
	
	public boolean isFull() throws Exception;
	public boolean isEmpty() throws Exception;
	
	public void setProductionMode() throws Exception;
	public void setConsomationMode() throws Exception;
}
