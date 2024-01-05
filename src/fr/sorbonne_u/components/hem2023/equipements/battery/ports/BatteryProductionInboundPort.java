package fr.sorbonne_u.components.hem2023.equipements.battery.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.production.interfaces.ProductionEquipmentCI;
import fr.sorbonne_u.components.hem2023.equipements.production.interfaces.ProductionEquipmentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class BatteryProductionInboundPort extends AbstractInboundPort 
	implements ProductionEquipmentCI {

	private static final long serialVersionUID = 1L;

	public BatteryProductionInboundPort(ComponentI owner) throws Exception{
		super(ProductionEquipmentCI.class, owner);	
		assert owner instanceof ProductionEquipmentCI;
	}
	
	public BatteryProductionInboundPort(String uri,ComponentI owner) throws Exception{
		super(uri, ProductionEquipmentCI.class, owner);
		assert owner instanceof ProductionEquipmentCI;
	}

	@Override
	public boolean addElectricityQuantity(double quantity) throws Exception {
		return this.getOwner().handleRequest(
				o -> ((ProductionEquipmentI)o).addElectricityQuantity(quantity));
	}
}
