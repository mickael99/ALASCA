package fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces;

import fr.sorbonne_u.components.hem2023.timer.Timer;

public interface WaterHeaterUserAndExternalControlI {
	public static enum WaterHeaterState {
		ON,
		OFF
	}
	public static enum WaterHeaterPowerLevel {
		LOW,
		NORMAL,
		HIGH
	}
	
	public int getTargetTemperature() throws Exception;
	public Timer getTimer() throws Exception;
	
	public void setPowerLevel(WaterHeaterPowerLevel power) throws Exception;
	public double getPowerLevel() throws Exception;
}
