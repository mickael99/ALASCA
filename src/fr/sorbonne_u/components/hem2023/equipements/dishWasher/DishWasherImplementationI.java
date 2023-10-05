package fr.sorbonne_u.components.hem2023.equipements.dishWasher;

public interface DishWasherImplementationI {
	
	public static enum DishWasherState {
		ON,
		OFF
	}
	
	public static enum WashingMode {
		ECO,
		FAST,
		NORMAL,
		INTENSIF
	}
	
	public int getWaterQuantity() throws Exception;
	public void startWashing() throws Exception;
	public void stopWashing() throws Exception;
}
