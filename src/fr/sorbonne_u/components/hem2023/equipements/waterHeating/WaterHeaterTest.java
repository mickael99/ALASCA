package fr.sorbonne_u.components.hem2023.equipements.waterHeating;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.connectors.WaterHeaterExternalControlConnector;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.connectors.WaterHeaterUserControlConnector;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterExternalControlCI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterUserAndExternalControlI.WaterHeaterPowerLevel;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterUserControlCI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.ports.WaterHeaterExternalControlOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.ports.WaterHeaterUserControlOutboundPort;
import fr.sorbonne_u.components.hem2023.timer.Timer;

@RequiredInterfaces(required={WaterHeaterUserControlCI.class, WaterHeaterExternalControlCI.class})
public class WaterHeaterTest extends AbstractComponent {
	
	public static final String URI_EXTERNAL_CONTROL_OUTBOUND_PORT = "URI_EXTERNAL_CONTROL_OUTBOUND_PORT";
	public static final String URI_USER_CONTROL_OUTBOUND_PORT = "URI_USER_CONTROL_OUTBOUND_PORT";
	protected WaterHeaterExternalControlOutboundPort waterHeaterExternalControlOutboundPort;
	protected WaterHeaterUserControlOutboundPort waterHeaterUserControlOutboundPort;
	
	protected WaterHeaterTest() throws Exception {
		super(1, 1);
		initialisePort();
	}
	
	protected WaterHeaterTest(String UriID) throws Exception {
		super(UriID, 1, 0);
		initialisePort();
	}
	
	private void initialisePort() throws Exception {		
		waterHeaterExternalControlOutboundPort = 
				new WaterHeaterExternalControlOutboundPort(URI_EXTERNAL_CONTROL_OUTBOUND_PORT, this);
		waterHeaterExternalControlOutboundPort.publishPort();
		
		waterHeaterUserControlOutboundPort =
				new WaterHeaterUserControlOutboundPort(URI_USER_CONTROL_OUTBOUND_PORT, this);
		waterHeaterUserControlOutboundPort.publishPort();
		
		this.tracer.get().setTitle("Testeur du chauffe eau");
		this.tracer.get().setRelativePosition(1, 1);
		this.toggleTracing();
	}
	
