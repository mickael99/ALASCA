package fr.sorbonne_u.components.hem2023.equipements.consomation.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.consomation.interfaces.ConsomationEquipmentCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ConsomationEquimentOutboundPort extends AbstractOutboundPort 
	implements ConsomationEquipmentCI {

	private static final long serialVersionUID = 1L;

	public ConsomationEquimentOutboundPort(ComponentI owner) throws Exception {
		super(ConsomationEquipmentCI.class, owner);
	}
	
	public ConsomationEquimentOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ConsomationEquipmentCI.class, owner);
	}

	@Override
	public boolean sendBattery(double quantity) throws Exception {
		return ((ConsomationEquipmentCI)this.getConnector()).sendBattery(quantity);
	}
}
