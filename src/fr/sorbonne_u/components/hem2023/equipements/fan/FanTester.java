/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan;

import static org.junit.jupiter.api.Assertions.assertTrue;


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

			this.tracer.get().setTitle("Fan tester component");
			this.tracer.get().setRelativePosition(0, 0);
			this.toggleTracing();		
		}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	public void			testGetState() throws Exception
	{
		this.logMessage("testGetState()... ");
		if(FanState.OFF != this.fop.getState()) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testGetMode() throws Exception
	{
		this.logMessage("testGetMode()... ");
		if(FanMode.LOW != this.fop.getMode()) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testTurnOnOff() throws Exception
	{
		this.logMessage("testTurnOnOff()... ");
		if(FanState.OFF == this.fop.getState()) {
			this.fop.turnOn();
			if(FanState.ON != this.fop.getState()) {
				this.logMessage("...KO... le ventilateur n'est pas ouvert");
				assertTrue(false);
			}
			if(FanMode.LOW != this.fop.getMode()) {
				this.logMessage("...KO... le ventilateur n'est pas dans le bonne mode en ouvrant");
				assertTrue(false);
			}
		} else {
			this.logMessage("...KO... le ventilateur est déjà ouvert");
			assertTrue(false);
		}
		
		this.fop.turnOff();
		if(FanState.OFF != this.fop.getState()) {
			this.logMessage("...KO... le ventilateur n'est pas fermé après le test");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testSetLowHighMeddium() throws Exception
	{
		this.logMessage("testSetLowHighMeddium()... ");
		this.fop.turnOn();
		if(FanState.ON != this.fop.getState()) {
			this.logMessage("...KO... le ventilateur n'est pas ouvert");
			assertTrue(false);
		}
		
		this.fop.setHigh();
		if(FanMode.HIGH != this.fop.getMode()) {
			this.logMessage("...KO... le ventilateur n'est pas en mode HIGH");
			assertTrue(false);
		}
		this.fop.setLow();
		if(FanMode.LOW != this.fop.getMode()) {
			this.logMessage("...KO... le ventilateur n'est pas en mode LOW");
			assertTrue(false);
		}
		this.fop.setMeddium();
		if(FanMode.MEDDIUM != this.fop.getMode()) {
			this.logMessage("...KO... le ventilateur n'est pas en mode MEDDIUM");
			assertTrue(false);
		}
		
		this.fop.turnOff();
		if(FanState.OFF != this.fop.getState()) {
			this.logMessage("...KO... le ventilateur n'est pas fermé après le test");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testTurnOnOffMusic() throws Exception {
		this.logMessage("testTurnOnOffMusic()... ");
		this.fop.turnOn();
		if(FanState.ON != this.fop.getState()) {
			this.logMessage("...KO... le ventilateur n'est pas ouvert");
			assertTrue(false);
		}
		
		this.fop.turnOnMusic();
		if(FanMusic.ON != this.fop.getMusicState()) {
			this.logMessage("...KO... le musique de ventilateur n'est pas ouvert");
			assertTrue(false);
		}
		this.fop.turnOffMusic();
		if(FanMusic.OFF != this.fop.getMusicState()) {
			this.logMessage("...KO... le musique de ventilateur n'est pas fermé");
			assertTrue(false);
		}
		
		this.fop.turnOff();
		if(FanState.OFF != this.fop.getState()) {
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
		this.testTurnOnOffMusic();
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
							this.fop.getPortURI(),
							fanInboundPortURI,
							FanConnector.class.getCanonicalName());
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

			System.out.println("Fan Tester waits until start");
			ac.waitUntilStart();
			System.out.println("Fan Tester waits to perform tests");
			this.doPortDisconnection(
						this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			Thread.sleep(3000);
		}
		this.runAllTests();
		this.logMessage("Fan Tester ends");
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.fop.getPortURI());
		super.finalise();
	}

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

