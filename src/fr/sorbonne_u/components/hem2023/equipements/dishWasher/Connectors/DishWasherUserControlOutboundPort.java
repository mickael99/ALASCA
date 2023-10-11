package fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.DishWasherUserControlCI;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class DishWasherUserControlOutboundPort extends AbstractOutboundPort implements DishWasherUserControlCI {

	private static final long serialVersionUID = 1L;

	public DishWasherUserControlOutboundPort(ComponentI owner) throws Exception {
		super(DishWasherUserControlCI.class, owner);
	}
	
	public DishWasherUserControlOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, DishWasherUserControlCI.class, owner);
	}

	@Override
	public void turnOn() throws Exception {
		((DishWasherUserControlCI)this.getConnector()).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((DishWasherUserControlCI)this.getConnector()).turnOff();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((DishWasherUserControlCI)this.getConnector()).isOn();
	}

	@Override
	public void setWashingMode(WashingMode washingMode) throws Exception {
		((DishWasherUserControlCI)this.getConnector()).setWashingMode(washingMode);
	}

	@Override
	public void enableDryingMode() throws Exception {
		((DishWasherUserControlCI)this.getConnector()).enableDryingMode();
	}

	@Override
	public void disableDryingMode() throws Exception {
		((DishWasherUserControlCI)this.getConnector()).disableDryingMode();
	}

	@Override
	public void scheduleWashing(Timer time) throws Exception {
		((DishWasherUserControlCI)this.getConnector()).scheduleWashing(time);
	}

	@Override
	public void openDoor() throws Exception {
		((DishWasherUserControlCI)this.getConnector()).openDoor();
	}

	@Override
	public void closeDoor() throws Exception {
		((DishWasherUserControlCI)this.getConnector()).closeDoor();
	}

	@Override
	public boolean fillWater(double d) throws Exception {
		return ((DishWasherUserControlCI)this.getConnector()).fillWater(d);
	}
	
	@Override 
	public void fillWaterCompletely() throws Exception {
		((DishWasherUserControlCI)this.getConnector()).fillWaterCompletely();
	}

	@Override
	public void startWashing() throws Exception {
		((DishWasherUserControlCI)this.getConnector()).startWashing();
	}

	@Override
	public void stopWashing() throws Exception {
		((DishWasherUserControlCI)this.getConnector()).stopWashing();
	}
	
	@Override
	public void removeTimer() throws Exception {
		((DishWasherUserControlCI)this.getConnector()).removeTimer();
	}
}
