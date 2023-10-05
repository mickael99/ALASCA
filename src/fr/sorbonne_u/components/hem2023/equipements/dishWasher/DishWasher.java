package fr.sorbonne_u.components.hem2023.equipements.dishWasher;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors.DishWasherInternalControlInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors.DishWasherUserControlInboundPort;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.exceptions.PreconditionException;

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
	protected boolean isWashing;
	
	protected double waterQuantityInLiter;
	protected static final double MAX_WATER_QUANTITY_IN_LITER = 15.0;
	
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
		if(VERBOSE)
			this.traceMessage("Initialisation des variables du lave vaisselle");
		
		dishWasherState = DishWasherState.OFF;
		washingMode = WashingMode.NORMAL;
		enableDrying = false;
		delayedProgram = new Timer(0, 0, 0);
		doorState = DoorState.CLOSE;
		waterQuantityInLiter = MAX_WATER_QUANTITY_IN_LITER;
		isWashing = false;
	}
	
	private void initialisePort() throws Exception {
		if(VERBOSE)
			this.traceMessage("Initialisation des ports du lave vaisselle");
		
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
			if(VERBOSE)
				this.traceMessage("Déconnexion des ports du lave vaisselle");
			
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
		if(VERBOSE)
			this.traceMessage("On lance le lave vaisselle");
		
		assert !isWashing :
			new PreconditionException("impossible de lancer le lave vaisselle car il est déjà entrain de tourner");
		assert isOn() : 
			new PreconditionException("impossible de lancer le lave vaisselle car il est éteint");
		assert delayedProgram.isFinish() : 
			new PreconditionException("impossible de lancer le lave vaisselle car il le minuteur n'est pas finis");
		assert !isDoorOpen() :
			new PreconditionException("impossible de lancer le lave vaisselle car la porte est ouverte");
		
		isWashing = true;
	}

	@Override
	public void stopWashing() throws Exception {
		if(VERBOSE)
			this.traceMessage("On arrête le lave vaisselle");
		
		assert isWashing :
			new PreconditionException("impossible d'arrêter le lave vaisselle car il est déjà à l'arrêt");
		assert isOn() : 
			new PreconditionException("impossible d'arrêter le lave vaisselle car il est éteint");
		
		isWashing = false;
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
		if(!isOn())
			return 0;
		
		int energyConsumption = 0;
		switch(washingMode) {
			case ECO:
				energyConsumption = 1200;
				break;
			case FAST:
				energyConsumption = 1000;
				break;
			case NORMAL: 
				energyConsumption = 1500;
				break;
			case INTENSIF:
				energyConsumption = 1800;
				break;
			default:
				throw new Exception("probleme, impossible de récuperer la consommation d'énergie du lave vaisselle");
		}
		
		if(this.enableDrying)
			energyConsumption += 1000;
		return energyConsumption;
	}

	@Override
	public void turnOn() throws Exception {
		if(VERBOSE)
			this.traceMessage("Le lave vaisselle est en mode ON");
		
		dishWasherState = DishWasherState.ON;
	}

	@Override
	public void turnOff() throws Exception {
		if(VERBOSE)
			this.traceMessage("Le lave vaisselle est en mode OFF");
		
		dishWasherState = DishWasherState.OFF;
	}

	@Override
	public boolean isOn() throws Exception {
		if(dishWasherState == DishWasherState.ON)
			return true;
		return false;
	}

	@Override
	public void setWashingMode(WashingMode washingMode) throws Exception {
		if(VERBOSE)
			this.traceMessage("Le lave vaisselle se met en mode " + washingMode.name());
		
		this.washingMode = washingMode;
	}

	@Override
	public void enableDryingMode() throws Exception {
		if(VERBOSE)
			this.traceMessage("Le lave vaisselle active le mode sechage");
		
		enableDrying = true;
	}

	@Override
	public void disableDryingMode() throws Exception {
		if(VERBOSE)
			this.traceMessage("Le lave vaisselle désactive le mode sechage");
		
		enableDrying = false;
	}

	@Override
	public void scheduleWashing(Timer time) throws Exception {
		if(VERBOSE)
			this.traceMessage("Le lave vaisselle active un timer -> " + 
					time.getHeure() + ":" + time.getMinute() + ":" + time.getSeconde());
		
		delayedProgram = time;
	}

	@Override
	public void openDoor() throws Exception {
		if(VERBOSE)
			this.traceMessage("La porte du lave vaisselle s'ouvre");
		
		doorState = DoorState.OPEN;
		if(isWashing) {
			if(VERBOSE)
				this.traceMessage
					("La porte est ouverte pendant que le lave vaisselle s'ouvre, arrêt d'urgence !");
			stopWashing();
		}
	}

	@Override
	public void closeDoor() throws Exception {
		if(VERBOSE)
			this.traceMessage("La porte du lave vaisselle se ferme");
		
		doorState = DoorState.CLOSE;
	}

	@Override
	public boolean fillWater(int waterQuantityToAdd) throws Exception {
		if(VERBOSE)
			this.traceMessage("Remplissage du réservoir du lave vaisselle");
			
		if(waterQuantityToAdd < 0 || waterQuantityInLiter + waterQuantityToAdd > MAX_WATER_QUANTITY_IN_LITER) {
			if(VERBOSE)
				this.traceMessage("Impossible de remplir le réservoir du lave vaisselle");			
			return false;
		}
		
		waterQuantityInLiter += waterQuantityToAdd;
		return true;
	}
	
	public boolean removeWaterQuantity(double waterQuantityToRemove) throws Exception {
		if(VERBOSE)
			this.traceMessage("Le réservoir perd de l'eau");
		if(waterQuantityToRemove < 0) {
			if(VERBOSE)
				this.traceMessage("Le réservoir ne perd pas d'eau car la quantité à retirer est négative");
			return false;
		}
		if(waterQuantityInLiter - waterQuantityToRemove < 0)
			waterQuantityInLiter = 0;
		else 
			waterQuantityInLiter -= waterQuantityToRemove;
		return true;
	}
	

}
