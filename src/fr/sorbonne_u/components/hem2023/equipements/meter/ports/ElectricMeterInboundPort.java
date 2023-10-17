package fr.sorbonne_u.components.hem2023.equipements.meter.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterCI;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterImplementationI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class ElectricMeterInboundPort extends AbstractInboundPort
implements	ElectricMeterCI {
	
	private static final long serialVersionUID = 1L;

	public ElectricMeterInboundPort(ComponentI owner) throws Exception {
		super(ElectricMeterCI.class, owner);
	}

	public ElectricMeterInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ElectricMeterCI.class, owner);
	}
	
	@Override
	public double getCurrentConsumption() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((ElectricMeterImplementationI)o).getCurrentConsumption());
	}

	@Override
	public double getCurrentProduction() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((ElectricMeterImplementationI)o).getCurrentProduction());
	}
}