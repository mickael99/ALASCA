package fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces;

import fr.sorbonne_u.components.hem2023.timer.Timer;

public interface WaterHeaterUserControlI extends WaterHeaterUserAndExternalControlI {
	public void turnOn() throws Exception;
	public void turnOff() throws Exception;
	public boolean isOn() throws Exception;
	
	public void setTargetWaterTemperature(int degree) throws Exception;
	public void scheduleHeating(Timer launchTime, Timer endTime) throws Exception;
}
