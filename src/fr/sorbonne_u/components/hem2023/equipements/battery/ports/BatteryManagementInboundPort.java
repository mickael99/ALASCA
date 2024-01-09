package fr.sorbonne_u.components.hem2023.equipements.battery.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.battery.interfaces.BatteryManagementCI;
import fr.sorbonne_u.components.hem2023.equipements.battery.interfaces.BatteryManagementI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class BatteryManagementInboundPort extends AbstractInboundPort implements BatteryManagementCI {

	private static final long serialVersionUID = 1L;

	public BatteryManagementInboundPort(ComponentI owner) throws Exception{
		super(BatteryManagementCI.class, owner);	
		assert owner instanceof BatteryManagementCI;
	}
	
	public BatteryManagementInboundPort(String uri,ComponentI owner) throws Exception{
		super(uri, BatteryManagementCI.class, owner);
		assert owner instanceof BatteryManagementCI;
	}
	@Override
	public void setProductionMode() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((BatteryManagementI)o).setProductionMode();
						return null;
				});
	}

	@Override
	public void setConsomationMode() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((BatteryManagementI)o).setConsomationMode();
						return null;
				});
	}

	@Override
	public boolean sendBatteryToAModularEquipment(String uri, double quantity) throws Exception {
		return this.getOwner().handleRequest(
				o -> ((BatteryManagementI)o).sendBatteryToAModularEquipment(uri, quantity));
	}
}
