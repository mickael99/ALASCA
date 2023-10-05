package fr.sorbonne_u.components.hem2023.equipements.dishWasher;

import fr.sorbonne_u.components.hem2023.timer.Timer;

public interface DishWasherInternalControlI extends DishWasherImplementationI {
	public boolean isDoorOpen() throws Exception;
	public WashingMode getWashingMode() throws Exception;
	public Timer getTimer() throws Exception;
	public boolean isDryingModeEnable()throws Exception;
	public int getEnergyConsumption() throws Exception;
	public double getWaterQuantity() throws Exception;

}
