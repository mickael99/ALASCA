/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.solarPannel;

import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2023.equipements.solarPannel.SolarPannelImplementationI.SolarPannelState;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

/**
 * @author Yukhoi
 *
 */
@RequiredInterfaces(required = {SolarPannelUserCI.class, ClocksServerCI.class})
public class SolarPannelTester extends AbstractComponent {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected final boolean				isUnitTest;
	protected SolarPannelOutboundPort		spop;
	protected String					solarPannelInboundPortURI;
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * @param isUnitTest
	 */
	protected SolarPannelTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest, SolarPannel.INBOUND_PORT_URI);
	}

	/**
	 * @param isUnitTest
	 * @param solarPannelInboundPortURI
	 */
	protected SolarPannelTester(
			boolean isUnitTest,
			String solarPannelInboundPortURI
			) throws Exception
	{
		super(1, 0);

		this.isUnitTest = isUnitTest;
		this.initialise(solarPannelInboundPortURI);
	}
	
	/**
	 * @param isUnitTest
	 * @param solarPannelInboundPortURI
	 * @param reflectionInboundPortURI
	 */
	protected SolarPannelTester(
			boolean isUnitTest,
			String solarPannelInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.isUnitTest = isUnitTest;
		this.initialise(solarPannelInboundPortURI);
	}
	
	// -------------------------------------------------------------------------
	// Initialize method
	// -------------------------------------------------------------------------

	/**
	 * @param solarPannelInboundPortURI
	 */
	protected void		initialise(
			String solarPannelInboundPortURI
			) throws Exception
	{
		this.solarPannelInboundPortURI = solarPannelInboundPortURI;
		this.spop = new SolarPannelOutboundPort(this);
		this.spop.publishPort();

		this.tracer.get().setTitle("Solar Pannel tester component");
		this.tracer.get().setRelativePosition(0, 0);
		this.toggleTracing();		
	}
	
	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	public void	testGetState() throws Exception
	{
		this.logMessage("testGetState()... ");
		if(SolarPannelState.OFF != this.spop.getState()) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	public void	testTurnOnOff() throws Exception
	{
		this.logMessage("testTurnOnOff()... ");
		if(SolarPannelState.OFF == this.spop.getState()) {
			this.spop.turnOn();
			if(SolarPannelState.ON != this.spop.getState()) {
				this.logMessage("...KO... le panneaux solaires n'est pas ouvert");
				assertTrue(false);
			}
		} else {
			this.logMessage("...KO... le panneaux solaires est déjà ouvert");
			assertTrue(false);
		}
		
		this.spop.turnOff();
		if(SolarPannelState.OFF != this.spop.getState()) {
			this.logMessage("...KO... le panneaux solaires n'est pas fermé après le test");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}
	
	protected void			runAllTests() throws Exception
	{
		this.testGetState();
		this.testTurnOnOff();
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
							this.spop.getPortURI(),
							solarPannelInboundPortURI,
							SolarPannelConnector.class.getCanonicalName());
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
		this.logMessage("Solar Pannel Tester ends");
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.spop.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.spop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	
}
