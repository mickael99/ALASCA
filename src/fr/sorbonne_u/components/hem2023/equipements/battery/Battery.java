package fr.sorbonne_u.components.hem2023.equipements.battery;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.equipements.ModularEquipementI;
import fr.sorbonne_u.components.hem2023.equipements.battery.interfaces.BatteryI;
import fr.sorbonne_u.components.hem2023.equipements.battery.ports.BatteryProductionInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.production.interfaces.ProductionEquipmentI;

public class Battery extends AbstractComponent implements BatteryI, ProductionEquipmentI {
	public final boolean VERBOSE = true;
	
	public String uri;
	
	public final static String URI_PRODUCTION = "URI_PRODUCTION";
	public final static String URI_CONSOMMATION = "URI_CONSOMMATION";
	
	/** The battery can be in consomation or production mode				*/
	protected BATTERY_MODE batteryMode;
	public final static BATTERY_MODE INITIAL_BATTERY_MODE = 
			BATTERY_MODE.CONSOMATION;
	
	/** Ports */
	public BatteryProductionInboundPort batteryProductionInboundPort;
	
	public double electricityQuantity;
	public final double INIT_ELECTRICITY_QUANTITY = 500.0;
	public final double MAX_ELECTRICITY_QUANTITY = 1000.0;

	protected Battery(String uriId) throws Exception {
		super(uriId, 1, 0);
		this.uri = uriId;
		
		initialise();
	}
	
	protected void initialise() throws Exception {
		electricityQuantity = INIT_ELECTRICITY_QUANTITY;
		batteryMode = INITIAL_BATTERY_MODE;
		
		batteryProductionInboundPort = new BatteryProductionInboundPort(URI_PRODUCTION, this);
		batteryProductionInboundPort.publishPort();
		
		if(VERBOSE) {
			this.tracer.get().setTitle("Battery component");
			this.tracer.get().setRelativePosition(4, 1);
			this.toggleTracing();
			this.traceMessage("Battery is ready\n");
		}
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
		}catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}
	
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		if(VERBOSE)
			this.traceMessage("déconnexion des liaisons entre les ports\n\n");

		super.finalise();
	}
	
	@Override 
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			if(VERBOSE)
				this.traceMessage("supréssion des ports de la batterie\n\n");
			
			this.batteryProductionInboundPort.unpublishPort();
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
	public boolean addElectricityQuantity(double quantity) throws Exception {		
		if(electricityQuantity + quantity > MAX_ELECTRICITY_QUANTITY) {
			if(VERBOSE) 
				this.traceMessage("Impossible to get energy\n");
			return false;
		}
		
		if(VERBOSE) 
			this.traceMessage(quantity + " watts adding\n");
		electricityQuantity += quantity;
		return true;
	}

	@Override
	public boolean giveElectrictyToAModularEquipement(ModularEquipementI e, double quantity) throws Exception {
		return false;
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
