package fr.sorbonne_u.components.hem2023.equipements.hem.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterConsumptionCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ElectricMeterConsumptionOutboundPort extends AbstractOutboundPort 
	implements ElectricMeterConsumptionCI {
	
	private static final long serialVersionUID = 1L;

	public ElectricMeterConsumptionOutboundPort(ComponentI owner) throws Exception {
		super(ElectricMeterConsumptionCI.class, owner);
	}

	public ElectricMeterConsumptionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ElectricMeterConsumptionCI.class, owner);
	}

	@Override
	public void addElectricConsumption(double quantity) throws Exception {
		((ElectricMeterConsumptionCI)this.getConnector()).addElectricConsumption(quantity);
	}
}
