package fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherInternalControlCI;
import fr.sorbonne_u.components.hem2023.timer.Timer;

public class DishWasherInternalControlConnector extends AbstractConnector implements DishWasherInternalControlCI {

	@Override
	public boolean isDoorOpen() throws Exception {
		return ((DishWasherInternalControlCI)this.offering).isDoorOpen();
	}

	@Override
	public WashingMode getWashingMode() throws Exception {
		return ((DishWasherInternalControlCI)this.offering).getWashingMode();
	}

	@Override
	public Timer getTimer() throws Exception {
		return ((DishWasherInternalControlCI)this.offering).getTimer();
	}

	@Override
	public boolean isDryingModeEnable() throws Exception {
		return ((DishWasherInternalControlCI)this.offering).isDryingModeEnable();
	}

	@Override
	public int getEnergyConsumption() throws Exception {
		return ((DishWasherInternalControlCI)this.offering).getEnergyConsumption();
	}

	@Override
	public double getWaterQuantity() throws Exception {
		return ((DishWasherInternalControlCI)this.offering).getWaterQuantity();
	}

	@Override
	public void startWashing() throws Exception {
		((DishWasherInternalControlCI)this.offering).startWashing();
	}

	@Override
	public void stopWashing() throws Exception {
		 ((DishWasherInternalControlCI)this.offering).stopWashing();
	}
	
	@Override
	public boolean removeWaterQuantity(double waterQuantityToRemove) throws Exception {
		return ((DishWasherInternalControlCI)this.offering).removeWaterQuantity(waterQuantityToRemove);
	}
	
	@Override
	public boolean isCuveWaterIsEmpty() throws Exception {
		return ((DishWasherInternalControlCI)this.offering).isCuveWaterIsEmpty();
	}
	
	@Override
	public boolean isWashing() throws Exception {
		return ((DishWasherInternalControlCI)this.offering).isWashing();
	}
	
	@Override 
	public void setWashingMode(WashingMode washingMode)throws Exception {
		((DishWasherInternalControlCI)this.offering).setWashingMode(washingMode);
	}
	
	@Override
	public boolean isSuspended() throws Exception {
		return ((DishWasherInternalControlCI)this.offering).isSuspended();
	}
	
	@Override
	public boolean suspend() throws Exception{
		return ((DishWasherInternalControlCI)this.offering).suspend();
	}
	
	@Override
	public boolean resume() throws Exception {
		return ((DishWasherInternalControlCI)this.offering).resume();
	}
	
	@Override
	public double emergency() throws Exception {
		return ((DishWasherInternalControlCI)this.offering).emergency();
	}
}
