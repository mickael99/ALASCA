package fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces;

import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface WaterHeaterExternalControlCI extends OfferedCI, RequiredCI, WaterHeaterExternalControlI {
	public int getCurrentTemperature() throws Exception;
	public int getEnergyConsumption() throws Exception;
	
	public boolean isHeating() throws Exception;
	public void startHeating() throws Exception;
	public void stopHeating() throws Exception;
	
	public int getTargetTemperature() throws Exception;
	public Timer getTimer() throws Exception;
	
	public void setPowerLevel(WaterHeaterPowerLevel power) throws Exception;
	public double getPowerLevel() throws Exception;
	public WaterHeaterPowerLevel getWaterHeaterPowerLevel() throws Exception;
}
