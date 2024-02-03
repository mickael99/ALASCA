/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.utils.ExecutionType;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.MILSimulationArchitectures;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * @author Yukhoi
 *
 */
@OfferedInterfaces(offered={FanUserCI.class})
public class Fan 
extends AbstractCyPhyComponent 
implements FanImplementationI {
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the hair dryer inbound port used in tests.					*/
	public static final String			REFLECTION_INBOUND_PORT_URI =
												"FAN-RIP-URI";	
	/** URI of the fan inbound port used in tests.					*/
	public static final String			INBOUND_PORT_URI =
												"FAN-INBOUND-PORT-URI";
	/** when true, methods trace their actions.								*/
	public static boolean				VERBOSE = true;
	public static final FanState		INITIAL_STATE = FanState.OFF;
	public static final FanMode			INITIAL_MODE = FanMode.LOW;
	public static final FanMusic		INITIAL_MUSIC_STATE = FanMusic.OFF;
	
	/** current state (on, off) of the fan.							*/
	protected FanState			currentState;
	/** current mode of operation (low, high) of the fan.			*/
	protected FanMode			currentMode;
	/** current mode of operation (low, high) of the fan.			*/
    protected FanMusic 			currentMusicState;

	/** inbound port offering the <code>HairDryerCI</code> interface.		*/
	protected FanInboundPort	fip;
	
	// Execution/Simulation
	protected AtomicSimulatorPlugin		asp;
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


	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a fan component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code INBOUND_PORT_URI != null}
	 * pre	{@code !INBOUND_PORT_URI.isEmpty()}
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected Fan()
	throws Exception
	{
		this(INBOUND_PORT_URI);
	}
	
	/**
	 * create a fan component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null}
	 * pre	{@code !fanInboundPortURI.isEmpty()}
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 * 
	 * @param hairDryerInboundPortURI	URI of the hair dryer inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected Fan(String fanInboundPortURI)
	throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, fanInboundPortURI,
				 ExecutionType.STANDARD, null, null, 0.0);
	}

	/**
	 * create a fan component with the given reflection inbound port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null}
	 * pre	{@code !fanInboundPortURI.isEmpty()}
	 * pre	{@code reflectionInboundPortURI != null}
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 *
	 * @param hairDryerInboundPortURI	URI of the fan inbound port.
	 * @param reflectionInboundPortURI	URI of the reflection innbound port of the component.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			Fan(
			String reflectionInboundPortURI,
			String fanInboundPortURI,
			ExecutionType currentExecutionType,
			String simArchitectureURI,
			String localSimulatorURI,
			double accFactor
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 0);

			assert	fanInboundPortURI != null &&
												!fanInboundPortURI.isEmpty() :
					new PreconditionException(
							"fanInboundPortURI != null && "
							+ "!fanInboundPortURI.isEmpty()");
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

			if (this.currentExecutionType.isUnitTest()) {
				Fan.VERBOSE = true;
			}

			this.initialise(fanInboundPortURI);
		}
	
	


	// -------------------------------------------------------------------------
	// Initialize method
	// -------------------------------------------------------------------------

	/**
	 * initialise the fan component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null}
	 * pre	{@code !fanInboundPortURI.isEmpty()}
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * post {@code getMode() == FanMusic.OFF}
	 * </pre>
	 * 
	 * @param fanInboundPortURI	URI of the fan inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		initialise(String fanInboundPortURI)
	throws Exception
	{
		assert	fanInboundPortURI != null :
					new PreconditionException(
										"fanInboundPortURI != null");
		assert	!fanInboundPortURI.isEmpty() :
					new PreconditionException(
										"!fanInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.fip = new FanInboundPort(fanInboundPortURI, this);
		this.fip.publishPort();
		
		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			Architecture architecture =
				MILSimulationArchitectures.createFanMILArchitecture();
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
					new AssertionError(
							"local simulator " + this.localSimulatorURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
						put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		case MIL_RT_SIMULATION:
		case SIL_SIMULATION:
			architecture =
				MILSimulationArchitectures.
							createFanRTArchitecture(
									this.currentExecutionType,
									this.accFactor);
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
					new AssertionError(
							"local simulator " + this.localSimulatorURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
					put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		case STANDARD:
		case UNIT_TEST:
		default:
		}		

		if (Fan.VERBOSE) {
			this.tracer.get().setTitle("Fan component");
			this.tracer.get().setRelativePosition(1, 0);
			this.toggleTracing();
		}
	}
	
	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			switch (this.currentExecutionType) {
			case MIL_SIMULATION:
				this.asp = new AtomicSimulatorPlugin();
				String uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				Architecture architecture =
					(Architecture) this.localSimulatorsArchitectures.get(uri);
				this.asp.setPluginURI(uri);
				this.asp.setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				break;
			case MIL_RT_SIMULATION:
			case SIL_SIMULATION:
				this.asp = new RTAtomicSimulatorPlugin();
				uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				architecture =
						(Architecture) this.localSimulatorsArchitectures.get(uri);
				((RTAtomicSimulatorPlugin)this.asp).setPluginURI(uri);
				((RTAtomicSimulatorPlugin)this.asp).
										setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				break;
			case STANDARD:
			case UNIT_TEST:
			default:
			}		
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}		
	}
	
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.fip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component Methods
	// -------------------------------------------------------------------------

	@Override
	public FanState getState() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Le ventilateur renvoie son état : " +
													this.currentState + ".\n");
		}
		return this.currentState;
	}

	@Override
	public FanMode getMode() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Le ventilateur renvoie son mode : " +
													this.currentMode + ".\n");
		}

		return this.currentMode;
	}

	@Override
	public FanMusic getMusicState() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Le ventilateur renvoie son état de musique : " +
													this.currentMusicState + ".\n");
		}

		return this.currentMusicState;
	}
	
	@Override
	public void turnOn() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Le ventilateur est ouvert.\n");
		}

		assert	this.getState() == FanState.OFF :
				new PreconditionException("getState() == FanState.OFF");

		this.currentState = FanState.ON;
		this.currentMode = FanMode.LOW;
		this.currentMusicState = FanMusic.OFF;
	}

	@Override
	public void turnOff() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Le ventilateur est fermé.\n");
		}
		
		assert	this.getState() == FanState.ON :
				new PreconditionException("getState() == FanState.ON");
		
		this.currentState = FanState.OFF;
	}

	@Override
	public void turnOnMusic() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("La musique du ventilateur est allumé.\n");
		}
		
		assert	this.getMusicState() == FanMusic.OFF :
				new PreconditionException("getMusicState() == FanMusic.OFF");
		
		this.currentMusicState = FanMusic.ON;
	}

	@Override
	public void turnOffMusic() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("La musique du ventilateur est éteint.\n");
		}

		assert	this.getState() == FanState.ON :
				new PreconditionException("getState() == FanState.ON");
		assert	this.getMusicState() == FanMusic.ON :
				new PreconditionException("getMusicState() == FanMusic.ON");

		this.currentMusicState = FanMusic.OFF;
	}

	@Override
	public void setHigh() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Le ventilateur est reglé en mode HIGH.\n");
		}

		assert	this.getState() == FanState.ON :
				new PreconditionException("getState() == FanState.ON");
		assert	this.getMode() != FanMode.HIGH :
				new PreconditionException("getMode() != FanMode.HIGH");

		this.currentMode = FanMode.HIGH;
	}

	@Override
	public void setLow() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Le ventilateur est reglé en mode LOW.\n");
		}

		assert	this.getState() == FanState.ON :
				new PreconditionException("getState() == FanState.ON");
		assert	this.getMode() != FanMode.LOW :
				new PreconditionException("getMode() != FanMode.LOW");

		this.currentMode = FanMode.LOW;
	}

	@Override
	public void setMeddium() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Le ventilateur est reglé en mode MEDDIUM.\n");
		}

		assert	this.getState() == FanState.ON :
				new PreconditionException("getState() == FanState.ON");
		assert	this.getMode() != FanMode.MEDDIUM :
				new PreconditionException("getMode() != FanMode.MEDDIUM");

		this.currentMode = FanMode.MEDDIUM;
	}

}
//-----------------------------------------------------------------------------

