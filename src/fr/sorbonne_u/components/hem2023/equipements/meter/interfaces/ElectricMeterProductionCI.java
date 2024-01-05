package fr.sorbonne_u.components.hem2023.equipements.meter.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;

public interface ElectricMeterProductionCI extends OfferedCI {
	public void addElectricProduction(double quantity) throws Exception;
}
