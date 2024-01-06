package fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces;

public interface WaterHeaterExternalControlI extends WaterHeaterUserAndExternalControlI {
	public int getCurrentTemperature() throws Exception;
	
	public boolean isHeating() throws Exception;
	public void startHeating() throws Exception;
	public void stopHeating() throws Exception;
	
	public WaterHeaterPowerLevel getWaterHeaterPowerLevel() throws Exception;
	
	public boolean suspended() throws Exception;
	public boolean suspend() throws Exception;
	public boolean resume() throws Exception;
	public double emergency() throws Exception;
}
