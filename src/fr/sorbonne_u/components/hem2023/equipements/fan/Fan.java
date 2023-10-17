/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * @author Yukhoi
 *
 */
public class Fan 
extends AbstractComponent 
implements FanImplementationI {
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the fan inbound port used in tests.					*/
	public static final String			INBOUND_PORT_URI =
												"FAN-INBOUND-PORT-URI";
	/** when true, methods trace their actions.								*/
	public static final boolean			VERBOSE = true;
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

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a hair dryer component.
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
		super(1, 0);
		this.initialise(INBOUND_PORT_URI);
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
		super(1, 0);
		this.initialise(fanInboundPortURI);
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
	protected Fan(
		String fanInboundPortURI,
		String reflectionInboundPortURI) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
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

		if (Fan.VERBOSE) {
			this.tracer.get().setTitle("Fan component");
			this.tracer.get().setRelativePosition(1, 0);
			this.toggleTracing();
		}
	}
	
	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
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


	@Override
	public FanState getState() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its state : " +
													this.currentState + ".\n");
		}
		return this.currentState;
	}

	@Override
	public FanMode getMode() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its mode : " +
													this.currentMode + ".\n");
		}

		return this.currentMode;
	}

	@Override
	public FanMusic getMusicState() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its music mode : " +
													this.currentMusicState + ".\n");
		}

		return this.currentMusicState;
	}
	
	@Override
	public void turnOn() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan is turned on.\n");
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
			this.traceMessage("Fan is turned off.\n");
		}
		
		assert	this.getState() == FanState.ON :
				new PreconditionException("getState() == FanState.ON");
		
		this.currentState = FanState.OFF;
	}

	@Override
	public void turnOnMusic() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan's music is turned on.\n");
		}
		
		assert	this.getMusicState() == FanMusic.OFF :
				new PreconditionException("getMusicState() == FanMusic.OFF");
		
		this.currentMusicState = FanMusic.ON;
	}

	@Override
	public void turnOffMusic() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan's music is turned off.\n");
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
			this.traceMessage("Fan is set high.\n");
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
			this.traceMessage("Fan is set low.\n");
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
			this.traceMessage("Fan is set low.\n");
		}

		assert	this.getState() == FanState.ON :
				new PreconditionException("getState() == FanState.ON");
		assert	this.getMode() != FanMode.MEDDIUM :
				new PreconditionException("getMode() != FanMode.MEDDIUM");

		this.currentMode = FanMode.MEDDIUM;
	}

}
//-----------------------------------------------------------------------------

