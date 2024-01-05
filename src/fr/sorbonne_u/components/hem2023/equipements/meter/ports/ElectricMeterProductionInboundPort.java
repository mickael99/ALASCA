package fr.sorbonne_u.components.hem2023.equipements.meter.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterProductionCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class ElectricMeterProductionInboundPort extends AbstractInboundPort 
	implements ElectricMeterProductionCI {

	private static final long serialVersionUID = 1L;

	public ElectricMeterProductionInboundPort(ComponentI owner) throws Exception {
		super(ElectricMeterProductionCI.class, owner);
	}

	public ElectricMeterProductionInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ElectricMeterProductionCI.class, owner);
	}

	@Override
	public void addElectricProduction(double quantity) throws Exception {
		this.getOwner().handleRequest(
				o -> { ((ElectricMeterImplementationI)o).addElectricProduction(quantity);
						return null;
				});		
	}
}
