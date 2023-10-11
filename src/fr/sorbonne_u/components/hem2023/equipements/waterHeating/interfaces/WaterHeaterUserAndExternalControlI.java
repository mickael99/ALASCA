package fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces;

import fr.sorbonne_u.components.hem2023.timer.Timer;

public interface WaterHeaterUserAndExternalControlI {
	public int getTargetTemperature() throws Exception;
	public Timer getTimer() throws Exception;
	
	public void setPowerLevel(double power) throws Exception;
	public double getPowerLevel() throws Exception;
}
