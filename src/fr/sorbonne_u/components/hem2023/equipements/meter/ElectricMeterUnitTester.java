package fr.sorbonne_u.components.hem2023.equipements.meter;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.equipements.hem.ports.ElectricMeterOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.meter.connectors.ElectricMeterConnector;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterCI;
import fr.sorbonne_u.components.hem2023.CVMGlobalTest;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredInterfaces(required={ElectricMeterCI.class})
public class ElectricMeterUnitTester extends AbstractComponent {

	protected ElectricMeterOutboundPort emop;
	
	protected ClocksServerOutboundPort	clocksServerOutboundPort;


	/**
	 * 				CONSTRUCTEURS
	 */
	
	protected ElectricMeterUnitTester() throws Exception {
		super(1, 0);

		this.emop = new ElectricMeterOutboundPort(this);
		this.emop.publishPort();

		this.tracer.get().setTitle("Electric meter tester component");
		this.tracer.get().setRelativePosition(0, 0);
		this.toggleTracing();		
	}
	
	/**
	 * 			CYCLE DE VIE
	 */
	
	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();
		try {
			this.doPortConnection(
					this.emop.getPortURI(),
					ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI,
					ElectricMeterConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	@Override
	public synchronized void execute() throws Exception {
		this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
		this.clocksServerOutboundPort.publishPort();
		this.doPortConnection(
				this.clocksServerOutboundPort.getPortURI(),
				ClocksServer.STANDARD_INBOUNDPORT_URI,
				ClocksServerConnector.class.getCanonicalName());
		this.logMessage("ElectricMeterUnitTester gets the clock.");
		AcceleratedClock ac =
			this.clocksServerOutboundPort.getClock(CVMGlobalTest.CLOCK_URI);

		this.logMessage("ElectricMeterUnitTester waits until start time.");
		ac.waitUntilStart();
		this.logMessage("ElectricMeterUnitTester starts.");
		this.doPortDisconnection(
					this.clocksServerOutboundPort.getPortURI());
		this.clocksServerOutboundPort.unpublishPort();
		this.logMessage("ElectricMeterUnitTester begins to perform tests.");
		this.runAllTests();
		this.logMessage("ElectricMeterUnitTester tests end.");
	}

	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.emop.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.emop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
	
	/**
	 * 			METHODES
	 */

	protected void testGetCurrentConsumption() throws Exception {
		this.traceMessage("début testGetCurrentConsumption\n");
		
		if(this.emop.getCurrentConsumption() != 0) {
			this.traceMessage("testGetCurrentConsumption a échoué\n");
			assertTrue(false);
		}
			
		this.traceMessage("testGetCurrentConsumption réussit\n\n");
	}

	protected void testGetCurrentProduction() throws Exception {
		this.traceMessage("début testGetCurrentProduction\n");
		if(this.emop.getCurrentProduction() != 0) {
			this.traceMessage("testGetCurrentProduction a échoué\n");
			assertTrue(false);
		}

		this.traceMessage("testGetCurrentProduction réussit\n\n");
	}

	protected void runAllTests() throws Exception {
		this.testGetCurrentConsumption();
		this.testGetCurrentProduction();
		this.traceMessage("tous les tests réussit !\n");
	}
}
