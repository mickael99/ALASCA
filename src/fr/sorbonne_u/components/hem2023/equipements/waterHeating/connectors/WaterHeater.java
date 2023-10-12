package fr.sorbonne_u.components.hem2023.equipements.waterHeating.connectors;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterExternalControlI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterUserControlI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.ports.WaterHeaterExternalControlInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.ports.WaterHeaterUserControlInboundPort;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.exceptions.PreconditionException;

public class WaterHeater extends AbstractComponent implements WaterHeaterExternalControlI, WaterHeaterUserControlI {
	public static final boolean VERBOSE = true;
	
	public static final String URI_EXTERNAL_CONTROL_PORT = "URI_EXTERNAL_CONTROL_PORT";
	public static final String URI_USER_CONTROL_PORT = "URI_USER_CONTROL_PORT";
	protected WaterHeaterExternalControlInboundPort waterHeaterExternalControlInboundPort;
	protected WaterHeaterUserControlInboundPort waterHeaterUserControlInboundPort;
	
	public static final int INITIALISE_TEMPERATURE = 50;
	public static final int MIN_TEMPERATURE = 45;
	public static final int MAX_TEMPERATURE = 60;
	protected int targetTemperature;
	protected int currentTemperature;
	
	protected Timer delayedProgram;
	
	protected WaterHeaterState state;
	protected WaterHeaterPowerLevel powerLevel; 
	
	protected WaterHeater() throws Exception {
		super(1, 0);
		initialiseWaterHeater();
		initialisePort();
		this.traceMessage("\n");
	}
	
	protected WaterHeater(String uriId) throws Exception {
		super(uriId, 1, 0);
		initialiseWaterHeater();
		initialisePort();
		this.traceMessage("\n");
	}
	
	private void initialiseWaterHeater() throws Exception {
		if(VERBOSE) {
			this.tracer.get().setTitle("WaterHeater component");
			this.tracer.get().setRelativePosition(0, 0);
			this.toggleTracing();
			
			this.traceMessage("Initialisation des variables du chauffe eau\n");
		}
		targetTemperature = INITIALISE_TEMPERATURE;
		currentTemperature = INITIALISE_TEMPERATURE;
		
		delayedProgram = new Timer(0, 0, 0);
		
		powerLevel = WaterHeaterPowerLevel.LOW;
		state = WaterHeaterState.OFF;
	}
	
	private void initialisePort() throws Exception {
		if(VERBOSE) 	
			this.traceMessage("Initialisation des ports du lave vaisselle\n");
		
		this.waterHeaterExternalControlInboundPort = 
				new WaterHeaterExternalControlInboundPort(URI_EXTERNAL_CONTROL_PORT, this);
		this.waterHeaterExternalControlInboundPort.publishPort();
		
		this.waterHeaterUserControlInboundPort = 
				new WaterHeaterUserControlInboundPort(URI_USER_CONTROL_PORT, this);
		this.waterHeaterUserControlInboundPort.publishPort();
	}
	
	/**
	 * 			LIFE CYCLE
	 */
	
	@Override 
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			if(VERBOSE)
				this.traceMessage("Déconnexion des ports du chauffe eau\n\n");
			
			this.waterHeaterExternalControlInboundPort.unpublishPort();
			this.waterHeaterUserControlInboundPort.unpublishPort();
		} catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	/**
	 * 			AUTRES METHODES
	 */
	
	@Override
	public Timer getTimer() throws Exception {
		return delayedProgram;
	}

	@Override
	public void turnOn() throws Exception {
		state = WaterHeaterState.ON;
	}

	@Override
	public void turnOff() throws Exception {
		state = WaterHeaterState.OFF;
	}

	@Override
	public boolean isOn() throws Exception {
		if(state == WaterHeaterState.ON)
			return true;
		return false;
	}

	@Override
	public int getTargetTemperature() throws Exception {
		return targetTemperature;
	}
	
	@Override
	public void setTargetWaterTemperature(int degree) throws Exception {
		assert degree >= MIN_TEMPERATURE && degree <= MAX_TEMPERATURE :
			new PreconditionException("la température de l'eau n'est pas valide");
		targetTemperature = degree;
	}
	
	@Override
	public int getCurrentTemperature() throws Exception {
		return currentTemperature;
	}
	
	@Override
	public void setPowerLevel(WaterHeaterPowerLevel power) throws Exception {
		powerLevel = power;
	}

	@Override
	public double getPowerLevel() throws Exception {
		switch(powerLevel) {
			case LOW: 
				return 2500.0;
			case NORMAL:
				return 4000.0;
			case HIGH:
				return 5000.0;
			default:
				throw new Exception();
		}
	}

	@Override
	public void scheduleHeating(Timer launchTime, Timer endTime) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public int getEnergyConsumption() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void heating() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void startHeating() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopHeating() throws Exception {
		// TODO Auto-generated method stub

	}

}
