package fr.sorbonne_u.components.hem2023.equipements.dishWasher.Ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherInternalControlCI;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class DishWasherInternalControlOutboundPort extends AbstractOutboundPort implements DishWasherInternalControlCI {

	private static final long serialVersionUID = 1L;
	
	public DishWasherInternalControlOutboundPort(ComponentI owner) throws Exception {
		super(DishWasherInternalControlCI.class, owner);
	}
	
	public DishWasherInternalControlOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, DishWasherInternalControlCI.class, owner);
	}

	@Override
	public boolean isDoorOpen() throws Exception {
		return ((DishWasherInternalControlCI)this.getConnector()).isDoorOpen();
	}

	@Override
	public WashingMode getWashingMode() throws Exception {
		return ((DishWasherInternalControlCI)this.getConnector()).getWashingMode();
	}

	@Override
	public Timer getTimer() throws Exception {
		return ((DishWasherInternalControlCI)this.getConnector()).getTimer();
	}

	@Override
	public boolean isDryingModeEnable() throws Exception {
		return ((DishWasherInternalControlCI)this.getConnector()).isDryingModeEnable();
	}

	@Override
	public int getEnergyConsumption() throws Exception {
		return ((DishWasherInternalControlCI)this.getConnector()).getEnergyConsumption();
	}

	@Override
	public double getWaterQuantity() throws Exception {
		return ((DishWasherInternalControlCI)this.getConnector()).getWaterQuantity();
	}

	@Override
	public void startWashing() throws Exception {
		((DishWasherInternalControlCI)this.getConnector()).startWashing();
	}

	@Override
	public void stopWashing() throws Exception {
		((DishWasherInternalControlCI)this.getConnector()).stopWashing();
	}
	
	@Override
	public boolean removeWaterQuantity(double waterQuantityToRemove) throws Exception {
		return ((DishWasherInternalControlCI)this.getConnector()).removeWaterQuantity(waterQuantityToRemove);
	}
	
	@Override
	public boolean isCuveWaterIsEmpty() throws Exception {
		return ((DishWasherInternalControlCI)this.getConnector()).isCuveWaterIsEmpty();
	}
	
	@Override
	public boolean isWashing() throws Exception {
		return ((DishWasherInternalControlCI)this.getConnector()).isWashing();
	}
}
