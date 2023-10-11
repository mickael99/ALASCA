package fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.DishWasherUserControlCI;
import fr.sorbonne_u.components.hem2023.timer.Timer;

public class DishWasherUserControlConnector extends AbstractConnector implements DishWasherUserControlCI {
	
	@Override
	public void turnOn() throws Exception {
		((DishWasherUserControlCI)this.offering).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((DishWasherUserControlCI)this.offering).turnOff();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((DishWasherUserControlCI)this.offering).isOn();
	}

	@Override
	public void setWashingMode(WashingMode washingMode) throws Exception {
		((DishWasherUserControlCI)this.offering).setWashingMode(washingMode);
	}

	@Override
	public void enableDryingMode() throws Exception {
		((DishWasherUserControlCI)this.offering).enableDryingMode();
	}

	@Override
	public void disableDryingMode() throws Exception {
		((DishWasherUserControlCI)this.offering).disableDryingMode();
	}

	@Override
	public void scheduleWashing(Timer time) throws Exception {
		((DishWasherUserControlCI)this.offering).scheduleWashing(time);
	}

	@Override
	public void openDoor() throws Exception {
		((DishWasherUserControlCI)this.offering).openDoor();
	}

	@Override
	public void closeDoor() throws Exception {
		((DishWasherUserControlCI)this.offering).closeDoor();
	}

	@Override
	public boolean fillWater(double waterQuantityToAdd) throws Exception {
		return ((DishWasherUserControlCI)this.offering).fillWater(waterQuantityToAdd);
	}
	@Override
	public void fillWaterCompletely() throws Exception {
		((DishWasherUserControlCI)this.offering).fillWaterCompletely();
	}

	@Override
	public void startWashing() throws Exception {
		((DishWasherUserControlCI)this.offering).startWashing();
	}

	@Override
	public void stopWashing() throws Exception {
		((DishWasherUserControlCI)this.offering).stopWashing();
	}
	
	@Override
	public void removeTimer() throws Exception {
		((DishWasherUserControlCI)this.offering).removeTimer();
	}
}
