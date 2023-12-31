/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.microwave;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * @author Yukhoi
 *
 */
public class Microwave
extends AbstractComponent
implements MicrowaveImplementationI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the microwave inbound port used in tests.					*/
	public static final String			INBOUND_PORT_URI =
												"MICROWAVE-INBOUND-PORT-URI";
	
	/** when true, methods trace their actions.								*/
	public static final boolean				VERBOSE = true;
	public static final MicrowaveState		INITIAL_STATE = MicrowaveState.OFF;
	public static final MicrowaveMode		INITIAL_MODE = MicrowaveMode.LOW;

	/** current state (on, off) of the microwave.							*/
	protected MicrowaveState		currentState;
	/** current mode of operation (low, high) of the microwave.			*/
	protected MicrowaveMode			currentMode;

	/** inbound port offering the <code>MicrowaveCI</code> interface.		*/
	protected MicrowaveInboundPort	mwip;

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
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected Microwave()
	throws Exception
	{
		super(1, 0);
		this.initialise(INBOUND_PORT_URI);
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
	protected Microwave(String microwaveInboundPortURI)
	throws Exception
	{
		super(1, 0);
		this.initialise(microwaveInboundPortURI);
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
	protected Microwave(
		String microwaveInboundPortURI,
		String reflectionInboundPortURI) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
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

		if (Microwave.VERBOSE) {
			this.tracer.get().setTitle("Microwave component");
			this.tracer.get().setRelativePosition(1, 0);
			this.toggleTracing();
		}
	}
	
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
	}

	@Override
	public void turnOff() throws Exception {
		if (Microwave.VERBOSE) {
			this.traceMessage("Le micro-ondes est éteint.\n");
		}

		assert	this.getState() == MicrowaveState.ON :
				new PreconditionException("getState() == MicrowaveState.ON");

		this.currentState = MicrowaveState.OFF;
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
	}

	@Override
	public void setMeddium() throws Exception {
		if (Microwave.VERBOSE) {
			this.traceMessage("Le micro-onde est reglé en mode MEDDIUM.\n");
		}

		assert	this.getMode() != MicrowaveMode.MEDDIUM :
				new PreconditionException("getMode() != MicrowaveMode.HIGH");

		this.currentMode = MicrowaveMode.MEDDIUM;			
	}

	@Override
	public void setLow() throws Exception {
		if (Microwave.VERBOSE) {
			this.traceMessage("Le micro-onde est reglé en mode LOW.\n");
		}

		assert	this.getMode() != MicrowaveMode.LOW :
				new PreconditionException("getMode() != MicrowaveMode.HIGH");

		this.currentMode = MicrowaveMode.LOW;			
	}

	@Override
	public void setUnfreez() throws Exception {
		if (Microwave.VERBOSE) {
			this.traceMessage("Le micro-onde est reglé en mode UNFREEZE.\n");
		}

		assert	this.getMode() != MicrowaveMode.UNFREEZE :
				new PreconditionException("getMode() != MicrowaveMode.HIGH");

		this.currentMode = MicrowaveMode.UNFREEZE;			
	}

}
//-------------------------------------------------------------------------------
