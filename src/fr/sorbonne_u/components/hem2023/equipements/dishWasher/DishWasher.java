package fr.sorbonne_u.components.hem2023.equipements.dishWasher;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors.DishWasherInternalControlInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors.DishWasherUserControlInboundPort;
import fr.sorbonne_u.components.hem2023.timer.Timer;

public class DishWasher extends AbstractComponent implements DishWasherUserControlI, DishWasherInternalControlI {
	public static final boolean VERBOSE = true;
	
	public static final String URI_USER_CONTROL_INBOUND_PORT = 
									"URI_USER_CONTROL_INBOUND_PORT";
	public static final String URI_INTERNAL_CONTROL_INBOUND_PORT = 
			"URI_INTERNAL_CONTROL_INBOUND_PORT";
	
	protected DishWasherUserControlInboundPort dishWasherUserControlInboundPort;
	protected DishWasherInternalControlInboundPort dishWasherInternalControlInboundPort;

	
	protected DishWasherState dishWasherState;
	protected WashingMode washingMode;
	protected boolean enableDrying;
	protected Timer delayedProgram;
	protected DoorState doorState;
	
	protected double waterQuantityInLiter;
	protected static final double MAX_WATER_QUANTITY_IN_LITER = 15;
	
	/**
	 * 
	 * 				CONSTRUCTORS
	 */
	protected DishWasher() throws Exception {
		super(1, 0);
		initialiseDishWasher();
		initialisePort();
	}
	
	protected DishWasher(String uri_id) throws Exception {
		super(uri_id, 1, 0);
		initialiseDishWasher();
		initialisePort();
	}
	
	private void initialiseDishWasher() throws Exception {
		dishWasherState = DishWasherState.OFF;
		washingMode = WashingMode.NORMAL;
		enableDrying = false;
		delayedProgram = new Timer(0, 0, 0);
		doorState = DoorState.CLOSE;
		waterQuantityInLiter = MAX_WATER_QUANTITY_IN_LITER;
	}
	
	private void initialisePort() throws Exception {
		this.dishWasherInternalControlInboundPort = 
				new DishWasherInternalControlInboundPort(URI_INTERNAL_CONTROL_INBOUND_PORT, this);
		this.dishWasherInternalControlInboundPort.publishPort();
		
		this.dishWasherUserControlInboundPort = 
				new DishWasherUserControlInboundPort(URI_USER_CONTROL_INBOUND_PORT, this);
		this.dishWasherUserControlInboundPort.publishPort();
		
		if(VERBOSE) {
			this.tracer.get().setTitle("Dishwasher component");
			this.tracer.get().setRelativePosition(0, 0);
			this.toggleTracing();
		}
	}
	
	/**
	 * 			LIFE CYCLE
	 */
	
	@Override 
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.dishWasherInternalControlInboundPort.unpublishPort();
			this.dishWasherUserControlInboundPort.unpublishPort();
		} catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	/**
	 * 				METHODES
	 */
	
	@Override
	public double getWaterQuantity() throws Exception {
		return waterQuantityInLiter;
	}

	@Override
	public void startWashing() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopWashing() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDoorOpen() throws Exception {
		if(doorState == DoorState.OPEN)
			return true;
		return false;
	}

	@Override
	public WashingMode getWashingMode() throws Exception {
		return washingMode;
	}

	@Override
	public Timer getTimer() throws Exception {
		return delayedProgram;
	}

	@Override
	public boolean isDryingModeEnable() throws Exception {
		return enableDrying;
	}

	@Override
	public int getEnergyConsumption() throws Exception {
		// TODO Auto-generated method stub
		return 0;
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

}
