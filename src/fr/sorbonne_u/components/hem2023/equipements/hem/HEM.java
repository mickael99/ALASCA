package fr.sorbonne_u.components.hem2023.equipements.hem;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.DishWasher;
import fr.sorbonne_u.components.hem2023.equipements.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2023.equipements.meter.connectors.ElectricMeterConnector;
import fr.sorbonne_u.components.hem2023.equipements.meter.ports.ElectricMeterOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.WaterHeater;

public class HEM extends AbstractComponent {
	public static final boolean VERBOSE = true;
	
	public static final String URI_ELECTRIC_METER_PORT = "URI_ELECTRIC_METER_PORT";
	public static final String URI_DISH_WASHER_PORT = "URI_DISH_WASHER_PORT";
	public static final String URI_WATER_HEATER_PORT = "URI_WATER_HEATER_PORT";

	protected ElectricMeterOutboundPort electricMeterOutboundPort; 
	protected AdjustableOutboundPort adjustableOutboundPortForDishWasher;
	protected AdjustableOutboundPort adjustableOutboundPortForWaterHeater;

	/**
	 * 
	 * 				CONSTRUCTORS
	 */
	public HEM() throws Exception {
		super(1, 1);
		initialisePort();	
	}
	
	public HEM(String uriId) throws Exception {
		super(uriId, 1, 1);
		initialisePort();
	}
	
	private void initialisePort() throws Exception {
		electricMeterOutboundPort = new ElectricMeterOutboundPort(URI_ELECTRIC_METER_PORT, this);
		adjustableOutboundPortForDishWasher = new AdjustableOutboundPort(URI_DISH_WASHER_PORT, this);
		adjustableOutboundPortForWaterHeater = new AdjustableOutboundPort(URI_WATER_HEATER_PORT, this);
		
		electricMeterOutboundPort.publishPort();
		adjustableOutboundPortForDishWasher.publishPort();
		adjustableOutboundPortForWaterHeater.publishPort();
		
		if(VERBOSE) {
			this.tracer.get().setTitle("Home Energy Manager component");
			this.tracer.get().setRelativePosition(2, 0);
			this.toggleTracing();
			this.traceMessage("\n");
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
				this.traceMessage("connexion des ports du gestionnaire");
			
			this.doPortConnection(electricMeterOutboundPort.getPortURI(), 
					ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI, 
					ElectricMeterConnector.class.getCanonicalName());
			
			this.doPortConnection(adjustableOutboundPortForDishWasher.getPortURI(), 
					DishWasher.URI_INTERNAL_CONTROL_INBOUND_PORT, 
					DishWasherConnector.class.getCanonicalName());
			
			this.doPortConnection(adjustableOutboundPortForWaterHeater.getPortURI(), 
					WaterHeater.URI_EXTERNAL_CONTROL_INBOUND_PORT, 
					WaterHeaterConnector.class.getCanonicalName());
		}catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}
	
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		runTest();
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		if(VERBOSE)
			this.traceMessage("déconnexion des ports du gestionnaire");
		this.doPortDisconnection(electricMeterOutboundPort.getPortURI());
		this.doPortDisconnection(adjustableOutboundPortForDishWasher.getPortURI());
		this.doPortDisconnection(adjustableOutboundPortForWaterHeater.getPortURI());
		
		super.finalise();
	}
	
	@Override 
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			if(VERBOSE)
				this.traceMessage("supréssion des ports du gestionnaire d'energie\n\n");
			
			this.electricMeterOutboundPort.unpublishPort();
			this.adjustableOutboundPortForDishWasher.unpublishPort();
			this.adjustableOutboundPortForWaterHeater.unpublishPort();
		} catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	/**
	 * 			METHODES DE TEST
	 */
	
	public void runTest() throws Exception {
		testMeter();
		testDishWasher();
		testWaterHeater();
	}
	
	public void testMeter() throws Exception {
		double currentConsumption = this.electricMeterOutboundPort.getCurrentConsumption();
		double currentProduction = this.electricMeterOutboundPort.getCurrentProduction();
		
		if(VERBOSE) {
			this.traceMessage("la consommation total est de " + currentConsumption + " watts");
			this.traceMessage("la production total est de " + currentProduction + " watts");
		}
	}
	
	public void testDishWasher() throws Exception {
		if(VERBOSE) {
			this.traceMessage("test de la connexion entre le lave vaisselle et le gestionnaire\n");
			
			this.traceMessage("le mode maximum est de -> " + 
					this.adjustableOutboundPortForDishWasher.maxMode() + "\n\n");
			
			this.traceMessage("le lave vaisselle est en mode " + 
					this.adjustableOutboundPortForDishWasher.currentMode() + "\n\n");
			
			this.traceMessage("On augmente le mode\n");
			this.adjustableOutboundPortForDishWasher.upMode();
			this.traceMessage("le lave vaisselle est en mode " + 
					this.adjustableOutboundPortForDishWasher.currentMode() + "\n\n");
			
			this.traceMessage("On diminue le mode\n");
			this.adjustableOutboundPortForDishWasher.downMode();
			this.traceMessage("le lave vaisselle est en mode " + 
					this.adjustableOutboundPortForDishWasher.currentMode() + "\n\n");
			
			this.traceMessage("On change le mode\n");
			this.adjustableOutboundPortForDishWasher.setMode(3);
			this.traceMessage("le lave vaisselle est en mode " + 
					this.adjustableOutboundPortForDishWasher.currentMode() + "\n\n");	
		}
	}
	
	public void testWaterHeater() throws Exception {
		if(VERBOSE) {
			this.traceMessage("test de la connexion entre le chauffe eau et le gestionnaire\n");
			
			this.traceMessage("le mode maximum est de -> " + 
					this.adjustableOutboundPortForWaterHeater.maxMode() + "\n\n");
			
			this.traceMessage("le chauffe eau est en mode " + 
					this.adjustableOutboundPortForWaterHeater.currentMode() + "\n\n");
			
			this.traceMessage("On augmente le mode\n");
			this.adjustableOutboundPortForWaterHeater.upMode();
			this.traceMessage("le chauff eeau est en mode " + 
					this.adjustableOutboundPortForWaterHeater.currentMode() + "\n\n");
			
			this.traceMessage("On diminue le mode\n");
			this.adjustableOutboundPortForWaterHeater.downMode();
			this.traceMessage("le chauffe eau est en mode " + 
					this.adjustableOutboundPortForWaterHeater.currentMode() + "\n\n");
			
			this.traceMessage("On change le mode\n");
			this.adjustableOutboundPortForWaterHeater.setMode(2);
			this.traceMessage("le chauffe eau est en mode " + 
					this.adjustableOutboundPortForWaterHeater.currentMode() + "\n\n");
		}
	}
}
