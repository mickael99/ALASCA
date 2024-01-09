package fr.sorbonne_u.components.hem2023.equipements.hem;

import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.classCreator.ClassCreator;
import fr.sorbonne_u.components.hem2023.equipements.battery.Battery;
import fr.sorbonne_u.components.hem2023.equipements.battery.interfaces.BatteryManagementI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.DishWasher;
import fr.sorbonne_u.components.hem2023.equipements.hem.connectors.BatteryManagementConnector;
import fr.sorbonne_u.components.hem2023.equipements.hem.connectors.ElectricMeterConsumptionConnector;
import fr.sorbonne_u.components.hem2023.equipements.hem.ports.BatteryManagementOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.hem.ports.ElectricMeterConsumptionOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.hem.ports.ElectricMeterOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.hem.registration.RegistrationI;
import fr.sorbonne_u.components.hem2023.equipements.hem.registration.RegistrationInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2023.equipements.meter.connectors.ElectricMeterConnector;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.WaterHeater;

public class HEM extends AbstractComponent 
	implements RegistrationI, BatteryManagementI {
	public static final boolean VERBOSE = true;
	
	public static final String URI_ELECTRIC_METER_PORT = "URI_ELECTRIC_METER_PORT";
	public static final String URI_DISH_WASHER_PORT = "URI_DISH_WASHER_PORT";
	public static final String URI_WATER_HEATER_PORT = "URI_WATER_HEATER_PORT";
	public static final String URI_REGISTRATION_INBOUND_PORT = "URI_REGISTRATION_INBOUND_PORT";

	protected ElectricMeterOutboundPort electricMeterOutboundPort;
	protected RegistrationInboundPort registrationInboundPort;
	protected BatteryManagementOutboundPort batteryManagementOutboundPort;
	protected ElectricMeterConsumptionOutboundPort electricMeterConsumptionOutboundPort;
	
	protected HashMap<String, AdjustableOutboundPort> registeredUriModularEquipement;

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
		registeredUriModularEquipement = new HashMap<String, AdjustableOutboundPort>();
		electricMeterOutboundPort = new ElectricMeterOutboundPort(URI_ELECTRIC_METER_PORT, this);
		registrationInboundPort = new RegistrationInboundPort(URI_REGISTRATION_INBOUND_PORT, this);
		batteryManagementOutboundPort = new BatteryManagementOutboundPort(this);
		electricMeterConsumptionOutboundPort = new ElectricMeterConsumptionOutboundPort(this);
		
		electricMeterOutboundPort.publishPort();
		registrationInboundPort.publishPort();
		batteryManagementOutboundPort.publishPort();
		electricMeterConsumptionOutboundPort.publishPort();
		
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
			this.doPortConnection(batteryManagementOutboundPort.getPortURI(), 
					Battery.URI_MANAGEMENT, 
					BatteryManagementConnector.class.getCanonicalName());
			this.doPortConnection(electricMeterConsumptionOutboundPort.getPortURI(), 
					ElectricMeter.CONSOMATION_URI, 
					ElectricMeterConsumptionConnector.class.getCanonicalName());
		}catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}
	
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		//runTest();
		this.setConsomationMode();
		
		if(this.sendBatteryToAModularEquipment(WaterHeater.Uri, 1000.0) && 			
				this.sendBatteryToAModularEquipment(DishWasher.Uri, 1000.0)) {
			this.addElectricConsumption(2000.0);
			if(VERBOSE)
				this.traceMessage("succesfull !!!");
		}
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		if(VERBOSE)
			this.traceMessage("déconnexion des liaisons entre les ports\n\n");
		this.doPortDisconnection(electricMeterOutboundPort.getPortURI());
		this.doPortDisconnection(batteryManagementOutboundPort.getPortURI());
		this.doPortDisconnection(electricMeterConsumptionOutboundPort.getPortURI());
		
		for(AdjustableOutboundPort ao : registeredUriModularEquipement.values())
			this.doPortDisconnection(ao.getPortURI());

		super.finalise();
	}
	
	@Override 
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			if(VERBOSE)
				this.traceMessage("supréssion des ports du gestionnaire d'energie\n\n");
			
			this.electricMeterOutboundPort.unpublishPort();
			registrationInboundPort.unpublishPort();
			this.batteryManagementOutboundPort.unpublishPort();
			electricMeterConsumptionOutboundPort.unpublishPort();
			for(AdjustableOutboundPort ao : registeredUriModularEquipement.values())
				ao.unpublishPort();
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
			AdjustableOutboundPort adjustableOutboundPortForDishWasher = registeredUriModularEquipement.get(DishWasher.Uri);
			
			this.traceMessage("test de la connexion entre le lave vaisselle et le gestionnaire\n");
				
			this.traceMessage("test de maxMode, résultat attendu -> 3\n\n");
			if(adjustableOutboundPortForDishWasher.maxMode() != 3)
				assertTrue(false);
			
			this.traceMessage("test de currentMode, résultat attendu -> 0\n\n");
			if(adjustableOutboundPortForDishWasher.currentMode() != 0)
				assertTrue(false);
			
			this.traceMessage("test de upMode, résultat attendu -> true et le lave vaisselle est en mode 1\n\n");
			if(adjustableOutboundPortForDishWasher.upMode() != true)
				assertTrue(false);
			if(adjustableOutboundPortForDishWasher.currentMode() != 1)
				assertTrue(false);
			adjustableOutboundPortForDishWasher.setMode(3);
			if(adjustableOutboundPortForDishWasher.upMode() != false)
				assertTrue(false);
			
			this.traceMessage("test de downMode, résultat attendu -> true et le lave vaisselle est en mode 0\n\n");
			if(adjustableOutboundPortForDishWasher.downMode() != true)
				assertTrue(false);
			if(adjustableOutboundPortForDishWasher.currentMode() != 2)
				assertTrue(false);
			adjustableOutboundPortForDishWasher.setMode(0);
			if(adjustableOutboundPortForDishWasher.downMode() != false)
				assertTrue(false);
			
			this.traceMessage("test de setMode, résultat attendu -> true et le lave vaisselle est en mode 2\n\n");
			if(adjustableOutboundPortForDishWasher.setMode(2) != true)
				assertTrue(false);	
		}
	}
	
	public void testWaterHeater() throws Exception {
		if(VERBOSE) {
			AdjustableOutboundPort adjustableOutboundPortForWaterHeater = registeredUriModularEquipement.get(WaterHeater.Uri);
			
			this.traceMessage("test de la connexion entre le chauffe eau et le gestionnaire\n\n");
			
			this.traceMessage("test de maxMode, résultat attendu -> 2\n\n");
			if(adjustableOutboundPortForWaterHeater.maxMode() != 2)
				assertTrue(false);
			
			this.traceMessage("test de currentMode, résultat attendu -> 0\n\n");
			if(adjustableOutboundPortForWaterHeater.currentMode() != 0)
				assertTrue(false);
			
			this.traceMessage("test de upMode, résultat attendu -> true et le chauffe eau est en mode 1\n\n");
			if(adjustableOutboundPortForWaterHeater.upMode() != true)
				assertTrue(false);
			if(adjustableOutboundPortForWaterHeater.currentMode() != 1)
				assertTrue(false);
			adjustableOutboundPortForWaterHeater.setMode(2);
			if(adjustableOutboundPortForWaterHeater.upMode() != false)
				assertTrue(false);
			
			this.traceMessage("test de downMode, résultat attendu -> true et le chauffe eau est en mode 0\n\n");
			if(adjustableOutboundPortForWaterHeater.downMode() != true)
				assertTrue(false);
			if(adjustableOutboundPortForWaterHeater.currentMode() != 1)
				assertTrue(false);
			adjustableOutboundPortForWaterHeater.setMode(0);
			if(adjustableOutboundPortForWaterHeater.downMode() != false)
				assertTrue(false);
			
			this.traceMessage("test de setMode, résultat attendu -> true et le chauffe eau est en mode 2\n\n");
			if(adjustableOutboundPortForWaterHeater.setMode(2) != true)
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
	
		AdjustableOutboundPort ao = new AdjustableOutboundPort(this);
		ao.publishPort();
		this.registeredUriModularEquipement.put(uid, ao);
		ClassCreator classCreator = new ClassCreator(path2xmlControlAdapter);
		Class<?> classConnector = classCreator.createClass();
		this.doPortConnection(ao.getPortURI(), 
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

	@Override
	public void setProductionMode() throws Exception {
		this.batteryManagementOutboundPort.setProductionMode();
	}

	@Override
	public void setConsomationMode() throws Exception {
		this.batteryManagementOutboundPort.setConsomationMode();		
	}

	@Override
	public boolean sendBatteryToAModularEquipment(String uri, double quantity) throws Exception {
		if(VERBOSE) 
			this.traceMessage("ask sending " + quantity + " watts to an equipment " + uri + " \n\n");
		
		return this.batteryManagementOutboundPort.sendBatteryToAModularEquipment(uri, quantity);
	}
	
	public void addElectricConsumption(double quantity) throws Exception {
		if(VERBOSE) 
			this.traceMessage("add " + quantity + " watts consumption\n\n");
		
		this.electricMeterConsumptionOutboundPort.addElectricConsumption(quantity);
	}
}
