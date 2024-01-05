package fr.sorbonne_u.components.hem2023.equipements.productionUnit.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.interfaces.ProductionUnitProductionCI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ProductionUnitProductionOutboundPort extends AbstractOutboundPort
		implements OfferedCI, ProductionUnitProductionCI {

	private static final long serialVersionUID = 1L;

	public ProductionUnitProductionOutboundPort(ComponentI owner) throws Exception {
		super(ProductionUnitProductionCI.class, owner);
	}

	public ProductionUnitProductionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ProductionUnitProductionCI.class, owner);
	}
	
	@Override
	public void productionToElectricMeter(double quantity) throws Exception {
		((ProductionUnitProductionCI)this.getConnector()).productionToElectricMeter(quantity);
	}
}
