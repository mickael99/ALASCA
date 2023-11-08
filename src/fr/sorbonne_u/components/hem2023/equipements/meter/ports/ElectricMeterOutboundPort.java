package fr.sorbonne_u.components.hem2023.equipements.meter.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ElectricMeterOutboundPort extends AbstractOutboundPort
implements	ElectricMeterCI {

	private static final long serialVersionUID = 1L;

	public ElectricMeterOutboundPort(ComponentI owner) throws Exception {
		super(ElectricMeterCI.class, owner);
	}

	public ElectricMeterOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ElectricMeterCI.class, owner);
	}
	
	@Override
	public double getCurrentConsumption() throws Exception {
		return ((ElectricMeterCI)this.getConnector()).getCurrentConsumption();
	}
 
	@Override
	public double getCurrentProduction() throws Exception {
		return ((ElectricMeterCI)this.getConnector()).getCurrentProduction();
	}
}