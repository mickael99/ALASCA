package fr.sorbonne_u.components.hem2023.equipements.waterHeating.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterUserControlCI;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class WaterHeaterUserControlOutboundPort extends AbstractOutboundPort implements WaterHeaterUserControlCI {
	
	private static final long serialVersionUID = 1L;

	public WaterHeaterUserControlOutboundPort(ComponentI owner) throws Exception {
		super(WaterHeaterUserControlCI.class, owner);
	}
	
	public WaterHeaterUserControlOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, WaterHeaterUserControlCI.class, owner);
	}
	
	@Override
	public void turnOn() throws Exception {
		((WaterHeaterUserControlCI)this.getConnector()).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((WaterHeaterUserControlCI)this.getConnector()).turnOff();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((WaterHeaterUserControlCI)this.getConnector()).isOn();
	}

	@Override
	public void setTargetWaterTemperature(int degree) throws Exception {
		((WaterHeaterUserControlCI)this.getConnector()).setTargetWaterTemperature(degree);
	}

	@Override
	public void scheduleHeating(Timer launchTime, Timer endTime) throws Exception {
		((WaterHeaterUserControlCI)this.getConnector()).scheduleHeating(launchTime, endTime);
	}

	@Override
	public int getTargetTemperature() throws Exception {
		return ((WaterHeaterUserControlCI)this.getConnector()).getTargetTemperature();
	}

	@Override
	public Timer getTimer() throws Exception {
		return ((WaterHeaterUserControlCI)this.getConnector()).getTimer();
	}

	@Override
	public void setPowerLevel(double power) throws Exception {
		((WaterHeaterUserControlCI)this.getConnector()).turnOn();
	}

	@Override
	public double getPowerLevel() throws Exception {
		return ((WaterHeaterUserControlCI)this.getConnector()).getPowerLevel();
	}
}
