package fr.sorbonne_u.components.hem2023.equipements.waterHeating.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterExternalControlCI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterExternalControlI;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class WaterHeaterExternalControlInboundPort extends AbstractInboundPort implements WaterHeaterExternalControlCI {
	
	private static final long serialVersionUID = 1L;
	
	public WaterHeaterExternalControlInboundPort(ComponentI owner) throws Exception{
		super(WaterHeaterExternalControlCI.class, owner);	
		assert owner instanceof WaterHeaterExternalControlI;
	}
	
	public WaterHeaterExternalControlInboundPort(String uri, ComponentI owner) throws Exception{
		super(uri, WaterHeaterExternalControlCI.class, owner);
		assert owner instanceof WaterHeaterExternalControlI;
	}
	
	@Override
	public int getCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterExternalControlI)o).getCurrentTemperature());
	}

	@Override
	public boolean isHeating() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterExternalControlI)o).isHeating());
	}

	@Override
	public void startHeating() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WaterHeaterExternalControlI)o).startHeating();
						return null;
				});
	}

	@Override
	public void stopHeating() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WaterHeaterExternalControlI)o).stopHeating();
						return null;
				});
	}

	@Override
	public int getTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterExternalControlI)o).getTargetTemperature());
	}

	@Override
	public Timer getTimer() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterExternalControlI)o).getTimer());
	}

	@Override
	public void setPowerLevel(WaterHeaterPowerLevel power) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WaterHeaterExternalControlI)o).setPowerLevel(power);
						return null;
				});
		
	}

	@Override
	public double getPowerLevel() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterExternalControlI)o).getPowerLevel());
	}
	
	@Override
	public WaterHeaterPowerLevel getWaterHeaterPowerLevel() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterExternalControlI)o).getWaterHeaterPowerLevel());
	}
	
	@Override
	public boolean suspended() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterExternalControlI)o).suspended());
	}
	
	@Override
	public boolean suspend() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterExternalControlI)o).suspend());
	}
	
	@Override
	public boolean resume() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterExternalControlI)o).resume());
	}
	
	@Override
	public double emergency() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterExternalControlI)o).emergency());
	}
}
