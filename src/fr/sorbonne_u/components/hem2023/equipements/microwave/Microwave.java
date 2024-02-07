/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.microwave;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.MILSimulationArchitectures;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.MicrowaveStateModel;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SetHighMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SetLowMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SetMediumMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SetUnfreezeMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SwitchOffMicrowave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.mil.events.SwitchOnMicrowave;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.hem2023.utils.ExecutionType;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * @author Yukhoi
 *
 */
@OfferedInterfaces(offered={MicrowaveUserCI.class})

public class Microwave
extends AbstractCyPhyComponent
implements MicrowaveImplementationI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the hair dryer inbound port used in tests.					*/
	public static final String			REFLECTION_INBOUND_PORT_URI =
												"MICROWAVE-RIP-URI";	

	/** URI of the microwave inbound port used in tests.					*/
	public static final String			INBOUND_PORT_URI =
												"MICROWAVE-INBOUND-PORT-URI";
	
	/** when true, methods trace their actions.								*/
	public static boolean				VERBOSE = true;
	public static final MicrowaveState		INITIAL_STATE = MicrowaveState.OFF;
	public static final MicrowaveMode		INITIAL_MODE = MicrowaveMode.LOW;

	/** current state (on, off) of the microwave.							*/
	protected MicrowaveState		currentState;
	/** current mode of operation (low, high) of the microwave.			*/
	protected MicrowaveMode			currentMode;

	/** inbound port offering the <code>MicrowaveCI</code> interface.		*/
	protected MicrowaveInboundPort	mwip;

	// Execution/Simulation

	/** plug-in holding the local simulation architecture and simulators.	*/
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
	 * create a microwave component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code INBOUND_PORT_URI != null}
	 * pre	{@code !INBOUND_PORT_URI.isEmpty()}
	 * post	{@code getState() == MicrowaveState.OFF}
	 * post	{@code getMode() == MicrowaveMode.LOW}
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected			Microwave()
	throws Exception
	{
		this(INBOUND_PORT_URI);
	}
	
	/**
	 * create a microwave component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code microwaveInboundPortURI != null}
	 * pre	{@code !microwaveInboundPortURI.isEmpty()}
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 * 
	 * @param microwaveInboundPortURI	URI of the microwave inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			Microwave(String microwaveInboundPortURI)
			throws Exception
			{
				this(REFLECTION_INBOUND_PORT_URI, microwaveInboundPortURI,
					 ExecutionType.STANDARD, null, null, 0.0);
			}


	/**
	 * create a microwave component with the given reflection inbound port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code microwaveInboundPortURI != null}
	 * pre	{@code !microwaveInboundPortURI.isEmpty()}
	 * pre	{@code reflectionInboundPortURI != null}
	 * post	{@code getState() == MicrowaveState.OFF}
	 * post	{@code getMode() == MicrowaveMode.LOW}
	 * </pre>
	 *
	 * @param microwaveInboundPortURI	URI of the microwave inbound port.
	 * @param reflectionInboundPortURI	URI of the reflection innbound port of the component.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			Microwave(
			String reflectionInboundPortURI,
			String microwaveInboundPortURI,
			ExecutionType currentExecutionType,
			String simArchitectureURI,
			String localSimulatorURI,
			double accFactor
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 0);

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

			if (this.currentExecutionType.isIntegrationTest()) {
				Microwave.VERBOSE = true;
			}

			this.initialise(microwaveInboundPortURI);
		}

	// -------------------------------------------------------------------------
	// Initialize method
	// -------------------------------------------------------------------------

	/**
	 * initialize the microwave component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code microwaveInboundPortURI != null}
	 * pre	{@code !microwaveInboundPortURI.isEmpty()}
	 * post	{@code getState() == MicrowaveState.OFF}
	 * post	{@code getMode() == MicrowaveMode.LOW}
	 * </pre>
	 * 
	 * @param microwaveInboundPortURI	URI of the microwave inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		initialise(String microwaveInboundPortURI)
	throws Exception
	{
		assert	microwaveInboundPortURI != null :
					new PreconditionException(
										"microwaveInboundPortURI != null");
		assert	!microwaveInboundPortURI.isEmpty() :
					new PreconditionException(
										"!microwaveInboundPortURI.isEmpty()");
		this.currentState = INITIAL_STATE;
				this.currentMode = INITIAL_MODE;
				this.mwip = new MicrowaveInboundPort(microwaveInboundPortURI, this);
				this.mwip.publishPort();
				
		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			Architecture architecture =
				MILSimulationArchitectures.createMicrowaveMILArchitecture();
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
							createMicrowaveRTArchitecture(
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
		case INTEGRATION_TEST:
		default:
		}
		

		if (Microwave.VERBOSE) {
			this.tracer.get().setTitle("Microwave component");
			this.tracer.get().setRelativePosition(1, 0);
			this.toggleTracing();
		}
	}
	
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
			case INTEGRATION_TEST:
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
			this.mwip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
	
	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	
	@Override
	public MicrowaveState getState() throws Exception {
		
		if (Microwave.VERBOSE) {
			this.traceMessage("Le micro-ondes renvoie son état : " +
													this.currentState + ".\n");

		}

		return this.currentState;
	}
	
	@Override
	public MicrowaveMode getMode() throws Exception {
		if (Microwave.VERBOSE) {
			this.traceMessage("Le micro-ondes renvoie son mode : " +
													this.currentMode + ".\n");
		}
		return this.currentMode;
	}

	@Override
	public void turnOn() throws Exception {
		if (Microwave.VERBOSE) {
			this.traceMessage("Le micro-ondes est allumé.\n");
		}

		assert	this.getState() == MicrowaveState.OFF :
				new PreconditionException("getState() == MicrowaveState.OFF");

		this.currentState = MicrowaveState.ON;
		this.currentMode = MicrowaveMode.LOW;
		
		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												MicrowaveStateModel.SIL_URI,
												t -> new SwitchOnMicrowave(t));
		}
	}

	@Override
	public void turnOff() throws Exception {
		if (Microwave.VERBOSE) {
			this.traceMessage("Le micro-ondes est éteint.\n");
		}

		assert	this.getState() == MicrowaveState.ON :
				new PreconditionException("getState() == MicrowaveState.ON");

		this.currentState = MicrowaveState.OFF;
		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												MicrowaveStateModel.SIL_URI,
												t -> new SwitchOffMicrowave(t));
		}
	}

	@Override
	public void setTimer(Timer newTimer) throws Exception {

	}

	@Override
	public void setHigh() throws Exception {
		if (Microwave.VERBOSE) {
			this.traceMessage("Le micro-onde est reglé en mode HIGH.\n");
		}

		assert	this.getMode() != MicrowaveMode.HIGH :
				new PreconditionException("getMode() != MicrowaveMode.HIGH");

		this.currentMode = MicrowaveMode.HIGH;		
		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												MicrowaveStateModel.SIL_URI,
												t -> new SetHighMicrowave(t));
		}
	}

	@Override
	public void setMeddium() throws Exception {
		if (Microwave.VERBOSE) {
			this.traceMessage("Le micro-onde est reglé en mode MEDDIUM.\n");
		}

		assert	this.getMode() != MicrowaveMode.MEDDIUM :
				new PreconditionException("getMode() != MicrowaveMode.MEDDIUM");

		this.currentMode = MicrowaveMode.MEDDIUM;
		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												MicrowaveStateModel.SIL_URI,
												t -> new SetMediumMicrowave(t));
		}
	}

	@Override
	public void setLow() throws Exception {
		if (Microwave.VERBOSE) {
			this.traceMessage("Le micro-onde est reglé en mode LOW.\n");
		}

		assert	this.getMode() != MicrowaveMode.LOW :
				new PreconditionException("getMode() != MicrowaveMode.LOW");

		this.currentMode = MicrowaveMode.LOW;
		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												MicrowaveStateModel.SIL_URI,
												t -> new SetLowMicrowave(t));
		}
	}

	@Override
	public void setUnfreeze() throws Exception {
		if (Microwave.VERBOSE) {
			this.traceMessage("Le micro-onde est reglé en mode UNFREEZE.\n");
		}

		assert	this.getMode() != MicrowaveMode.UNFREEZE :
				new PreconditionException("getMode() != MicrowaveMode.UNFREEZE");

		this.currentMode = MicrowaveMode.UNFREEZE;	
		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												MicrowaveStateModel.SIL_URI,
												t -> new SetUnfreezeMicrowave(t));
		}
	}

}
//-------------------------------------------------------------------------------
