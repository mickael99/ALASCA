package fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.DishWasherUserControlCI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.DishWasherUserControlI;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class DishWasherUserControlInboundPort extends AbstractInboundPort implements DishWasherUserControlCI {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @Override
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
	 * @param owner
	 * @throws Exception
	 */
	public DishWasherUserControlInboundPort(ComponentI owner) throws Exception{
		super(DishWasherUserControlCI.class, owner);
		assert owner instanceof DishWasherUserControlI;
	}
	
	public DishWasherUserControlInboundPort(String uri,ComponentI owner) throws Exception{
		super(uri, DishWasherUserControlCI.class, owner);
		assert owner instanceof DishWasherUserControlI;
	}

	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((DishWasherUserControlI)o).turnOn();
						return null;
				});
	}

	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((DishWasherUserControlI)o).turnOff();
						return null;
				});
	}

	@Override
	public boolean isOn() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((DishWasherUserControlI)o).isOn());
	}

	@Override
	public void setWashingMode(WashingMode washingMode) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((DishWasherUserControlI)o).setWashingMode(washingMode);
						return null;
				});
	}

	@Override
	public void enableDryingMode() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((DishWasherUserControlI)o).enableDryingMode();
						return null;
				});
	}

	@Override
	public void disableDryingMode() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((DishWasherUserControlI)o).disableDryingMode();
						return null;
				});
	}

	@Override
	public void scheduleWashing(Timer time) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((DishWasherUserControlI)o).scheduleWashing(time);
						return null;
				});
	}

	@Override
	public void openDoor() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((DishWasherUserControlI)o).openDoor();
						return null;
				});
	}

	@Override
	public void closeDoor() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((DishWasherUserControlI)o).closeDoor();
						return null;
				});
	}

	@Override
	public boolean fillWater(int waterQuantityToAdd) throws Exception {
		return this.getOwner().handleRequest(
				o -> ((DishWasherUserControlI)o).fillWater(waterQuantityToAdd));
	}

	@Override
	public void startWashing() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((DishWasherUserControlI)o).startWashing();
						return null;
				});
	}

	@Override
	public void stopWashing() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((DishWasherUserControlI)o).stopWashing();
						return null;
				});
	}

}
