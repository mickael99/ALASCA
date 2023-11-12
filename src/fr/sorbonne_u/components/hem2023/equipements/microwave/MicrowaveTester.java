/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.microwave;

import static org.junit.Assert.assertTrue;


import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2023.equipements.microwave.MicrowaveImplementationI.MicrowaveMode;
import fr.sorbonne_u.components.hem2023.equipements.microwave.MicrowaveImplementationI.MicrowaveState;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

/**
 * @author Yukhoi
 *
 */

@RequiredInterfaces(required = {MicrowaveUserCI.class, ClocksServerCI.class})
public class MicrowaveTester
extends AbstractComponent {
	
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
		this(isUnitTest, Microwave.INBOUND_PORT_URI);
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

	public void			testGetState() throws Exception
	{
		this.logMessage("testGetState()... ");

		if(MicrowaveState.OFF != this.mwop.getState()) {
		
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testGetMode() throws Exception
	{
		this.logMessage("testGetMode()... ");
		if(MicrowaveMode.LOW != this.mwop.getMode()) {
			this.logMessage("...KO...");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void			testTurnOnOff() throws Exception
	{
		this.logMessage("testTurnOnOff()... ");
		if (MicrowaveState.OFF == this.mwop.getState()) {
			this.mwop.turnOn();
			if(MicrowaveState.ON != this.mwop.getState()) {
				this.logMessage("...KO...Le micro-onde n'est pas allumé");
				assertTrue(false);
			}
		}
		this.mwop.turnOff();
		if(MicrowaveState.OFF != this.mwop.getState()) {
			this.logMessage("...KO...Le micro-onde n'est pas éteint");
			assertTrue(false);
		} 
		this.logMessage("...done.");
	}
	
	public void			testSetMode() throws Exception
	{
		this.logMessage("testSetMode()... ");
		this.mwop.setHigh();
		if(MicrowaveMode.HIGH != this.mwop.getMode()) {
			this.logMessage("...KO... Le micro-ondes n'a pas été réglé correctement en mode HIGH ");
			assertTrue(false);
		}
		this.mwop.setLow();
		if(MicrowaveMode.LOW != this.mwop.getMode()) {
			this.logMessage("...KO... Le micro-ondes n'a pas été réglé correctement en mode LOW ");
			assertTrue(false);
		}
		this.mwop.setMeddium();
		if(MicrowaveMode.MEDDIUM != this.mwop.getMode()) {
			this.logMessage("...KO... Le micro-ondes n'a pas été réglé correctement en mode MEDDIUM ");
			assertTrue(false);
		}
		this.mwop.setUnfreez();
		if(MicrowaveMode.UNFREEZE != this.mwop.getMode()) {
			this.logMessage("...KO... Le micro-ondes n'a pas été réglé correctement en mode UNFREEZE ");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	protected void			runAllTests() throws Exception
	{
		this.testGetState();
		this.testGetMode();
		this.testTurnOnOff();
		this.testSetMode();
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
							this.mwop.getPortURI(),
							microwaveInboundPortURI,
							MicrowaveConnector.class.getCanonicalName());
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
			System.out.println("Microwave Tester gets the clock");
			AcceleratedClock ac =
					this.clocksServerOutboundPort.getClock(
										CVMIntegrationTest.TEST_CLOCK_URI);

			System.out.println("Microwave Tester waits until start");
			ac.waitUntilStart();
			System.out.println("Microwave Tester waits to perform tests");
			this.doPortDisconnection(
						this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			Thread.sleep(3000);
		}
		this.runAllTests();
		this.logMessage("Le test pour le micro-ondes est réussi!");
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.mwop.getPortURI());
		super.finalise();
	}

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
//--------------------------------------------------------------------------------