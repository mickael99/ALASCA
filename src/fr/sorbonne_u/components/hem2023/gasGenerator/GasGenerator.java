/**
 * 
 */
package fr.sorbonne_u.components.hem2023.gasGenerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * @author Yukhoi
 *
 */
public class GasGenerator extends AbstractComponent implements GasGeneratorUserCI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the solar pannel inbound port used in tests.					*/
	public static final String 				INBOUND_PORT_URI =
												"GAS-GENERATOR-INBOUND-PORT-URI";
	/** when true, methods trace their actions.								*/
	public static final boolean				VERBOSE = true;
	
	public static final GasGeneratorState 	INITIAL_STATE = GasGeneratorState.OFF;
	public static final GasGeneretorMode	INITIAL_MODE = GasGeneretorMode.LOW;
	
	/** current state (on, off) of the solar pannel.							*/
	protected GasGeneratorState				currentState;
	/** current mode (high, low, meddium) of the solar pannel.							*/
	protected GasGeneretorMode				currentMode;
	/** Current solar panel power storage 		*/
	protected int 							currentBattery;
	/** Maximum solar panel power storage 		*/
	protected int 							maxBattery;

	/** inbound port offering the <code>SolarPannelUserCI</code> interface.		*/
	protected GasGeneratorInboundPort		spip;
	
	

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	/**
	 * create a solar pannel component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code INBOUND_PORT_URI != null}
	 * pre	{@code !INBOUND_PORT_URI.isEmpty()}
	 * post	{@code getState() == GasGeneratorState.OFF}
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected GasGenerator()
	throws Exception
	{
		super(1, 0);
		this.initialise(INBOUND_PORT_URI);
	}
	
	/**
	 * create a solar pannel component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code gasGeneratorInboundPortURI != null}
	 * pre	{@code !gasGeneratorInboundPortURI.isEmpty()}
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 * 
	 * @param gasGeneratorInboundPortURI	URI of the solar pannel inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected GasGenerator(String gasGeneratorInboundPortURI)
	throws Exception
	{
		super(1, 0);
		this.initialise(gasGeneratorInboundPortURI);
	}

	/**
	 * create a fan component with the given reflection inbound port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code gasGeneratorInboundPortURI != null}
	 * pre	{@code !gasGeneratorInboundPortURI.isEmpty()}
	 * pre	{@code reflectionInboundPortURI != null}
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 *
	 * @param gasGeneratorInboundPortURI	URI of the solar pannel inbound port.
	 * @param reflectionInboundPortURI	URI of the reflection innbound port of the component.
	 * @throws Exception				<i>to do</i>.
	 */
	protected GasGenerator(
		String gasGeneratorInboundPortURI,
		String reflectionInboundPortURI) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(gasGeneratorInboundPortURI);
	}
	
	// -------------------------------------------------------------------------
	// Initialize method
	// -------------------------------------------------------------------------
	/**
	 * initialise the solar pannel component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code gasGeneratorInboundPortURI != null}
	 * pre	{@code !gasGeneratorInboundPortURI.isEmpty()}
	 * post	{@code getState() == FanState.OFF}
	 * </pre>
	 * 
	 * @param gasGeneratorInboundPortURI	URI of the fan inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void initialise(String gasGeneratorInboundPortURI)
	throws Exception
	{
		assert	gasGeneratorInboundPortURI != null :
					new PreconditionException(
										"gasGeneratorInboundPortURI != null");
		assert	!gasGeneratorInboundPortURI.isEmpty() :
					new PreconditionException(
										"!gasGeneratorInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.currentBattery = 0;
		this.spip = new GasGeneratorInboundPort(gasGeneratorInboundPortURI, this);
		this.spip.publishPort();

		if (GasGenerator.VERBOSE) {
			this.tracer.get().setTitle("Solar Pannel component");
			this.tracer.get().setRelativePosition(1, 0);
			this.toggleTracing();
		}
	}
	
	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.spip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component Methods
	// -------------------------------------------------------------------------
	
	@Override
	public GasGeneratorState getState() throws Exception {
		if (GasGenerator.VERBOSE) {
			this.traceMessage("Gas Generator returns its state : " +
													this.currentState + ".\n");
		}
		return this.currentState;
	}

	@Override
	public GasGeneretorMode getMode() throws Exception {
		if (GasGenerator.VERBOSE) {
			this.traceMessage("Gas Generator returns its mode : " +
													this.currentMode + ".\n");
		}
		return this.currentMode;
	}

	@Override
	public void turnOn() throws Exception {
		if (GasGenerator.VERBOSE) {
			this.traceMessage("Gas Generator is turned on.\n");
		}

		assert	this.getState() == GasGeneratorState.OFF :
				new PreconditionException("getState() == GasGeneratorState.OFF");

		this.currentState = GasGeneratorState.ON;
	}

	@Override
	public void turnOff() throws Exception {
		if (GasGenerator.VERBOSE) {
			this.traceMessage("Gas Generator is turned off.\n");
		}

		assert	this.getState() == GasGeneratorState.ON :
				new PreconditionException("getState() == GasGeneratorState.ON");

		this.currentState = GasGeneratorState.OFF;
	}

	@Override
	public int getBattery() throws Exception {
		if (GasGenerator.VERBOSE) {
			this.traceMessage("Gas Generator returns its current power .\n");
		}
		return this.currentBattery;
	}

	@Override
	public void setHigh() throws Exception {
		if (GasGenerator.VERBOSE) {
			this.traceMessage("Gas Generator is set high.\n");
		}

		assert	this.getState() == GasGeneratorState.ON :
				new PreconditionException("getState() == GasGeneratorState.ON");
		assert	this.getMode() != GasGeneretorMode.HIGH :
				new PreconditionException("getMode() != GasGeneretorMode.HIGH");

		this.currentMode = GasGeneretorMode.HIGH;
	}

	@Override
	public void setLow() throws Exception {
		if (GasGenerator.VERBOSE) {
			this.traceMessage("Gas Generator is set low.\n");
		}

		assert	this.getState() == GasGeneratorState.ON :
				new PreconditionException("getState() == GasGeneratorState.ON");
		assert	this.getMode() != GasGeneretorMode.LOW :
				new PreconditionException("getMode() != GasGeneretorMode.LOW");

		this.currentMode = GasGeneretorMode.LOW;
	}

	@Override
	public void setMeddium() throws Exception {
		if (GasGenerator.VERBOSE) {
			this.traceMessage("Gas Generator is set meddium.\n");
		}

		assert	this.getState() == GasGeneratorState.ON :
				new PreconditionException("getState() == GasGeneratorState.ON");
		assert	this.getMode() != GasGeneretorMode.MEDDIUM :
				new PreconditionException("getMode() != GasGeneretorMode.MEDDIUM");

		this.currentMode = GasGeneretorMode.MEDDIUM;
	}

}
