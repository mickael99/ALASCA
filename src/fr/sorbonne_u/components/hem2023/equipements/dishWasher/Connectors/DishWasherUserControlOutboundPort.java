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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnOff() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isOn() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWashingMode(WashingMode washingMode) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableDryingMode() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disableDryingMode() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scheduleWashing(Timer time) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openDoor() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeDoor() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean fillWater(int waterQuantityToAdd) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getWaterQuantity() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void startWashing() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopWashing() throws Exception {
		// TODO Auto-generated method stub
		
	}

	
}
