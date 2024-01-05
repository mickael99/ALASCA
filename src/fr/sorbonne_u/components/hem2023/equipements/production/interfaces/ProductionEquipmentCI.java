package fr.sorbonne_u.components.hem2023.equipements.production.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;

public interface ProductionEquipmentCI extends OfferedCI, ProductionEquipmentI {
	public boolean addElectricityQuantity(double quantity) throws Exception;
}
