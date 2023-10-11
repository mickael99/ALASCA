package fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces;

public interface WaterHeaterExternalControlI extends WaterHeaterUserAndExternalControlI {
	public int getCurrentTemperature() throws Exception;
	public int getEnergyConsumption() throws Exception;
	
	public void heating() throws Exception;
	public void startHeating() throws Exception;
	public void stopHeating() throws Exception;
}
