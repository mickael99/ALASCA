package fr.sorbonne_u.components.hem2023.equipements.meter.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;

public interface ElectricMeterConsumptionCI extends OfferedCI {
	public void addElectricConsumption(double quantity) throws Exception;
}
