package fr.sorbonne_u.components.hem2023.equipements.dishWasher.Ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherInternalControlCI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherInternalControlI;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class DishWasherInternalControlInboundPort extends AbstractInboundPort implements DishWasherInternalControlCI {
	
	private static final long serialVersionUID = 1L;

	public DishWasherInternalControlInboundPort(ComponentI owner) throws Exception{
		super(DishWasherInternalControlCI.class, owner);	
		assert owner instanceof DishWasherInternalControlI;
	}
	
	public DishWasherInternalControlInboundPort(String uri,ComponentI owner) throws Exception{
		super(uri, DishWasherInternalControlCI.class, owner);
		assert owner instanceof DishWasherInternalControlI;
	}
	
	@Override
	public boolean isDoorOpen() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((DishWasherInternalControlI)o).isDoorOpen());
	}

	@Override
	public WashingMode getWashingMode() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((DishWasherInternalControlI)o).getWashingMode());
	}

	@Override
	public Timer getTimer() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((DishWasherInternalControlI)o).getTimer());
	}

	@Override
	public boolean isDryingModeEnable() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((DishWasherInternalControlI)o).isDryingModeEnable());
	}

	@Override
	public int getEnergyConsumption() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((DishWasherInternalControlI)o).getEnergyConsumption());
	}

	@Override
	public double getWaterQuantity() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((DishWasherInternalControlI)o).getWaterQuantity());
	}

	@Override
	public void startWashing() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((DishWasherInternalControlI)o).startWashing();
						return null;
				});
	}

	@Override
	public void stopWashing() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((DishWasherInternalControlI)o).stopWashing();
						return null;
				});
	}
	
	@Override
	public boolean removeWaterQuantity(double waterQuantityToRemove) throws Exception {
		return this.getOwner().handleRequest(
				o -> ((DishWasherInternalControlI)o).removeWaterQuantity(waterQuantityToRemove));
	}
	
	@Override
	public boolean isCuveWaterIsEmpty() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((DishWasherInternalControlI)o).isCuveWaterIsEmpty());
	}
	
	@Override
	public boolean isWashing() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((DishWasherInternalControlI)o).isWashing());
	}
}
