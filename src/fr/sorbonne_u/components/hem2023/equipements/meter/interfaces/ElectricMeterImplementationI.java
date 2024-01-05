package fr.sorbonne_u.components.hem2023.equipements.meter.interfaces;

public interface ElectricMeterImplementationI  {	
	public double getCurrentConsumption() throws Exception;
	public double getCurrentProduction() throws Exception;
	public void addElectricProduction(double quantity) throws Exception;
	public void addElectricConsumption(double quantity) throws Exception;
}