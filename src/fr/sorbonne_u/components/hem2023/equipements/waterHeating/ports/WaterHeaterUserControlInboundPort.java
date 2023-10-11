package fr.sorbonne_u.components.hem2023.equipements.waterHeating.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterExternalControlI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterUserControlCI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterUserControlI;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class WaterHeaterUserControlInboundPort extends AbstractInboundPort implements WaterHeaterUserControlCI {
	private static final long serialVersionUID = 1L;
	
	public WaterHeaterUserControlInboundPort(ComponentI owner) throws Exception{
		super(WaterHeaterUserControlCI.class, owner);	
		assert owner instanceof WaterHeaterExternalControlI;
	}
	
	public WaterHeaterUserControlInboundPort(String uri,ComponentI owner) throws Exception{
		super(uri, WaterHeaterUserControlCI.class, owner);
		assert owner instanceof WaterHeaterExternalControlI;
	}
	
	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WaterHeaterUserControlI)o).turnOn();
						return null;
				});
	}

	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WaterHeaterUserControlI)o).turnOff();
						return null;
				});
	}

	@Override
	public boolean isOn() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterUserControlI)o).isOn());
	}

	@Override
	public void setTargetWaterTemperature(int degree) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WaterHeaterUserControlI)o).setTargetWaterTemperature(degree);
						return null;
				});
	}

	@Override
	public void scheduleHeating(Timer launchTime, Timer endTime) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WaterHeaterUserControlI)o).scheduleHeating(launchTime, endTime);
						return null;
				});
	}

	@Override
	public int getTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterUserControlI)o).getTargetTemperature());
	}

	@Override
	public Timer getTimer() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterUserControlI)o).getTimer());
	}

	@Override
	public void setPowerLevel(double power) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WaterHeaterUserControlI)o).setPowerLevel(power);
						return null;
				});
	}

	@Override
	public double getPowerLevel() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterUserControlI)o).getPowerLevel());
	}

}
