package fr.sorbonne_u.components.hem2023.equipements.meter.interfaces;


import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface		ElectricMeterCI
extends		ElectricMeterImplementationI, RequiredCI, OfferedCI {
	@Override
	public double getCurrentConsumption() throws Exception;
	
	@Override
	public double getCurrentProduction() throws Exception;
}
