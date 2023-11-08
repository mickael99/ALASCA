/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.gasGenerator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2023.equipements.gasGenerator.GasGeneratorImplementationI.GasGeneratorState;
import fr.sorbonne_u.components.hem2023.equipements.gasGenerator.GasGeneratorImplementationI.GasGeneretorMode;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

/**
 * @author Yukhoi
 *
 */
@RequiredInterfaces(required = {GasGeneratorUserCI.class, ClocksServerCI.class})
public class GasGeneratorTester extends AbstractComponent {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected final boolean				isUnitTest;
	protected GasGeneratorOutboundPort		ggop;
	protected String					gasGeneratorInboundPortURI;
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * @param isUnitTest
	 */
	protected GasGeneratorTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest, GasGenerator.INBOUND_PORT_URI);
	}

	/**
	 * @param isUnitTest
	 * @param gasGeneratorInboundPortURI
	 */
	protected GasGeneratorTester(
			boolean isUnitTest,
			String gasGeneratorInboundPortURI
			) throws Exception
	{
		super(1, 0);

		this.isUnitTest = isUnitTest;
		this.initialise(gasGeneratorInboundPortURI);
	}
	
	/**
	 * @param isUnitTest
	 * @param gasGeneratorInboundPortURI
	 * @param reflectionInboundPortURI
	 */
	protected GasGeneratorTester(
			boolean isUnitTest,
			String gasGeneratorInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.isUnitTest = isUnitTest;
		this.initialise(gasGeneratorInboundPortURI);
	}
	
	// -------------------------------------------------------------------------
	// Initialize method
	// -------------------------------------------------------------------------

	/**
	 * @param gasGeneratorInboundPortURI
	 */
	protected void		initialise(
			String gasGeneratorInboundPortURI
			) throws Exception
	{
		this.gasGeneratorInboundPortURI = gasGeneratorInboundPortURI;
		this.ggop = new GasGeneratorOutboundPort(this);
		this.ggop.publishPort();

		this.tracer.get().setTitle("Gas Generator tester component");
		this.tracer.get().setRelativePosition(0, 0);
		this.toggleTracing();		
	}
	
	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	public void	testGetState() throws Exception
	{
		this.logMessage("testGetState()... ");
		if(GasGeneratorState.OFF != this.ggop.getState()) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testGetMode() throws Exception
	{
		this.logMessage("testGetMode()... ");
		if(GasGeneretorMode.LOW != this.ggop.getMode()) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void	testTurnOnOff() throws Exception
	{
		this.logMessage("testTurnOnOff()... ");
		if(GasGeneratorState.OFF == this.ggop.getState()) {
			this.ggop.turnOn();
			if(GasGeneratorState.ON != this.ggop.getState()) {
				this.logMessage("...KO... la  n'est pas ouvert");
				assertTrue(false);
			}
		} else {
			this.logMessage("...KO... le panneaux solaires est déjà ouvert");
			assertTrue(false);
		}
		
		this.ggop.turnOff();
		if(GasGeneratorState.OFF != this.ggop.getState()) {
			this.logMessage("...KO... le panneaux solaires n'est pas fermé après le test");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testSetLowHighMeddium() throws Exception
	{
		this.logMessage("testSetLowHighMeddium()... ");
		this.ggop.turnOn();
		if(GasGeneratorState.ON != this.ggop.getState()) {
			this.logMessage("...KO... le ventilateur n'est pas ouvert");
			assertTrue(false);
		}
		
		this.ggop.setHigh();
		if(GasGeneretorMode.HIGH != this.ggop.getMode()) {
			this.logMessage("...KO... le ventilateur n'est pas en mode HIGH");
			assertTrue(false);
		}
		this.ggop.setLow();
		if(GasGeneretorMode.LOW != this.ggop.getMode()) {
			this.logMessage("...KO... le ventilateur n'est pas en mode LOW");
			assertTrue(false);
		}
		this.ggop.setMeddium();
		if(GasGeneretorMode.MEDDIUM != this.ggop.getMode()) {
			this.logMessage("...KO... le ventilateur n'est pas en mode MEDDIUM");
			assertTrue(false);
		}
		
		this.ggop.turnOff();
		if(GasGeneratorState.OFF != this.ggop.getState()) {
			this.logMessage("...KO... le ventilateur n'est pas fermé après le test");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	protected void			runAllTests() throws Exception
	{
		this.testGetState();
		this.testGetMode();
		this.testTurnOnOff();
		this.testSetLowHighMeddium();
	}
	
	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void	start()
	throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
							this.ggop.getPortURI(),
							gasGeneratorInboundPortURI,
							GasGeneratorConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	@Override
	public synchronized void execute() throws Exception
	{
		if (!this.isUnitTest) {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			System.out.println("Fan Tester gets the clock");
			AcceleratedClock ac =
					this.clocksServerOutboundPort.getClock(
										CVMIntegrationTest.TEST_CLOCK_URI);

			System.out.println("Solar Pannel Tester waits until start");
			ac.waitUntilStart();
			System.out.println("Solar Pannel Tester waits to perform tests");
			this.doPortDisconnection(
						this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			Thread.sleep(3000);
		}
		this.runAllTests();
		this.logMessage("Le test pour le générateur de gaz est réussi!");
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.ggop.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.ggop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	
	

}
