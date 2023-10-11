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
	public int getEnergyConsumption() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterExternalControlI)o).getEnergyConsumption());
	}

	@Override
	public void heating() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WaterHeaterExternalControlI)o).heating();
						return null;
				});
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
	public void setPowerLevel(double power) throws Exception {
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
}
