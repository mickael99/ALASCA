package fr.sorbonne_u.components.hem2023.equipements.dishWasher;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherInternalControlCI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherInternalControlI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherUserControlCI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherUserControlI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.ports.DishWasherInternalControlInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.ports.DishWasherUserControlInboundPort;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.exceptions.PreconditionException;

@OfferedInterfaces(offered={DishWasherUserControlCI.class, DishWasherInternalControlCI.class})
public class DishWasher extends AbstractComponent 
	implements DishWasherUserControlI, DishWasherInternalControlI {
	public static final boolean VERBOSE = false;
	
	public String Uri;
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
	protected boolean washing;
	protected boolean suspended;
	
	protected double waterQuantityInLiter;
	protected static final double MAX_WATER_QUANTITY_IN_LITER = 15.0;
	
	/**
	 * 
	 * 				CONSTRUCTORS
	 */
	
	protected DishWasher(String uriId) throws Exception {
		super(uriId, 1, 0);
		Uri = uriId;
		initialiseDishWasher();
		initialisePort();
		this.traceMessage("\n");
	}
	
	private void initialiseDishWasher() throws Exception {
		if(VERBOSE) {
			this.tracer.get().setTitle("Dishwasher component");
			this.tracer.get().setRelativePosition(1, 0);
			this.toggleTracing();
			
			this.traceMessage("Initialisation des variables du lave vaisselle\n");
		}
		
		dishWasherState = DishWasherState.OFF;
		washingMode = WashingMode.NORMAL;
		enableDrying = false;
		delayedProgram = new Timer(0, 0, 0);
		doorState = DoorState.CLOSE;
		waterQuantityInLiter = MAX_WATER_QUANTITY_IN_LITER;
		washing = false;
		suspended = false;
	}
	
	private void initialisePort() throws Exception {
		if(VERBOSE) 	
			this.traceMessage("Initialisation des ports du lave vaisselle\n");
		
		
		this.dishWasherInternalControlInboundPort = 
				new DishWasherInternalControlInboundPort(URI_INTERNAL_CONTROL_INBOUND_PORT, this);
		this.dishWasherInternalControlInboundPort.publishPort();
		
		this.dishWasherUserControlInboundPort = 
				new DishWasherUserControlInboundPort(URI_USER_CONTROL_INBOUND_PORT, this);
		this.dishWasherUserControlInboundPort.publishPort();
	}
	
	/**
	 * 			LIFE CYCLE
	 */
	
	@Override 
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			if(VERBOSE)
				this.traceMessage("Déconnexion des ports du lave vaisselle\n\n");
			
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
		if(VERBOSE)
			this.traceMessage("il reste " + waterQuantityInLiter + " litre(s) d'eau dans le reservoir\n\n");
		return waterQuantityInLiter;
	}
	
	@Override
	public boolean isWashing() throws Exception {
		return washing;
	}

	@Override
	public void startWashing() throws Exception {
		if(VERBOSE)
			this.traceMessage("On lance le lave vaisselle\n");
		
		assert !washing :
			new PreconditionException("impossible de lancer le lave vaisselle car il est déjà entrain de tourner");
		assert isOn() : 
			new PreconditionException("impossible de lancer le lave vaisselle car il est éteint");
		assert delayedProgram.isFinish() : 
			new PreconditionException("impossible de lancer le lave vaisselle car le minuteur n'est pas finis");
		assert !isDoorOpen() :
			new PreconditionException("impossible de lancer le lave vaisselle car la porte est ouverte");
		assert !isCuveWaterIsEmpty() :
			new PreconditionException("impossible de lancer le lave vaisselle car le reservoir est vide");
		
		washing = true;
		if(VERBOSE)
			this.traceMessage("\n");
	}

	@Override
	public void stopWashing() throws Exception {
		if(VERBOSE)
			this.traceMessage("On arrête le lave vaisselle\n");
		
		assert washing :
			new PreconditionException("impossible d'arrêter le lave vaisselle car il est déjà à l'arrêt");
		assert isOn() : 
			new PreconditionException("impossible d'arrêter le lave vaisselle car il est éteint");
		
		washing = false;
		if(VERBOSE)
			this.traceMessage("\n");
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
			this.traceMessage("Le lave vaisselle est en mode ON\n");
		
		dishWasherState = DishWasherState.ON;
		
		if(VERBOSE)
			this.traceMessage("\n");
	}

	@Override
	public void turnOff() throws Exception {
		if(VERBOSE)
			this.traceMessage("Le lave vaisselle est en mode OFF\n");
		
		dishWasherState = DishWasherState.OFF;
		
		if(VERBOSE)
			this.traceMessage("\n");
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
			this.traceMessage("Le lave vaisselle se met en mode " + washingMode.name() + "\n");
		
		this.washingMode = washingMode;
		
		if(VERBOSE)
			this.traceMessage("\n");
	}

	@Override
	public void enableDryingMode() throws Exception {
		if(VERBOSE)
			this.traceMessage("Le lave vaisselle active le mode sechage\n");
		
		enableDrying = true;
		
		if(VERBOSE)
			this.traceMessage("\n");
	}

	@Override
	public void disableDryingMode() throws Exception {
		if(VERBOSE)
			this.traceMessage("Le lave vaisselle désactive le mode sechage\n");
		
		enableDrying = false;
		
		if(VERBOSE)
			this.traceMessage("\n");
	}

	@Override
	public void scheduleWashing(Timer time) throws Exception {
		if(VERBOSE)
			this.traceMessage("Le lave vaisselle active un timer -> " + 
					time.getHeure() + ":" + time.getMinute() + ":" + time.getSeconde() + "\n");
		
		delayedProgram = time;
		
		if(VERBOSE)
			this.traceMessage("\n");
	}
	
	@Override
	public void removeTimer() throws Exception {
		this.delayedProgram.remove();
	}
	
	@Override
	public void openDoor() throws Exception {
		if(VERBOSE)
			this.traceMessage("La porte du lave vaisselle s'ouvre\n");
		
		doorState = DoorState.OPEN;
		if(washing) {
			if(VERBOSE)
				this.traceMessage
					("La porte est ouverte pendant que le lave vaisselle s'ouvre, arrêt d'urgence !\n");
		}
		
		if(VERBOSE)
			this.traceMessage("\n");
	}

	@Override
	public void closeDoor() throws Exception {
		if(VERBOSE)
			this.traceMessage("La porte du lave vaisselle se ferme\n");
		
		doorState = DoorState.CLOSE;
		
		if(VERBOSE)
			this.traceMessage("\n");
	}

	@Override
	public boolean fillWater(double waterQuantityToAdd) throws Exception {
		if(VERBOSE)
			this.traceMessage("Remplissage du réservoir du lave vaisselle de " + waterQuantityToAdd + " litre(s) d'eau\n");
			
		if(waterQuantityToAdd < 0.0 || waterQuantityInLiter + waterQuantityToAdd > MAX_WATER_QUANTITY_IN_LITER) {
			if(VERBOSE)
				this.traceMessage("Impossible de remplir le réservoir du lave vaisselle\n");			
			return false;
		}
		
		waterQuantityInLiter += waterQuantityToAdd;
		
		if(VERBOSE)
			this.traceMessage("\n");
		
		return true;
	}
	
	@Override
	public void fillWaterCompletely() throws Exception {
		if(VERBOSE)
			this.traceMessage("Remplissage du réservoir du lave vaisselle au maximum\n");
		
		waterQuantityInLiter = MAX_WATER_QUANTITY_IN_LITER;
		
		if(VERBOSE)
			this.traceMessage("\n");
	}
	
	@Override
	public boolean removeWaterQuantity(double waterQuantityToRemove) throws Exception {
		if(VERBOSE)
			this.traceMessage("Le réservoir perd " + waterQuantityToRemove + " litre(s) d'eau\n");
		
		if(waterQuantityToRemove < 0.0) {
			if(VERBOSE)
				this.traceMessage("Le réservoir ne perd pas d'eau car la quantité à retirer est négative\n");
			return false;
		}
		if(waterQuantityInLiter - waterQuantityToRemove < 0.0)
			waterQuantityInLiter = 0.0;
		else 
			waterQuantityInLiter -= waterQuantityToRemove;
		
		if(VERBOSE)
			this.traceMessage("\n");
		
		return true;
	}
	
	@Override
	public boolean isCuveWaterIsEmpty() throws Exception {
		if(waterQuantityInLiter == 0.0) {
			if(VERBOSE)
				this.traceMessage("Le réservoir est vide\n");
			return true;
		}
		
		if(VERBOSE)
			this.traceMessage("\n");
		
		return false;
	}
	
	public boolean isSuspended() throws Exception {
		if(VERBOSE)
			this.traceMessage("test si le lave vaisselle est suspendu\n");
		return suspended;
	}
	
	@Override 
	public boolean suspend() throws Exception {
		if(suspended) {
			if(VERBOSE)
				this.traceMessage("le lave vaissel est déjà suspendu\n");
			return false;
		}
		if(VERBOSE)
			this.traceMessage("suspension du lave vaisselle\n");
		suspended = true;
		return true;
	}
	
	@Override 
	public boolean resume() throws Exception {
		if(!suspended) {
			if(VERBOSE)
				this.traceMessage("le lave vaissel est déjà lancé\n");
			return false;
		}
		if(VERBOSE)
			this.traceMessage("redémarrage du lave vaisselle\n");
		suspended = false;
		return true;
	}
	
	@Override 
	public double emergency() throws Exception {
		if((isDoorOpen() && isWashing()) || (isWashing() && isCuveWaterIsEmpty()))
			return 1.0;
		return 0.0;
	}
}