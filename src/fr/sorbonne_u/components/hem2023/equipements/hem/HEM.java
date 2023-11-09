package fr.sorbonne_u.components.hem2023.equipements.hem;

import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.classCreator.ClassCreator;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.DishWasher;
import fr.sorbonne_u.components.hem2023.equipements.hem.registration.RegistrationI;
import fr.sorbonne_u.components.hem2023.equipements.hem.registration.RegistrationInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2023.equipements.meter.connectors.ElectricMeterConnector;
import fr.sorbonne_u.components.hem2023.equipements.meter.ports.ElectricMeterOutboundPort;

public class HEM extends AbstractComponent 
	implements RegistrationI {
	public static final boolean VERBOSE = true;
	
	public static final String URI_ELECTRIC_METER_PORT = "URI_ELECTRIC_METER_PORT";
	public static final String URI_DISH_WASHER_PORT = "URI_DISH_WASHER_PORT";
	public static final String URI_WATER_HEATER_PORT = "URI_WATER_HEATER_PORT";
	public static final String URI_REGISTRATION_INBOUND_PORT = "URI_REGISTRATION_INBOUND_PORT";

	protected ElectricMeterOutboundPort electricMeterOutboundPort; 
	protected AdjustableOutboundPort adjustableOutboundPortForDishWasher;
	protected AdjustableOutboundPort adjustableOutboundPortForWaterHeater;
	protected RegistrationInboundPort registrationInboundPort;
	
	protected HashMap<String, String> registeredUriModularEquipement;

	/**
	 * 
	 * 				CONSTRUCTORS
	 */
	public HEM() throws Exception {
		super(2, 1);
		initialiseHEM();	
	}
	
	public HEM(String uriId) throws Exception {
		super(uriId, 1, 1);
		initialiseHEM();
	}
	
	private void initialiseHEM() throws Exception {
		registeredUriModularEquipement = new HashMap<String, String>();
		electricMeterOutboundPort = new ElectricMeterOutboundPort(URI_ELECTRIC_METER_PORT, this);
		adjustableOutboundPortForDishWasher = new AdjustableOutboundPort(URI_DISH_WASHER_PORT, this);
		adjustableOutboundPortForWaterHeater = new AdjustableOutboundPort(URI_WATER_HEATER_PORT, this);
		registrationInboundPort = new RegistrationInboundPort(URI_REGISTRATION_INBOUND_PORT, this);
		
		electricMeterOutboundPort.publishPort();
		adjustableOutboundPortForDishWasher.publishPort();
		adjustableOutboundPortForWaterHeater.publishPort();
		registrationInboundPort.publishPort();
		
		if(VERBOSE) {
			this.tracer.get().setTitle("Home Energy Manager component");
			this.tracer.get().setRelativePosition(1, 1);
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
				this.traceMessage("connexion des ports du gestionnaire\n\n");
			
			this.doPortConnection(electricMeterOutboundPort.getPortURI(), 
					ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI, 
					ElectricMeterConnector.class.getCanonicalName());
			
			this.doPortConnection(adjustableOutboundPortForDishWasher.getPortURI(), 
					DishWasher.URI_INTERNAL_CONTROL_INBOUND_PORT, 
					DishWasherConnector.class.getCanonicalName());
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
			this.traceMessage("déconnexion des liaisons entre les ports\n\n");
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
			registrationInboundPort.unpublishPort();
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
			this.traceMessage("la consommation total est de " + currentConsumption + " watts\n");
			this.traceMessage("la production total est de " + currentProduction + " watts\n\n");
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
			this.traceMessage("test de la connexion entre le chauffe eau et le gestionnaire\n\n");
			
			this.traceMessage("test de maxMode, résultat attendu -> 2\n\n");
			if(this.adjustableOutboundPortForWaterHeater.maxMode() != 2)
				assertTrue(false);
			
			this.traceMessage("test de currentMode, résultat attendu -> 0\n\n");
			if(this.adjustableOutboundPortForWaterHeater.currentMode() != 0)
				assertTrue(false);
			
			this.traceMessage("test de upMode, résultat attendu -> true et le chauffe eau est en mode 1\n\n");
			if(this.adjustableOutboundPortForWaterHeater.upMode() != true)
				assertTrue(false);
			if(this.adjustableOutboundPortForWaterHeater.currentMode() != 1)
				assertTrue(false);
			this.adjustableOutboundPortForWaterHeater.setMode(2);
			if(this.adjustableOutboundPortForWaterHeater.upMode() != false)
				assertTrue(false);
			
			this.traceMessage("test de downMode, résultat attendu -> true et le chauffe eau est en mode 0\n\n");
			if(this.adjustableOutboundPortForWaterHeater.downMode() != true)
				assertTrue(false);
			if(this.adjustableOutboundPortForWaterHeater.currentMode() != 1)
				assertTrue(false);
			this.adjustableOutboundPortForWaterHeater.setMode(0);
			if(this.adjustableOutboundPortForWaterHeater.downMode() != false)
				assertTrue(false);
			
			this.traceMessage("test de setMode, résultat attendu -> true et le chauffe eau est en mode 2\n\n");
			if(this.adjustableOutboundPortForWaterHeater.setMode(2) != true)
				assertTrue(false);
		}
	}
	
	/**
	 * 			REGISTRATION
	 */

	@Override
	public boolean registered(String uid) throws Exception {
		if(VERBOSE)
			this.traceMessage("Verification de l'inscription de " + uid + "\n\n");
		if(this.registeredUriModularEquipement.containsKey(uid))
			return true;
		return false;
	}

	@Override
	public boolean register(String uid, String controlPortURI, String path2xmlControlAdapter) throws Exception {
		if(VERBOSE)
			this.traceMessage("Inscription de " + uid + "\n\n");
		
		if(registered(uid))
			return false;
	
		this.registeredUriModularEquipement.put(uid, controlPortURI);
		ClassCreator classCreator = new ClassCreator(path2xmlControlAdapter);
		Class<?> classConnector = classCreator.createClass();
		
		this.doPortConnection(adjustableOutboundPortForWaterHeater.getPortURI(), 
				controlPortURI, 
				classConnector.getName());
		
		return true;
	}

	@Override
	public void unregister(String uid) throws Exception {		
		if(VERBOSE)
			this.traceMessage("Désinscription de " + uid + "\n\n");
		if(registered(uid))
			this.registeredUriModularEquipement.remove(uid);
	}
}
