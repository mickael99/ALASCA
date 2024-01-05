package fr.sorbonne_u.components.hem2023.equipements.productionUnit.interfaces;

import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface ProductionUnitProductionCI extends RequiredCI {
	public void productionToElectricMeter(double quantity) throws Exception;
}
