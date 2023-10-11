package fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces;

import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface WaterHeaterUserControlCI extends RequiredCI, OfferedCI, WaterHeaterUserControlI{
	public void turnOn() throws Exception;
	public void turnOff() throws Exception;
	public boolean isOn() throws Exception;
	
	public void setTargetWaterTemperature(int degree) throws Exception;
	public void scheduleHeating(Timer launchTime, Timer endTime) throws Exception;
	
	public int getTargetTemperature() throws Exception;
	public Timer getTimer() throws Exception;
	
	public void setPowerLevel(double power) throws Exception;
	public double getPowerLevel() throws Exception;
}
