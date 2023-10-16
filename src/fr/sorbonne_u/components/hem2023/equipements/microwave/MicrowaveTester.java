/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.microwave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutionException;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2023.equipements.fan.Fan;
import fr.sorbonne_u.components.hem2023.equipements.microwave.MicrowaveImplementationI.MicrowaveMode;
import fr.sorbonne_u.components.hem2023.equipements.microwave.MicrowaveImplementationI.MicrowaveState;
import fr.sorbonne_u.components.hem2023.equipements.fan.FanImplementationI.FanMode;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

/**
 * @author Yukhoi
 *
 */
public class MicrowaveTester extends AbstractComponent {
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected final boolean				isUnitTest;
	protected MicrowaveOutboundPort		mwop;
	protected String					microwaveInboundPortURI;
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * @param isUnitTest
	 */
	protected			MicrowaveTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest, Fan.INBOUND_PORT_URI);
	}

	/**
	 * @param isUnitTest
	 * @param microwaveInboundPortURI
	 */
	protected			MicrowaveTester(
			boolean isUnitTest,
			String microwaveInboundPortURI
			) throws Exception
		{
			super(1, 0);

			this.isUnitTest = isUnitTest;
			this.initialise(microwaveInboundPortURI);
		}
	
	/**
	 * @param isUnitTest
	 * @param microwaveInboundPortURI
	 * @param reflectionInboundPortURI

	 */
	protected			MicrowaveTester(
			boolean isUnitTest,
			String microwaveInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 0);

			this.isUnitTest = isUnitTest;
			this.initialise(microwaveInboundPortURI);
		}
	
	// -------------------------------------------------------------------------
	// Initialize method
	// -------------------------------------------------------------------------
	
	/**
	 * @param microwaveInboundPortURI

	 */
	protected void		initialise(
			String microwaveInboundPortURI
			) throws Exception
		{
			this.microwaveInboundPortURI = microwaveInboundPortURI;
			this.mwop = new MicrowaveOutboundPort(this);
			this.mwop.publishPort();

			this.tracer.get().setTitle("Microwave tester component");
			this.tracer.get().setRelativePosition(0, 0);
			this.toggleTracing();		
		}
	
	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	public void			testGetState()
	{
		this.logMessage("testGetState()... ");
		try {
			assertEquals(MicrowaveState.OFF, this.mwop.getState());
		} catch (Exception e) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testGetMode()
	{
		this.logMessage("testGetMode()... ");
		try {
			assertEquals(FanMode.LOW, this.mwop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testTurnOnOff()
	{
		this.logMessage("testTurnOnOff()... ");
		try {
			assertEquals(MicrowaveState.OFF, this.mwop.getState());
			this.mwop.turnOn();
			assertEquals(MicrowaveState.ON, this.mwop.getState());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.mwop.turnOn());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.mwop.turnOff();
			assertEquals(MicrowaveState.OFF, this.mwop.getState());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.mwop.turnOff());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testSetMode()
	{
		this.logMessage("testSetMode()... ");
		try {
			this.mwop.setHigh();
			assertEquals(MicrowaveMode.HIGH, this.mwop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.mwop.setHigh());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.mwop.setLow();
			assertEquals(MicrowaveMode.LOW, this.mwop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.mwop.setLow());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.mwop.setMeddium();
			assertEquals(MicrowaveMode.MEDDIUM, this.mwop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.mwop.setMeddium());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.mwop.setUnfreez();
			assertEquals(MicrowaveMode.MEDDIUM, this.mwop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.mwop.setMeddium());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	protected void			runAllTests()
	{
		this.testGetState();
		this.testGetMode();
		this.testTurnOnOff();
		this.testSetMode();
	}
	
	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void	start()
	throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
							this.mwop.getPortURI(),
							microwaveInboundPortURI,
							MicrowaveConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
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

			System.out.println("Fan Tester waits until start");
			ac.waitUntilStart();
			System.out.println("Fan Tester waits to perform tests");
			this.doPortDisconnection(
						this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			Thread.sleep(3000);
		}
		this.runAllTests();
		System.out.println("Fan Tester ends");
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.mwop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.mwop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}


}
