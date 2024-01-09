package fr.sorbonne_u.components.hem2023.equipements.meter.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface ElectricMeterConsumptionCI extends OfferedCI, RequiredCI {
	public void addElectricConsumption(double quantity) throws Exception;
}
