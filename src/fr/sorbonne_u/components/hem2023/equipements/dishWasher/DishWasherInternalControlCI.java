package fr.sorbonne_u.components.hem2023.equipements.dishWasher;

import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface DishWasherInternalControlCI extends OfferedCI, RequiredCI, DishWasherInternalControlI{
	public boolean isDoorOpen() throws Exception;
	public WashingMode getWashingMode() throws Exception;
	public Timer getTimer() throws Exception;
	public boolean isDryingModeEnable()throws Exception;
	public int getEnergyConsumption() throws Exception;
	
	public double getWaterQuantity() throws Exception;
	public void startWashing() throws Exception;
	public void stopWashing() throws Exception;
}
