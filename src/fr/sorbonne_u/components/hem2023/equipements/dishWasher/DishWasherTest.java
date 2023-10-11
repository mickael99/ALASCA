package fr.sorbonne_u.components.hem2023.equipements.dishWasher;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors.DishWasherInternalControlConnector;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors.DishWasherUserControlConnector;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.Ports.DishWasherInternalControlOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.Ports.DishWasherUserControlOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherInternalAndControlI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherInternalControlCI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherUserControlCI;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Test;


@RequiredInterfaces(required={DishWasherUserControlCI.class, DishWasherInternalControlCI.class, ClocksServerCI.class})
public class DishWasherTest extends AbstractComponent {
	public static final String URI_INTERNAL_CONTROL_OUTBOUND_PORT
		= "URI_INTERNAL_CONTROL_OUTBOUND_PORT";
	
	public static final String URI_USER_CONTROL_OUTBOUND_PORT
		= "URI_USER_CONTROL_OUTBOUND_PORT";
	
	protected DishWasherInternalControlOutboundPort dishWasherInternalControlOutboundPort;
	protected DishWasherUserControlOutboundPort dishWasherUserControlOutboundPort;
	protected ClocksServerOutboundPort clocksServerOutboundPort;

	
	protected DishWasherTest() throws Exception {
		super(1, 1);
		initialisePort();
	}
	
	protected DishWasherTest(String UriID) throws Exception {
		super(UriID, 1, 0);
		initialisePort();
	}
	
	private void initialisePort() throws Exception {		
		dishWasherInternalControlOutboundPort = 
				new DishWasherInternalControlOutboundPort(URI_INTERNAL_CONTROL_OUTBOUND_PORT, this);
		dishWasherInternalControlOutboundPort.publishPort();
		
		dishWasherUserControlOutboundPort =
				new DishWasherUserControlOutboundPort(URI_USER_CONTROL_OUTBOUND_PORT, this);
		dishWasherUserControlOutboundPort.publishPort();
		
		this.tracer.get().setTitle("Testeur du lave vaisselle");
		this.tracer.get().setRelativePosition(1, 1);
		this.toggleTracing();
	}
	
