package fr.sorbonne_u.components.hem2023.equipements.hem.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.battery.interfaces.BatteryManagementCI;

public class BatteryManagementOutboundPort extends AbstractOutboundPort implements BatteryManagementCI {
	
	private static final long serialVersionUID = 1L;

	public BatteryManagementOutboundPort(ComponentI owner) throws Exception {
		super(BatteryManagementCI.class, owner);
	}
	
	public BatteryManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, BatteryManagementCI.class, owner);
	}

	@Override
	public void setProductionMode() throws Exception {
		((BatteryManagementCI)this.getConnector()).setProductionMode();
	}

	@Override
	public void setConsomationMode() throws Exception {
		((BatteryManagementCI)this.getConnector()).setConsomationMode();

	}

	@Override
	public boolean sendBatteryToAModularEquipment(String uri, double quantity) throws Exception {
		return ((BatteryManagementCI)this.getConnector()).sendBatteryToAModularEquipment(uri, quantity);
	}
}
