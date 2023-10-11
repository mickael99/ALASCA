package fr.sorbonne_u.components.hem2023.equipements.dishWasher;

import fr.sorbonne_u.components.hem2023.timer.Timer;

public interface DishWasherInternalControlI extends DishWasherInternalAndControlI {
	public boolean isDoorOpen() throws Exception;
	public WashingMode getWashingMode() throws Exception;
	public Timer getTimer() throws Exception;
	public boolean isDryingModeEnable()throws Exception;
	public int getEnergyConsumption() throws Exception;
	public double getWaterQuantity() throws Exception;
	public boolean removeWaterQuantity(double waterQuantityToRemove) throws Exception;
	public boolean isCuveWaterIsEmpty() throws Exception;
}
