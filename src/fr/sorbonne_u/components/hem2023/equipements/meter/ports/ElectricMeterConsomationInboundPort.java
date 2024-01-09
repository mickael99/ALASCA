package fr.sorbonne_u.components.hem2023.equipements.meter.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterConsumptionCI;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterImplementationI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class ElectricMeterConsomationInboundPort extends AbstractInboundPort 
	implements ElectricMeterConsumptionCI {
	
	private static final long serialVersionUID = 1L;

	public ElectricMeterConsomationInboundPort(ComponentI owner) throws Exception {
		super(ElectricMeterConsumptionCI.class, owner);
	}

	public ElectricMeterConsomationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ElectricMeterConsumptionCI.class, owner);
	}

	@Override
	public void addElectricConsumption(double quantity) throws Exception {
		this.getOwner().handleRequest(
				o -> { ((ElectricMeterImplementationI)o).addElectricConsumption(quantity);
						return null;
				});
	}
}
