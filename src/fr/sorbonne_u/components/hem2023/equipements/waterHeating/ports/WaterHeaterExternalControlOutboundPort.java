package fr.sorbonne_u.components.hem2023.equipements.waterHeating.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterExternalControlCI;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class WaterHeaterExternalControlOutboundPort extends AbstractOutboundPort
		implements WaterHeaterExternalControlCI {

	private static final long serialVersionUID = 1L;

	public WaterHeaterExternalControlOutboundPort(ComponentI owner) throws Exception {
		super(WaterHeaterExternalControlCI.class, owner);
	}
	
	public WaterHeaterExternalControlOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, WaterHeaterExternalControlCI.class, owner);
	}
	
	@Override
	public int getCurrentTemperature() throws Exception {
		return ((WaterHeaterExternalControlCI)this.getConnector()).getCurrentTemperature();
	}

	@Override
	public int getEnergyConsumption() throws Exception {
		return ((WaterHeaterExternalControlCI)this.getConnector()).getEnergyConsumption();
	}

	@Override
	public boolean isHeating() throws Exception {
		return ((WaterHeaterExternalControlCI)this.getConnector()).isHeating();
	}

	@Override
	public void startHeating() throws Exception {
		((WaterHeaterExternalControlCI)this.getConnector()).startHeating();
	}

	@Override
	public void stopHeating() throws Exception {
		((WaterHeaterExternalControlCI)this.getConnector()).stopHeating();
	}

	@Override
	public int getTargetTemperature() throws Exception {
		return ((WaterHeaterExternalControlCI)this.getConnector()).getTargetTemperature();
	}

	@Override
	public Timer getTimer() throws Exception {
		return ((WaterHeaterExternalControlCI)this.getConnector()).getTimer();
	}

	@Override
	public void setPowerLevel(WaterHeaterPowerLevel power) throws Exception {
		((WaterHeaterExternalControlCI)this.getConnector()).setPowerLevel(power);
	}

	@Override
	public double getPowerLevel() throws Exception {
		return ((WaterHeaterExternalControlCI)this.getConnector()).getPowerLevel();
	}
	
	@Override
	public WaterHeaterPowerLevel getWaterHeaterPowerLevel() throws Exception {
		return ((WaterHeaterExternalControlCI)this.getConnector()).getWaterHeaterPowerLevel();
	}
	
	@Override
	public boolean suspended() throws Exception {
		return ((WaterHeaterExternalControlCI)this.getConnector()).suspended();
	}
	
	@Override
	public boolean suspend() throws Exception {
		return ((WaterHeaterExternalControlCI)this.getConnector()).suspend();
	}
	
	@Override
	public boolean resume() throws Exception {
		return ((WaterHeaterExternalControlCI)this.getConnector()).resume();
	}
	
	@Override
	public double emergency() throws Exception {
		return ((WaterHeaterExternalControlCI)this.getConnector()).emergency();
	}
}
