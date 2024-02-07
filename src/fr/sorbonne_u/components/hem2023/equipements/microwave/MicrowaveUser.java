/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.microwave;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.CVMGlobalTest;
import fr.sorbonne_u.components.hem2023.equipements.microwave.MicrowaveImplementationI.MicrowaveMode;
import fr.sorbonne_u.components.hem2023.equipements.microwave.MicrowaveImplementationI.MicrowaveState;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.MILSimulationArchitectures;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.MicrowaveOperationI;
import fr.sorbonne_u.components.hem2023.utils.ExecutionType;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.exceptions.PreconditionException;
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

public class MicrowaveUser
extends		AbstractCyPhyComponent
implements MicrowaveOperationI{
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** standard reflection, inbound port URI for the {@code HairDryerUser}
	 *  component.															*/
	public static final String			REFLECTION_INBOUND_PORT_URI =
													"HAIR-DRYER-USER-RIP-URI";
	/** when true, operations are traced.									*/
	public static boolean				VERBOSE = true ;

	/** outbound port to connect to the {@code HairDryer} component.		*/
	protected MicrowaveOutboundPort		mwop;
	/** service inbound port URI of the {@code HairDryer} component.		*/
	protected String					microwaveInboundPortURI;

	// Execution/Simulation

	/** current type of execution.											*/
	protected final ExecutionType		currentExecutionType;
	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.				*/
	protected final String				simArchitectureURI;
	/** URI of the local simulator used to compose the global simulation
	 *  architecture.														*/
	protected final String				localSimulatorURI;
	/** acceleration factor to be used when running the real time
	 *  simulation.															*/
	protected double					accFactor;
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;
	/** clock used for time-triggered synchronisation in test actions.		*/
	protected AcceleratedClock			acceleratedClock;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			MicrowaveUser() throws Exception
	{
		this(Microwave.INBOUND_PORT_URI, ExecutionType.STANDARD);
	}

	protected			MicrowaveUser(
		String microwaveInboundPortURI,
		ExecutionType currentExecutionType
		) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, microwaveInboundPortURI,
			 currentExecutionType, null, null, 0.0);

		assert	currentExecutionType.isStandard() ||
										currentExecutionType.isIntegrationTest() :
				new PreconditionException(
						"currentExecutionType.isStandard() || "
						+ "currentExecutionType.isUnitTest()");
	}
	
	protected			MicrowaveUser(
		String reflectionInboundPortURI,
		String microwaveInboundPortURI,
		ExecutionType currentExecutionType,
		String simArchitectureURI,
		String localSimulatorURI,
		double accFactor
		) throws Exception
	{
		super(reflectionInboundPortURI, 2, 1);

		assert	microwaveInboundPortURI != null &&
										!microwaveInboundPortURI.isEmpty() :
				new PreconditionException(
						"hairDryerInboundPortURI != null && "
						+ "!hairDryerInboundPortURI.isEmpty()");
		assert	currentExecutionType != null :
				new PreconditionException("currentExecutionType != null");
		assert	!currentExecutionType.isSimulated() ||
								(simArchitectureURI != null &&
											!simArchitectureURI.isEmpty()) :
				new PreconditionException(
						"currentExecutionType.isSimulated() ||  "
						+ "(simArchitectureURI != null && "
						+ "!simArchitectureURI.isEmpty())");
		assert	!currentExecutionType.isSimulated() ||
								(localSimulatorURI != null &&
											!localSimulatorURI.isEmpty()) :
				new PreconditionException(
						"currentExecutionType.isSimulated() ||  "
						+ "(localSimulatorURI != null && "
						+ "!localSimulatorURI.isEmpty())");
		assert	!currentExecutionType.isSIL() || accFactor > 0.0 :
				new PreconditionException(
						"!currentExecutionType.isSIL() || accFactor > 0.0");

		this.currentExecutionType = currentExecutionType;
		this.simArchitectureURI = simArchitectureURI;
		this.localSimulatorURI = localSimulatorURI;
		this.accFactor = accFactor;

		this.initialise(microwaveInboundPortURI);
	}
	
	protected void		initialise(
		String microwaveInboundPortURI
		) throws Exception
	{
		this.microwaveInboundPortURI = microwaveInboundPortURI;
		this.mwop = new MicrowaveOutboundPort(this);
		this.mwop.publishPort();

		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			Architecture architecture =
					MILSimulationArchitectures.
								createMicrowaveUserMILArchitecture();
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
					new AssertionError(
							"local simulator " + this.localSimulatorURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
						put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		case MIL_RT_SIMULATION:
			architecture =
				MILSimulationArchitectures.
							createMicrowaveUserMILRTArchitecture(
															this.accFactor);
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
					new AssertionError(
							"local simulator " + this.localSimulatorURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
						put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		default:
		}		

		this.tracer.get().setTitle("Hair dryer user component");
		this.tracer.get().setRelativePosition(2, 1);
		this.toggleTracing();		
	}
	
	// -------------------------------------------------------------------------
	// Component internal testing method triggered by the SIL simulator
	// -------------------------------------------------------------------------

	@Override
	public void			turnOn()
	{
		if (VERBOSE) {
			this.logMessage("MicrowaveUser#turnOn().");
		}
		try {
			this.mwop.turnOn();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	@Override
	public void			turnOff()
	{
		if (VERBOSE) {
			this.logMessage("MicrowaveUser#turnOff().");
		}
		try {
			this.mwop.turnOff();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
	
	@Override
	public void			setMeddium()
	{
		if (VERBOSE) {
			this.logMessage("MicrowaveUser#setMeddium().");
		}
		try {
			this.mwop.setMeddium();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
	
	@Override
	public void			setLow()
	{
		if (VERBOSE) {
			this.logMessage("MicrowaveUser#setLow().");
		}
		try {
			this.mwop.setLow();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
	
	@Override
	public void			setUnfreeze()
	{
		if (VERBOSE) {
			this.logMessage("MicrowaveUser#setUnfreeze().");
		}
		try {
			this.mwop.setUnfreeze();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
	
	@Override
	public void			setHigh()
	{
		if (VERBOSE) {
			this.logMessage("MicrowaveUser#setHigh().");
		}
		try {
			this.mwop.setHigh();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
	
	// -------------------------------------------------------------------------
	// Component internal tests
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
			assertEquals(MicrowaveMode.LOW, this.mwop.getMode());
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
			assertEquals(MicrowaveMode.LOW, this.mwop.getMode());
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
			this.mwop.turnOn();
			this.mwop.setHigh();
			assertEquals(MicrowaveState.ON, this.mwop.getState());
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
			this.mwop.setMeddium();
			assertEquals(MicrowaveState.ON, this.mwop.getState());
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
			this.mwop.setMeddium();
			assertEquals(MicrowaveState.ON, this.mwop.getState());
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
			this.mwop.setUnfreeze();
			assertEquals(MicrowaveState.ON, this.mwop.getState());
			assertEquals(MicrowaveMode.UNFREEZE, this.mwop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.mwop.setUnfreeze());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.mwop.turnOff();
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

	@Override
	public synchronized void	start()
	throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.mwop.getPortURI(),
					this.microwaveInboundPortURI,
					MicrowaveConnector.class.getCanonicalName());

			switch (this.currentExecutionType) {
			case MIL_SIMULATION:
				AtomicSimulatorPlugin asp = new AtomicSimulatorPlugin();
				String uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				Architecture architecture =
					(Architecture) this.localSimulatorsArchitectures.get(uri);
				asp.setPluginURI(uri);
				asp.setSimulationArchitecture(architecture);
				this.installPlugin(asp);
				break;
			case MIL_RT_SIMULATION:
				RTAtomicSimulatorPlugin rtasp = new RTAtomicSimulatorPlugin();
				uri = this.architecturesURIs2localSimulatorURIS.
											get(this.simArchitectureURI);
				architecture =
					(Architecture) this.localSimulatorsArchitectures.get(uri);
				rtasp.setPluginURI(uri);
				rtasp.setSimulationArchitecture(architecture);
				this.installPlugin(rtasp);
				break;
			default:
			}		
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	@Override
	public synchronized void execute() throws Exception
	{
		this.logMessage("MicrowaveUser executes.");
		if (this.currentExecutionType.isIntegrationTest() ||
										this.currentExecutionType.isSIL()) {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			this.logMessage("MicrowaveUser gets the clock.");
			this.acceleratedClock =
				this.clocksServerOutboundPort.getClock(CVMGlobalTest.CLOCK_URI);
			this.doPortDisconnection(
								this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			this.logMessage("MicrowaveUser waits until start time.");
			this.acceleratedClock.waitUntilStart();
			this.logMessage("MicrowaveUser starts.");
			if (this.currentExecutionType.isIntegrationTest()) {
				this.logMessage("MicrowaveUser begins to perform unit tests.");
				this.runAllTests();
				this.logMessage("MicrowaveUser unit tests end.");
			} else {
				this.logMessage("MicrowaveUser begins to perform SIL scenario.");
				this.silTestScenario();
				this.logMessage("MicrowaveUser SIL scenario end.");				
			}
		}
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
	
	// -------------------------------------------------------------------------
	// SIL test scenarios
	// -------------------------------------------------------------------------

	protected void				silTestScenario()
	{
		// Define the instants of the different actions in the scenario.
		Instant startInstant = Instant.parse(CVMGlobalTest.START_INSTANT);
		Instant switchOn = startInstant.plusSeconds(3600L);
		Instant setHigh = startInstant.plusSeconds(3800L);
		Instant setLow = startInstant.plusSeconds(4500L);
		Instant setMeddium = startInstant.plusSeconds(5200L);
		Instant setUnfreeze = startInstant.plusSeconds(5900L);
		Instant switchOff = startInstant.plusSeconds(6400L);

		// For each action, compute the waiting time for this action using the
		// above instant and the clock, and then schedule the rask that will
		// perform the action at the appropriate time.
		long delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(switchOn);
		this.logMessage(
				"HairDryer#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + switchOn);
		this.scheduleTask(
				o -> ((MicrowaveUser)o).turnOn(),
				delayInNanos, TimeUnit.NANOSECONDS);
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(setHigh);
		this.logMessage(
				"HairDryer#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + setHigh);
		this.scheduleTask(
				o -> ((MicrowaveUser)o).setHigh(),
				delayInNanos, TimeUnit.NANOSECONDS);
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(setLow);
		this.logMessage(
				"HairDryer#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + setLow);
		this.scheduleTask(
				o -> ((MicrowaveUser)o).setLow(),
				delayInNanos, TimeUnit.NANOSECONDS);
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(setMeddium);
		this.logMessage(
				"HairDryer#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + setMeddium);
		this.scheduleTask(
				o -> ((MicrowaveUser)o).setMeddium(),
				delayInNanos, TimeUnit.NANOSECONDS);
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(setUnfreeze);
		this.logMessage(
				"HairDryer#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + setUnfreeze);
		this.scheduleTask(
				o -> ((MicrowaveUser)o).setUnfreeze(),
				delayInNanos, TimeUnit.NANOSECONDS);
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(switchOff);
		this.logMessage(
				"HairDryer#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + switchOff);
		this.scheduleTask(
				o -> ((MicrowaveUser)o).turnOff(),
				delayInNanos, TimeUnit.NANOSECONDS);
	}
}
//-----------------------------------------------------------------------------
