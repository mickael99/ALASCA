package fr.sorbonne_u.components.hem2023.equipements.waterHeating;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterExternalControlI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterUserControlI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.ports.WaterHeaterExternalControlInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.ports.WaterHeaterUserControlInboundPort;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.components.hem2023.equipements.ModularEquipementI;
import fr.sorbonne_u.components.hem2023.equipements.hem.HEM;
import fr.sorbonne_u.components.hem2023.equipements.hem.registration.RegistrationConnector;
import fr.sorbonne_u.components.hem2023.equipements.hem.registration.RegistrationOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterExternalControlCI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterUserControlCI;

@OfferedInterfaces(offered={WaterHeaterUserControlCI.class, WaterHeaterExternalControlCI.class})
public class WaterHeater extends AbstractComponent 
	implements WaterHeaterExternalControlI, WaterHeaterUserControlI, ModularEquipementI {
	public static final boolean VERBOSE = true;
	protected String path2xmlControlAdapter;
	private boolean registrationRequired = true;
	
	public static String Uri ;
	public static final String URI_EXTERNAL_CONTROL_INBOUND_PORT = "URI_EXTERNAL_CONTROL_INBOUND_PORT";
	public static final String URI_USER_CONTROL_INBOUND_PORT = "URI_USER_CONTROL_INBOUND_PORT";
	public static final String URI_REGISTRATION_OUTBOUND_PORT = "URI_REGISTRATION_OUTBOUND_PORT";
	protected WaterHeaterExternalControlInboundPort waterHeaterExternalControlInboundPort;
	protected WaterHeaterUserControlInboundPort waterHeaterUserControlInboundPort;
	protected RegistrationOutboundPort registrationOutboundPort;
	
	public static final int INITIALISE_TEMPERATURE = 50;
	public static final int MIN_TEMPERATURE = 45;
	public static final int MAX_TEMPERATURE = 60;
	protected int targetTemperature;
	protected int currentTemperature;
	
	protected int energyConsumption;
	
	protected Timer delayedProgram;
	
	protected WaterHeaterState state;
	protected WaterHeaterPowerLevel powerLevel;
	
	protected boolean heating;
	protected boolean suspended;
	
	protected WaterHeater(String uriId) throws Exception {
		super(uriId, 1, 0);
		Uri = uriId;
		initialiseWaterHeater();
		initialisePort();
		this.traceMessage("\n");
	}
	
	protected WaterHeater(String uriId, boolean registrationRequired) throws Exception {
		this(uriId);
		this.registrationRequired = registrationRequired;
	}
	
	private void initialiseWaterHeater() throws Exception {
		if(VERBOSE) {
			this.tracer.get().setTitle("WaterHeater component");
			this.tracer.get().setRelativePosition(2, 0);
			this.toggleTracing();
			
			this.traceMessage("Initialisation des variables du chauffe eau\n");
		}
		targetTemperature = INITIALISE_TEMPERATURE;
		currentTemperature = INITIALISE_TEMPERATURE;
		
		delayedProgram = new Timer(0, 0, 0);
		
		powerLevel = WaterHeaterPowerLevel.LOW;
		state = WaterHeaterState.OFF;
		energyConsumption = 0;
		
		heating = false;
		suspended = false;
		
		this.path2xmlControlAdapter = new String("waterheater-descriptor.xml");
	}
	
	private void initialisePort() throws Exception {
		if(VERBOSE) 	
			this.traceMessage("Initialisation des ports du lave vaisselle\n\n");
		
		this.waterHeaterExternalControlInboundPort = 
				new WaterHeaterExternalControlInboundPort(URI_EXTERNAL_CONTROL_INBOUND_PORT, this);
		this.waterHeaterExternalControlInboundPort.publishPort();
		
		this.waterHeaterUserControlInboundPort = 
				new WaterHeaterUserControlInboundPort(URI_USER_CONTROL_INBOUND_PORT, this);
		this.waterHeaterUserControlInboundPort.publishPort();
		
		if(registrationRequired) {
			this.registrationOutboundPort = 
					new RegistrationOutboundPort(URI_REGISTRATION_OUTBOUND_PORT, this);
			this.registrationOutboundPort.publishPort();
		}
	}
	
	/**
	 * 			LIFE CYCLE
	 */
	
	@Override 
	public synchronized void start() throws ComponentStartException {
		super.start();
		try {
			if(VERBOSE)
				this.traceMessage("Connexion des ports\n");
		
			if(registrationRequired) 
				this.doPortConnection(registrationOutboundPort.getPortURI(), HEM.URI_REGISTRATION_INBOUND_PORT, 
						RegistrationConnector.class.getCanonicalName());
			
			//inscription du chauffe eau au gestionnaire d'energie
			if(this.registrationRequired) {
				if(VERBOSE)
					this.traceMessage("Inscription du chauffe eau au gestionnaire d'energie\n");
				this.register();
			}
			
		} catch(Exception e) {
			throw new ComponentStartException(e);
		}
	}
	
	@Override
	public synchronized void execute() throws Exception {
		if(this.registrationRequired) {
			if(VERBOSE)
				this.traceMessage("Test si le chauffe eau est bien enregistré au gestionnaire\n\n");
			if(!this.registered())
				this.traceMessage("Chauffe eau non connecté\n\n");
		}
		super.execute();
	}
	
	@Override 
	public synchronized void finalise() throws Exception {
		if(VERBOSE) 
			this.traceMessage("Déconnexion des liaisons entre les ports\n\n");
		
		if(registrationRequired) {
			this.unregister();
			this.doPortDisconnection(this.registrationOutboundPort.getPortURI());
		}
		
		super.finalise();
	}
	
	@Override 
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			if(VERBOSE)
				this.traceMessage("Déconnexion des ports du chauffe eau\n\n");
			
			this.waterHeaterExternalControlInboundPort.unpublishPort();
			this.waterHeaterUserControlInboundPort.unpublishPort();
			
			if(registrationRequired) 
				this.registrationOutboundPort.unpublishPort();
		} catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	/**
	 * 			AUTRES METHODES
	 */
	
	public String getUri() {
		return Uri;
	}
	
	@Override
	public Timer getTimer() throws Exception {
		if(VERBOSE) 
			this.traceMessage("il reste " + delayedProgram + "\n\n");
		return delayedProgram;
	}

	@Override
	public void turnOn() throws Exception {
		if(VERBOSE) 
			this.traceMessage("Le chauffe eau est allumé\n\n");
		state = WaterHeaterState.ON;
	}

	@Override
	public void turnOff() throws Exception {
		if(VERBOSE) 
			this.traceMessage("Le chauffe eau est éteint\n\n");
		state = WaterHeaterState.OFF;
	}

	@Override
	public boolean isOn() throws Exception {
		if(VERBOSE) 
			this.traceMessage("Test si le chauffe eau est éteint ou allumé\n\n");
		if(state == WaterHeaterState.ON)
			return true;
		return false;
	}

	@Override
	public int getTargetTemperature() throws Exception {
		if(VERBOSE) 
			this.traceMessage("la température cible est de " + targetTemperature + " degrés\n\n");
		return targetTemperature;
	}
	
	@Override
	public void setTargetWaterTemperature(int degree) throws Exception {
		if(VERBOSE) 
			this.traceMessage("changement de la température cible à " + degree + " degrés\n\n");
		assert degree >= currentTemperature && degree <= MAX_TEMPERATURE:
			new PreconditionException("la température de l'eau n'est pas valide");
		targetTemperature = degree;
	}
	
	@Override
	public int getCurrentTemperature() throws Exception {
		if(VERBOSE) 
			this.traceMessage("la température courante est de " + currentTemperature + " degrés\n\n");
		return currentTemperature;
	}
	
	@Override
	public void setPowerLevel(WaterHeaterPowerLevel power) throws Exception {
		powerLevel = power;
		if(VERBOSE) 
			this.traceMessage("changement du niveau de puissance à " + powerLevel +  " watts\n\n");
	}

	@Override
	public double getPowerLevel() throws Exception {
		double currentPowerLevel;
		switch(powerLevel) {
			case LOW: 
				currentPowerLevel = 2500.0;
				break;
			case NORMAL:
				currentPowerLevel = 4000.0;
				break;
			case HIGH:
				currentPowerLevel = 5000.0;
				break;
			default:
				throw new Exception();
		}
		
		if(VERBOSE) 
			this.traceMessage("le niveau de puissance est à " + currentPowerLevel +  " watts\n\n");
		return currentPowerLevel;
	}

	@Override
	public void scheduleHeating(Timer launchTime, Timer endTime) throws Exception {
		delayedProgram = launchTime.differenceBeetweenTwoTimer(endTime);
		if(VERBOSE) 
			this.traceMessage("lancement du chauffe eau dans " + delayedProgram + "\n\n");
	}

	@Override
	public int getEnergyConsumption() throws Exception {
		if(VERBOSE) 
			this.traceMessage("la consommation d'energie est à " + energyConsumption + " watts" + "\n\n");
		return energyConsumption;
	}

	@Override
	public boolean isHeating() throws Exception {
		if(VERBOSE) 
			this.traceMessage("test si le chauffe eau est en route ou non\n\n");
		if(this.currentTemperature < this.targetTemperature)
			return true;
		return false;
	}

	@Override
	public void startHeating() throws Exception {
		if(VERBOSE) 
			this.traceMessage("lancement du chauffe eau\n\n");
		assert isOn() : 
			new PreconditionException("impossible de démarrer le chauffe eau car il n'est pas allumé");
		assert delayedProgram.isFinish() : 
			new PreconditionException("impossible de démarrer le chauffe eau car le timer n'est pas terminé");
		
		heating = true;
	}

	@Override
	public void stopHeating() throws Exception {
		if(VERBOSE) 
			this.traceMessage("arrêt du chauffe eau\n\n");
		heating = false;
	}
	
	@Override
	public void removeTimer() throws Exception {
		delayedProgram.remove();
	}
	
	@Override
	public WaterHeaterPowerLevel getWaterHeaterPowerLevel() throws Exception {
		return powerLevel;
	}
	
	@Override
	public boolean suspended() throws Exception {
		return suspended;
	}
	
	@Override
	public boolean suspend() throws Exception {
		if(suspended) {
			if(VERBOSE)
				this.traceMessage("le chauffe eau est déjà suspendu\n\n");
			return false;
		}
		if(VERBOSE)
			this.traceMessage("suspension du chauffe eau\n\n");
		suspended = true;
		return true;
	}
	
	@Override
	public boolean resume() throws Exception {
		if(!suspended) {
			if(VERBOSE)
				this.traceMessage("le chauffe eau est déjà lancé\n\n");
			return false;
		}
		if(VERBOSE)
			this.traceMessage("redémarrage du chauffe eau\n\n");
		suspended = false;
		return true;
	}
	
	@Override
	public double emergency() throws Exception {
		double delta = Math.abs(targetTemperature - currentTemperature);
		double ret = -1.0;
		if (currentTemperature < MIN_TEMPERATURE || delta >= MAX_TEMPERATURE) 
			ret = 1.0;
		else 
			ret = delta / MAX_TEMPERATURE;
	
		return ret;
	}


	/**
	 * 		S'ENREGISTRER ET SE DESENREGISTRER DU GESTIONNAIRE
	 */
	
	public boolean registered() throws Exception {
		return this.registrationOutboundPort.registered(Uri);
	}

	public boolean register() throws Exception {
		return this.registrationOutboundPort.register
				(Uri, URI_EXTERNAL_CONTROL_INBOUND_PORT, this.path2xmlControlAdapter);
	}

	public void unregister() throws Exception {
		this.registrationOutboundPort.unregister(Uri);
	}
}