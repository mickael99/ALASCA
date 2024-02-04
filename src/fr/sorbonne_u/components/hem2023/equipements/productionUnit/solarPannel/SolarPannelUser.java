package fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel;

import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.interfaces.*;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.interfaces.SolarPannelImplementationI.SolarPannelState;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.ports.*;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil.MILSimulationArchitectures;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil.SolarPannelOperationI;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.CVMGlobalTest;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.SolarPannel;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.connectors.SolarPannelConnector;
import fr.sorbonne_u.components.hem2023.utils.ExecutionType;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>HairDryerTester</code> implements a component performing
 * tests for the class <code>HairDryer</code> as a BCM component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-09-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@RequiredInterfaces(required = {SolarPannelUserCI.class, ClocksServerCI.class})
public class			SolarPannelUser
extends		AbstractCyPhyComponent
implements	SolarPannelOperationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** standard reflection, inbound port URI for the {@code HairDryerUser}
	 *  component.															*/
	public static final String			REFLECTION_INBOUND_PORT_URI =
													"SOLAR-PANNEL-USER-RIP-URI";
	/** when true, operations are traced.									*/
	public static boolean				VERBOSE = true ;

	/** outbound port to connect to the {@code Fan} component.		*/
	protected SolarPannelOutboundPort			spop;
	/** service inbound port URI of the {@code Fan} component.		*/
	protected String					solarPannelInboundPortURI;

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

	/**
	 * create a fan component with the standard URIs and execution types.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected			SolarPannelUser() throws Exception
	{
		this(SolarPannel.INBOUND_PORT_URI, ExecutionType.STANDARD);
	}

	/**
	 * create a fan component with the given URIs and execution types.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code currentExecutionType.isStandard() || currentExecutionType.isUnitTest()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param fanInboundPortURI	URI of the fan inbound port.
	 * @param currentExecutionType		current execution type for the next run.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			SolarPannelUser(
		String solarPannelInboundPortURI,
		ExecutionType currentExecutionType
		) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, solarPannelInboundPortURI,
			 currentExecutionType, null, null, 0.0);

		assert	currentExecutionType.isStandard() ||
										currentExecutionType.isUnitTest() :
				new PreconditionException(
						"currentExecutionType.isStandard() || "
						+ "currentExecutionType.isUnitTest()");
	}

	/**
	 * create a fan component with the given URIs and execution types.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null && !fanInboundPortURI.isEmpty()}
	 * pre	{@code currentExecutionType != null}
	 * pre	{@code !currentExecutionType.isSimulated() || (simArchitectureURI != null && !simArchitectureURI.isEmpty())}
	 * pre	{@code !currentExecutionType.isSimulated() || (localSimulatorURI != null && !localSimulatorURI.isEmpty())}
	 * pre	{@code !currentExecutionType.isSIL() || accFactor > 0.0}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the component.
	 * @param hairDryerInboundPortURI	URI of the hair dryer inbound port.
	 * @param currentExecutionType		current execution type for the next run.
	 * @param simArchitectureURI		URI of the simulation architecture to be created or the empty string if the component does not execute as a simulation.
	 * @param localSimulatorURI			URI of the local simulator to be used in the simulation architecture.
	 * @param accFactor					acceleration factor for the simulation.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			SolarPannelUser(
		String reflectionInboundPortURI,
		String solarPannelInboundPortURI,
		ExecutionType currentExecutionType,
		String simArchitectureURI,
		String localSimulatorURI,
		double accFactor
		) throws Exception
	{
		super(reflectionInboundPortURI, 2, 1);

		assert	solarPannelInboundPortURI != null &&
										!solarPannelInboundPortURI.isEmpty() :
				new PreconditionException(
						"solarPannelInboundPortURI != null && "
						+ "!solarPannelInboundPortURI.isEmpty()");
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

		this.initialise(solarPannelInboundPortURI);
	}

	/**
	 * initialise the component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param hairDryerInboundPortURI	URI of the hair dryer inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		initialise(
		String solarPannelInboundPortURI
		) throws Exception
	{
		this.solarPannelInboundPortURI = solarPannelInboundPortURI;
		this.spop = new SolarPannelOutboundPort(this);
		this.spop.publishPort();

		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			Architecture architecture =
					MILSimulationArchitectures.
								createSolarPannelUserMILArchitecture();
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
							createSolarPannelUserMILRTArchitecture(
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

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.HairDryerOperationI#turnOn()
	 */
	@Override
	public void			turnOn()
	{
		if (VERBOSE) {
			this.logMessage("FanUser#turnOn().");
		}
		try {
			this.spop.turnOn();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.HairDryerOperationI#turnOff()
	 */
	@Override
	public void			turnOff()
	{
		if (VERBOSE) {
			this.logMessage("FanUser#turnOff().");
		}
		try {
			this.spop.turnOff();
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
			assertEquals(SolarPannelState.OFF, this.spop.getState());
		} catch (Exception e) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}


	public void			testTurnOnOff()
	{
		this.logMessage("testTurnOnOff()... ");
		try {
			assertEquals(SolarPannelState.OFF, this.spop.getState());
			this.spop.turnOn();
			assertEquals(SolarPannelState.ON, this.spop.getState());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.spop.turnOn());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.spop.turnOff();
			assertEquals(SolarPannelState.OFF, this.spop.getState());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.spop.turnOff());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}


	protected void			runAllTests()
	{
		this.testGetState();
		this.testTurnOnOff();
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
					this.spop.getPortURI(),
					this.solarPannelInboundPortURI,
					SolarPannelConnector.class.getCanonicalName());

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

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void execute() throws Exception
	{
		this.logMessage("HairDryerUser executes.");
		if (this.currentExecutionType.isUnitTest() ||
										this.currentExecutionType.isSIL()) {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			this.logMessage("HairDryerUser gets the clock.");
			this.acceleratedClock =
				this.clocksServerOutboundPort.getClock(CVMGlobalTest.CLOCK_URI);
			this.doPortDisconnection(
								this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			this.logMessage("FanUser waits until start time.");
			this.acceleratedClock.waitUntilStart();
			this.logMessage("FanUser starts.");
			if (this.currentExecutionType.isUnitTest()) {
				this.logMessage("FanUser begins to perform unit tests.");
				this.runAllTests();
				this.logMessage("FanUser unit tests end.");
			} else {
				this.logMessage("FanUser begins to perform SIL scenario.");
				this.silTestScenario();
				this.logMessage("FanUser SIL scenario end.");				
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.spop.getPortURI());

		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
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
		Instant switchOff = startInstant.plusSeconds(5000L);

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
				o -> ((SolarPannelUser)o).turnOn(),
				delayInNanos, TimeUnit.NANOSECONDS);
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(switchOff);
		this.logMessage(
				"HairDryer#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + switchOff);
		this.scheduleTask(
				o -> ((SolarPannelUser)o).turnOff(),
				delayInNanos, TimeUnit.NANOSECONDS);
	}

}
// -----------------------------------------------------------------------------
