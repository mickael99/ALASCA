package fr.sorbonne_u.components.hem2023.equipements.dishWasher;

import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface DishWasherUserControlCI extends RequiredCI, OfferedCI, DishWasherUserControlI {
	public void turnOn() throws Exception;
	public void turnOff() throws Exception;
	public boolean isOn() throws Exception;
	
	public void setWashingMode(WashingMode washingMode)throws Exception;
	
	public void enableDryingMode() throws Exception;
	public void disableDryingMode() throws Exception;
	
	public void scheduleWashing(Timer time) throws Exception;
		
	public void openDoor() throws Exception;
	public void closeDoor() throws Exception;
	
	public boolean fillWater(int waterQuantityToAdd) throws Exception;
	
	public void startWashing() throws Exception;
	public void stopWashing() throws Exception;
}
