package fr.sorbonne_u.components.hem2023.equipements.meter;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.equipements.meter.connectors.ElectricMeterConnector;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterCI;
import fr.sorbonne_u.components.hem2023.equipements.meter.ports.ElectricMeterOutboundPort;

import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredInterfaces(required={ElectricMeterCI.class})
public class ElectricMeterUnitTester extends AbstractComponent {

	protected ElectricMeterOutboundPort electricMeterOutboundPort;

	/**
	 * 				CONSTRUCTEURS
	 */
	
	protected ElectricMeterUnitTester() throws Exception {
		super(1, 0);

		this.electricMeterOutboundPort = new ElectricMeterOutboundPort(this);
		this.electricMeterOutboundPort.publishPort();

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
					this.electricMeterOutboundPort.getPortURI(),
					ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI,
					ElectricMeterConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	@Override
	public synchronized void execute() throws Exception {
		this.runAllTests();
	}

	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.electricMeterOutboundPort.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.electricMeterOutboundPort.unpublishPort();
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
		
		if(this.electricMeterOutboundPort.getCurrentConsumption() != 0) {
			this.traceMessage("testGetCurrentConsumption a échoué\n");
			assertTrue(false);
		}
			
		this.traceMessage("testGetCurrentConsumption réussit\n\n");
	}

	protected void testGetCurrentProduction() throws Exception {
		this.traceMessage("début testGetCurrentProduction\n");
		if(this.electricMeterOutboundPort.getCurrentProduction() != 0) {
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