	/**
	 * 			LIFE CYCLE
	 */
	public synchronized void start() throws ComponentStartException {
		super.start();
			
		try {
			this.doPortConnection(waterHeaterExternalControlOutboundPort.getPortURI(), 
					WaterHeater.URI_EXTERNAL_CONTROL_INBOUND_PORT, 
					WaterHeaterExternalControlConnector.class.getCanonicalName());
			
			this.doPortConnection(waterHeaterUserControlOutboundPort.getPortURI(), 
					WaterHeater.URI_USER_CONTROL_INBOUND_PORT, 
					WaterHeaterUserControlConnector.class.getCanonicalName());
		}catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}
	public synchronized void execute() throws Exception {
		super.execute();
		runAllTests();
	}
	
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(waterHeaterExternalControlOutboundPort.getPortURI());
		this.doPortDisconnection(waterHeaterUserControlOutboundPort.getPortURI());
		super.finalise();
	}
	
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			waterHeaterExternalControlOutboundPort.unpublishPort();
			waterHeaterUserControlOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	/**
	 * 			Methodes de test
	 */
	@Test
	public void runAllTests() throws Exception {
		testTurnOnTurnOff();
		testTargetTemperature();
		testScheduleHeating();
		testCurrentTemperature();
		testPowerLevel();
		testEnergyConsumption();
		testStartHeating();
		testStopHeating();
		this.traceMessage("tous les tests du chauffe eau ont réussit !");
	}
	
	@Test
	protected void testTurnOnTurnOff() throws Exception {
		this.traceMessage("début testTurnOnTurnOff\n");
		waterHeaterUserControlOutboundPort.turnOn();
		
		if(!waterHeaterUserControlOutboundPort.isOn()) {
			this.traceMessage
				("Le test testTurnOnTurnOff a échoué car le chauffe eau ne s'est pas allumé\n");
			assertTrue(false);
		}
		
		waterHeaterUserControlOutboundPort.turnOff();
		if(waterHeaterUserControlOutboundPort.isOn()) {
			this.traceMessage
				("Le test testTurnOnTurnOff a échoué car le chauffe eau ne s'est pas éteint\n");
			assertTrue(false);
		}
		this.traceMessage("testTurnOnTurnOff réussit\n\n");
	}
	
	@Test
	protected void testTargetTemperature() throws Exception {
		this.traceMessage("début testTargetTemperature\n");
		waterHeaterUserControlOutboundPort.setTargetWaterTemperature(55);
		if(waterHeaterUserControlOutboundPort.getTargetTemperature() != 55) {
			this.traceMessage
				("Le test testTargetTemperature a échoué\n");
			assertTrue(false);
		}
		
		this.traceMessage("testTargetTemperature réussit\n\n");
	}
	
	@Test
	protected void testScheduleHeating() throws Exception {
		this.traceMessage("début testScheduleHeating\n");
		Timer launchTime = new Timer(23, 0, 0);
		Timer endTime = new Timer(4, 30, 5);
		Timer expectedAnswer = new Timer(5, 30, 5);
		waterHeaterUserControlOutboundPort.scheduleHeating(launchTime, endTime);
		if(!waterHeaterUserControlOutboundPort.getTimer().equals(expectedAnswer)) {
			this.traceMessage
				("Le test testScheduleHeating a échoué\n");
			assertTrue(false);
		}
		
		this.traceMessage("testScheduleHeating réussit\n\n");
	}
	
	//a modifier lorsqu'on aura automatisé le changement de température de l'eau
	@Test
	protected void testCurrentTemperature() throws Exception {
		this.traceMessage("début testCurrentTemperature\n");
		if(waterHeaterExternalControlOutboundPort.getCurrentTemperature() != 
				WaterHeater.INITIALISE_TEMPERATURE) {
			this.traceMessage
				("Le test testCurrentTemperature a échoué\n");
			assertTrue(false);
		}
		
		this.traceMessage("testCurrentTemperature réussit\n\n");
	}
	
	@Test
	protected void testPowerLevel() throws Exception {
		this.traceMessage("début testPowerLevel\n");
		
		waterHeaterExternalControlOutboundPort.setPowerLevel(WaterHeaterPowerLevel.NORMAL);
		if(waterHeaterExternalControlOutboundPort.getPowerLevel() != 4000) {
			this.traceMessage
				("Le test testPowerLevel a échoué\n");
			assertTrue(false);
		}
		
		this.traceMessage("testPowerLevel réussit\n\n");
	}
	
	//a modifier lorsqu'on aura automatisé la consommation d'energie
	@Test
	protected void testEnergyConsumption() throws Exception {
		this.traceMessage("début testEnergyConsumption\n");
		
		if(waterHeaterExternalControlOutboundPort.getEnergyConsumption() != 0) {
			this.traceMessage
				("Le test testEnergyConsumption a échoué\n");
			assertTrue(false);
		}
		
		this.traceMessage("testEnergyConsumption réussit\n\n");
	}
	
	@Test
	protected void testStartHeating() throws Exception {
		this.traceMessage("début testStartHeating\n");
		waterHeaterUserControlOutboundPort.turnOn();
		waterHeaterUserControlOutboundPort.removeTimer();
		
		waterHeaterExternalControlOutboundPort.startHeating();
		
		if(!waterHeaterExternalControlOutboundPort.isHeating()) {
			this.traceMessage
				("Le test testStartHeating a échoué\n");
			assertTrue(false);
		}
		
		this.traceMessage("testStartHeating réussit\n\n");
	}
	
	@Test
	protected void testStopHeating() throws Exception {
		this.traceMessage("début testStopHeating\n");
		
		waterHeaterExternalControlOutboundPort.stopHeating();
		
		if(waterHeaterExternalControlOutboundPort.isHeating()) {
			this.traceMessage
				("Le test testStopHeating a échoué\n");
			assertTrue(false);
		}
		
		this.traceMessage("testStopHeating réussit\n\n");
	}
}