	/**
	 * 			LIFE CYCLE
	 */
	public synchronized void start() throws ComponentStartException {
		super.start();
			
		try {
			this.doPortConnection(dishWasherInternalControlOutboundPort.getPortURI(), 
					DishWasher.URI_INTERNAL_CONTROL_INBOUND_PORT, 
					DishWasherInternalControlConnector.class.getCanonicalName());
			
			this.doPortConnection(dishWasherUserControlOutboundPort.getPortURI(), 
					DishWasher.URI_USER_CONTROL_INBOUND_PORT, 
					DishWasherUserControlConnector.class.getCanonicalName());
		}catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}
	public synchronized void execute() throws Exception {
		super.execute();
		runAllTests();
	}
	
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(dishWasherInternalControlOutboundPort.getPortURI());
		this.doPortDisconnection(dishWasherUserControlOutboundPort.getPortURI());
		super.finalise();
	}
	
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			dishWasherInternalControlOutboundPort.unpublishPort();
			dishWasherUserControlOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	/**
	 * 			OTHER METHODS
	 */
	
	@Test
	protected void runAllTests() throws Exception {
		testTurnOnTurnOff();
		testOpenCloseDoor();
		testSetWashingMode();
		testDryingMode();
		waterTests();
		testScheduleWashing();
		TestGetEnergyConsumption();
		testStartWashing();
		testStopWashing();
		this.traceMessage("tous les tests du lave vaisselle ont réussit !");
	}
	
	@Test
	protected void waterTests() throws Exception {
		testFillWater();
		testGetWaterQuantity();
		testIsCuveWaterIsEmpty();
	}
	
	@Test
	protected void testTurnOnTurnOff() throws Exception {
		this.traceMessage("début testTurnOnTurnOff\n");
		dishWasherUserControlOutboundPort.turnOn();
		
		if(!dishWasherUserControlOutboundPort.isOn()) {
			this.traceMessage
				("Le test testTurnOnTurnOff a échoué car le lave vaisselle ne s'est pas allumé\n");
			assertTrue(false);
		}
		
		dishWasherUserControlOutboundPort.turnOff();
		if(dishWasherUserControlOutboundPort.isOn()) {
			this.traceMessage
				("Le test testTurnOnTurnOff a échoué car le lave vaisselle ne s'est pas éteint\n");
			assertTrue(false);
		}
		this.traceMessage("testTurnOnTurnOff réussit\n\n");
	}
	
	@Test
	protected void testOpenCloseDoor() throws Exception {
		this.traceMessage("début testOpenCloseDoor\n");
		
		dishWasherUserControlOutboundPort.openDoor();
		if(!this.dishWasherInternalControlOutboundPort.isDoorOpen()) {
			this.traceMessage("testOpenCloseDoor a échoué car la porte n'a pas pu s'ouvrir\n");
			assertTrue(false);
		}
		
		dishWasherUserControlOutboundPort.closeDoor();
		if(this.dishWasherInternalControlOutboundPort.isDoorOpen()) {
			this.traceMessage("testOpenCloseDoor a échoué car la porte n'a pas pu se fermer\n");
			assertTrue(false);
		}
		
		this.traceMessage("testOpenCloseDoor a réussit\n\n");
	}
	
	@Test
	protected void testSetWashingMode() throws Exception {
		this.traceMessage("début testSetWashingMode\n");
		
		dishWasherUserControlOutboundPort.setWashingMode
			(DishWasherInternalAndControlI.WashingMode.NORMAL);
		if(!this.dishWasherInternalControlOutboundPort.getWashingMode().equals
				(DishWasherInternalAndControlI.WashingMode.NORMAL)) {
			this.traceMessage("testSetWashingMode a échoué\n");
			assertTrue(false);
		}
		
		this.traceMessage("testSetWashingMode a réussit\n\n");
	}
	
	@Test
	protected void testDryingMode() throws Exception {
		this.traceMessage("début testDryingMode\n");
		
		dishWasherUserControlOutboundPort.enableDryingMode();
		if(!this.dishWasherInternalControlOutboundPort.isDryingModeEnable()) {
			this.traceMessage("testDryingMode a échoué\n");
			assertTrue(false);
		}
		
		dishWasherUserControlOutboundPort.disableDryingMode();
		if(this.dishWasherInternalControlOutboundPort.isDryingModeEnable()) {
			this.traceMessage("testDryingMode a échoué\n");
			assertTrue(false);
		}
		
		this.traceMessage("testDryingMode réussit\n\n");
	}
	
	@Test
	protected void testFillWater() throws Exception {
		this.traceMessage("début testFillWater\n");
		
		if(dishWasherUserControlOutboundPort.fillWater(-10.0) == true) {
			this.traceMessage
				("testFillWater a échoué car le remplissage du reservoir aurait du échoué\n");
			assertTrue(false);
		}
		if(dishWasherUserControlOutboundPort.fillWater(20) == true) {
			this.traceMessage
			("testFillWater a échoué car le remplissage du reservoir aurait du échoué\n");
			assertTrue(false);
		}
		
		dishWasherInternalControlOutboundPort.removeWaterQuantity(10.0);
		if(dishWasherUserControlOutboundPort.fillWater(10.0) == false) {
			this.traceMessage
				("testFillWater a échoué car le remplissage du reservoir aurait du réussir\n");
			assertTrue(false);
		}
		
		this.traceMessage("testFillWater réussit\n\n");
	}
	
	@Test
	protected void testGetWaterQuantity() throws Exception {
		this.traceMessage("début testGetWaterQuantity\n");
		
		dishWasherUserControlOutboundPort.fillWaterCompletely();
		if(dishWasherInternalControlOutboundPort.getWaterQuantity() != 
				DishWasher.MAX_WATER_QUANTITY_IN_LITER) {
			this.traceMessage("testGetWaterQuantity a échoué\n");
			assertTrue(false);
		}
		
		dishWasherInternalControlOutboundPort.removeWaterQuantity(10.0);
		if(dishWasherInternalControlOutboundPort.getWaterQuantity() != 
				DishWasher.MAX_WATER_QUANTITY_IN_LITER - 10.0) {
			this.traceMessage("testGetWaterQuantity a échoué\n");
			assertTrue(false);
		}
			
		this.traceMessage("testGetWaterQuantity réussit\n\n");
	}
	
	@Test 
	protected void testIsCuveWaterIsEmpty() throws Exception {
		this.traceMessage("début testIsCuveWaterIsEmpty\n");
		
		dishWasherUserControlOutboundPort.fillWaterCompletely();
		if(dishWasherInternalControlOutboundPort.isCuveWaterIsEmpty() == 
				true) {
			this.traceMessage("testIsCuveWaterIsEmpty a échoué\n");
			assertTrue(false);
		}
		
		dishWasherInternalControlOutboundPort.removeWaterQuantity(DishWasher.MAX_WATER_QUANTITY_IN_LITER);
		if(dishWasherInternalControlOutboundPort.isCuveWaterIsEmpty() == 
				false) {
			this.traceMessage("testIsCuveWaterIsEmpty a échoué\n");
			assertTrue(false);
		}
			
		this.traceMessage("testIsCuveWaterIsEmpty réussit\n\n");
	}
	
	@Test 
	protected void testScheduleWashing() throws Exception {
		this.traceMessage("début testScheduleWashing\n");
		
		Timer timer = new Timer(0, 30, 30);
		dishWasherUserControlOutboundPort.scheduleWashing(timer);
		
		if(!dishWasherInternalControlOutboundPort.getTimer().equals(new Timer(0, 30, 30))) {
			this.traceMessage("testScheduleWashing a échoué\n");
			assertTrue(false);
		}
			
		this.traceMessage("testScheduleWashing réussit\n\n");
	}
	
	@Test
	protected void TestGetEnergyConsumption() throws Exception {
		this.traceMessage("début TestGetEnergyConsumption\n");
		
		dishWasherUserControlOutboundPort.turnOn();
		dishWasherUserControlOutboundPort.disableDryingMode();
		dishWasherUserControlOutboundPort.setWashingMode
			(DishWasherInternalAndControlI.WashingMode.NORMAL);
		
		if(dishWasherInternalControlOutboundPort.getEnergyConsumption() != 1500) {
			this.traceMessage("TestGetEnergyConsumption a échoué\n");
			assertTrue(false);
		}
		dishWasherUserControlOutboundPort.enableDryingMode();
		if(dishWasherInternalControlOutboundPort.getEnergyConsumption() != 2500) {
			this.traceMessage("TestGetEnergyConsumption a échoué\n");
			assertTrue(false);
		}
			
		this.traceMessage("TestGetEnergyConsumption réussit\n\n");
	}
	
	@Test
	protected void testStartWashing() throws Exception {
		this.traceMessage("début testStartWashing\n");
		
		dishWasherUserControlOutboundPort.turnOn();
		dishWasherUserControlOutboundPort.setWashingMode
			(DishWasherInternalAndControlI.WashingMode.NORMAL);
		dishWasherUserControlOutboundPort.enableDryingMode();
		
		dishWasherUserControlOutboundPort.removeTimer();
		
		dishWasherUserControlOutboundPort.startWashing();
		
		if(!dishWasherInternalControlOutboundPort.isWashing()) {
			this.traceMessage("testStartWashing a échoué\n");
			assertTrue(false);
		}
			
		this.traceMessage("testStartWashing réussit\n\n");
	}
	
	@Test
	protected void testStopWashing() throws Exception {
		this.traceMessage("début testStopWashing\n");
		
		dishWasherUserControlOutboundPort.stopWashing();
		
		if(dishWasherInternalControlOutboundPort.isWashing()) {
			this.traceMessage("testStopWashing a échoué\n");
			assertTrue(false);
		}
			
		this.traceMessage("testStopWashing réussit\n\n");
	}
}
