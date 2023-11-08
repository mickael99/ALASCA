package fr.sorbonne_u.components.hem2023.equipements.meter.interfaces;

public interface ElectricMeterImplementationI {	
	public double getCurrentConsumption() throws Exception;
	public double getCurrentProduction() throws Exception;
}