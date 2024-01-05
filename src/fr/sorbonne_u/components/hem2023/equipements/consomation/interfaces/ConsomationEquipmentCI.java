package fr.sorbonne_u.components.hem2023.equipements.consomation.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface ConsomationEquipmentCI extends OfferedCI, RequiredCI, ConsomationEquipmentI {
	public boolean sendBattery(double quantity) throws Exception;
}
