package fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces;

public interface DishWasherInternalAndControlI {
	
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
	
	public static enum DoorState {
		OPEN,
		CLOSE
	}
	
	public void startWashing() throws Exception;
	public void stopWashing() throws Exception;
}
