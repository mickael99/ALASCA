/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutionException;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2023.equipements.fan.FanImplementationI.FanMode;
import fr.sorbonne_u.components.hem2023.equipements.fan.FanImplementationI.FanMusic;
import fr.sorbonne_u.components.hem2023.equipements.fan.FanImplementationI.FanState;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

/**
 * @author Yukhoi
 *
 */

@RequiredInterfaces(required = {FanUserCI.class, ClocksServerCI.class})
public class FanTester 
extends AbstractComponent {
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected final boolean				isUnitTest;
	protected FanOutboundPort		fop;
	protected String					fanInboundPortURI;
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * @param isUnitTest
	 */
	protected			FanTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest, Fan.INBOUND_PORT_URI);
	}

	/**
	 * @param isUnitTest
	 * @param fanInboundPortURI
	 */
	protected			FanTester(
			boolean isUnitTest,
			String fanInboundPortURI
			) throws Exception
		{
			super(1, 0);

			this.isUnitTest = isUnitTest;
			this.initialise(fanInboundPortURI);
		}
	
	/**
	 * @param isUnitTest
	 * @param fanInboundPortURI
	 * @param reflectionInboundPortURI

	 */
	protected			FanTester(
			boolean isUnitTest,
			String fanInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 0);

			this.isUnitTest = isUnitTest;
			this.initialise(fanInboundPortURI);
		}
	
	// -------------------------------------------------------------------------
	// Initialize method
	// -------------------------------------------------------------------------
	
	/**
	 * @param fanInboundPortURI

	 */
	protected void		initialise(
			String fanInboundPortURI
			) throws Exception
		{
			this.fanInboundPortURI = fanInboundPortURI;
			this.fop = new FanOutboundPort(this);
			this.fop.publishPort();

			this.tracer.get().setTitle("Hair dryer tester component");
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
			assertEquals(FanState.OFF, this.fop.getState());
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
			assertEquals(FanMode.LOW, this.fop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testTurnOnOff()
	{
		this.logMessage("testTurnOnOff()... ");
		try {
			assertEquals(FanState.OFF, this.fop.getState());
			this.fop.turnOn();
			assertEquals(FanState.ON, this.fop.getState());
			assertEquals(FanMode.LOW, this.fop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.fop.turnOn());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.fop.turnOff();
			assertEquals(FanState.OFF, this.fop.getState());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.fop.turnOff());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testSetLowHighMeddium()
	{
		this.logMessage("testSetLowHighMeddium()... ");
		try {
			this.fop.turnOn();
			this.fop.setHigh();
			assertEquals(FanState.ON, this.fop.getState());
			assertEquals(FanMode.HIGH, this.fop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.fop.setHigh());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.fop.setLow();
			assertEquals(FanState.ON, this.fop.getState());
			assertEquals(FanMode.LOW, this.fop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.fop.setLow());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.fop.setMeddium();
			assertEquals(FanState.ON, this.fop.getState());
			assertEquals(FanMode.MEDDIUM, this.fop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.fop.setMeddium());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.fop.turnOff();
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testTurnOnOffMusic() {
		this.logMessage("testTurnOnOffMusic()... ");
		try {
			this.fop.turnOn();
			this.fop.turnOnMusic();
			assertEquals(FanState.ON, this.fop.getState());
			assertEquals(FanMusic.ON, this.fop.getMusicState());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.fop.turnOnMusic());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.fop.turnOffMusic();
			assertEquals(FanState.ON, this.fop.getState());
			assertEquals(FanMusic.ON, this.fop.getMusicState());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.fop.turnOffMusic());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.fop.turnOff();
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
		this.testSetLowHighMeddium();
		this.testTurnOnOffMusic();
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
							this.fop.getPortURI(),
							fanInboundPortURI,
							FanConnector.class.getCanonicalName());
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
		this.doPortDisconnection(this.fop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.fop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

}
//-----------------------------------------------------------------------------

