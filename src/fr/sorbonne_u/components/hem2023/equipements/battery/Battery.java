package fr.sorbonne_u.components.hem2023.equipements.battery;

import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.equipements.battery.interfaces.BatteryI;
import fr.sorbonne_u.components.hem2023.equipements.battery.ports.BatteryProductionInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.consomation.ports.ConsomationEquimentOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.DishWasher;
import fr.sorbonne_u.components.hem2023.equipements.production.connectors.ProductionEquipmentConnector;
import fr.sorbonne_u.components.hem2023.equipements.production.interfaces.ProductionEquipmentI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.WaterHeater;

public class Battery extends AbstractComponent implements BatteryI, ProductionEquipmentI {
	public final boolean VERBOSE = true;
	
	protected TEST_TYPE testType;
	
	public String uri;
	
	public final static String URI_PRODUCTION = "URI_PRODUCTION";
	public final static String URI_CONSOMMATION = "URI_CONSOMMATION";
	
	/** The battery can be in consomation or production mode				*/
	protected BATTERY_MODE batteryMode;
	public final static BATTERY_MODE INITIAL_BATTERY_MODE = 
			BATTERY_MODE.PRODUCTION;
	
	/** Ports */
	public BatteryProductionInboundPort batteryProductionInboundPort;
	
	public HashMap<String, ConsomationEquimentOutboundPort> consomationOutboundPorts;
	
	public double electricityQuantity;
	public final double INIT_ELECTRICITY_QUANTITY = 500.0;
	public final double MAX_ELECTRICITY_QUANTITY = 50000000.0;

	protected Battery(String uriId, TEST_TYPE testType) throws Exception {
		super(uriId, 1, 0);
		this.uri = uriId;
		this.testType = testType;
		
		initialise();
		
		if(testType == TEST_TYPE.ALL)
			publishPortToModularEquipments();
	}
	
	protected void initialise() throws Exception {
		electricityQuantity = INIT_ELECTRICITY_QUANTITY;
		batteryMode = INITIAL_BATTERY_MODE;
				
		batteryProductionInboundPort = new BatteryProductionInboundPort(URI_PRODUCTION, this);
		batteryProductionInboundPort.publishPort();
		
		if(testType == TEST_TYPE.ALL)
			consomationOutboundPorts = new HashMap<String, ConsomationEquimentOutboundPort>();

		if(VERBOSE) {
			this.tracer.get().setTitle("Battery component");
			this.tracer.get().setRelativePosition(4, 1);
			this.toggleTracing();
			this.traceMessage("Battery is ready\n");
		}
	}
	
	protected void publishPortToModularEquipments() throws Exception {
		ConsomationEquimentOutboundPort c1, c2;
		c1 = new ConsomationEquimentOutboundPort(this);
		c1.publishPort();
		c2 = new ConsomationEquimentOutboundPort(this);
		c2.publishPort();
		
		this.consomationOutboundPorts.put(WaterHeater.Uri, c1);
		this.consomationOutboundPorts.put(DishWasher.Uri, c2);
	}
	
	/**
	 * 		CYCLE DE VIE
	 */
	
	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();		
		try {
			if(VERBOSE)
				this.traceMessage("connexion des ports de la batterie\n\n");
			
			if(testType == TEST_TYPE.ALL) {
				this.doPortConnection(this.consomationOutboundPorts.get(WaterHeater.Uri).getPortURI(), 
						WaterHeater.URI_PRODUCTION_PORT, 
						ProductionEquipmentConnector.class.getCanonicalName());
				
				this.doPortConnection(this.consomationOutboundPorts.get(DishWasher.Uri).getPortURI(), 
						DishWasher.URI_PRODUCTION_PORT, 
						ProductionEquipmentConnector.class.getCanonicalName());
			}
			
		}catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}
	
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		
		if(VERBOSE)
			this.traceMessage("consomation -> " + this.electricityQuantity);
		
		this.setConsomationMode();
		
		if(this.sendBatteryToAModularEquipment(WaterHeater.Uri, 1000.0) && 			
				this.sendBatteryToAModularEquipment(DishWasher.Uri, 1000.0)) {
			if(VERBOSE)
				this.traceMessage("succesfull !!!");
		}
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		if(VERBOSE)
			this.traceMessage("déconnexion des liaisons entre les ports\n\n");
		
		if(testType == TEST_TYPE.ALL) {
			for(ConsomationEquimentOutboundPort c : this.consomationOutboundPorts.values())
				this.doPortDisconnection(c.getPortURI());
		}

		super.finalise();
	}
	
	@Override 
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			if(VERBOSE)
				this.traceMessage("supréssion des ports de la batterie\n\n");
			
			this.batteryProductionInboundPort.unpublishPort();
			
			if(testType == TEST_TYPE.ALL) {
				for(ConsomationEquimentOutboundPort c : this.consomationOutboundPorts.values())
					c.unpublishPort();
			}
			
		} catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
		
		super.shutdown();
	}
	
	/**
	 * 
	 * 				METHODES
	 * 
	 */
	
	@Override
	public double getElectricityQuantity() throws Exception {
		return electricityQuantity;
	}

	@Override
	public synchronized boolean addElectricityQuantity(double quantity) throws Exception {
		assert this.batteryMode == BATTERY_MODE.PRODUCTION;
		
		if(electricityQuantity + quantity > MAX_ELECTRICITY_QUANTITY) {
			if(VERBOSE) 
				this.traceMessage("Impossible to get energy\n");
			return false;
		}
		
		if(VERBOSE) {
			this.traceMessage(quantity + " watts adding\n");
			this.traceMessage(this.electricityQuantity + quantity + " total\n");
		}
		electricityQuantity += quantity;
		return true;
	}

	@Override
	public boolean sendBatteryToAModularEquipment(String uri, double quantity) throws Exception {
		if(VERBOSE)
			this.traceMessage("prepare to send " + quantity + " watts to a modular equipment");
			
		assert this.batteryMode == BATTERY_MODE.CONSOMATION &&
					this.consomationOutboundPorts.containsKey(uri);
		
		if(this.electricityQuantity - quantity < 0) {
			if(VERBOSE)
				this.traceMessage("impossible to send battery to a modular equipment");
			
			return false;
		}
				
		return this.consomationOutboundPorts.get(uri).sendBattery(quantity);
	}

	@Override
	public boolean isFull() throws Exception {
		return electricityQuantity == MAX_ELECTRICITY_QUANTITY;
	}

	@Override
	public boolean isEmpty() throws Exception {
		return electricityQuantity == 0;
	}

	@Override
	public void setProductionMode() throws Exception {
		this.batteryMode = BATTERY_MODE.PRODUCTION;
	}

	@Override
	public void setConsomationMode() throws Exception {
		this.batteryMode = BATTERY_MODE.CONSOMATION;
	}
}
