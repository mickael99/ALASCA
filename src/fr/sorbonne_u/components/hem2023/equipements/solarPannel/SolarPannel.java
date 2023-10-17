/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.solarPannel;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * @author Yukhoi
 *
 */
public class SolarPannel extends AbstractComponent implements SolarPannelUserCI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the solar pannel inbound port used in tests.					*/
	public static final String 				INBOUND_PORT_URI =
												"SOLAR-PANNEL-INBOUND-PORT-URI";
	/** when true, methods trace their actions.								*/
	public static final boolean				VERBOSE = true;
	
	public static final SolarPannelState 	INITIAL_STATE = SolarPannelState.OFF;
	
	/** current state (on, off) of the solar pannel.							*/
	protected SolarPannelState				currentState;
	/** Current solar panel power storage 		*/
	protected int 							currentBattery;
	/** Maximum solar panel power storage 		*/
	protected int 							maxBattery;

	/** inbound port offering the <code>SolarPannelUserCI</code> interface.		*/
	protected SolarPannelInboundPort		spip;
	
	

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
	 * post	{@code getState() == FanState.OFF}
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected SolarPannel()
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
	 * pre	{@code solarPannelInboundPortURI != null}
	 * pre	{@code !solarPannelInboundPortURI.isEmpty()}
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 * 
	 * @param solarPannelInboundPortURI	URI of the solar pannel inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected SolarPannel(String solarPannelInboundPortURI)
	throws Exception
	{
		super(1, 0);
		this.initialise(solarPannelInboundPortURI);
	}

	/**
	 * create a fan component with the given reflection inbound port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code solarPannelInboundPortURI != null}
	 * pre	{@code !solarPannelInboundPortURI.isEmpty()}
	 * pre	{@code reflectionInboundPortURI != null}
	 * post	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 *
	 * @param solarPannelInboundPortURI	URI of the solar pannel inbound port.
	 * @param reflectionInboundPortURI	URI of the reflection innbound port of the component.
	 * @throws Exception				<i>to do</i>.
	 */
	protected SolarPannel(
		String solarPannelInboundPortURI,
		String reflectionInboundPortURI) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(solarPannelInboundPortURI);
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
	 * pre	{@code solarPannelInboundPortURI != null}
	 * pre	{@code !solarPannelInboundPortURI.isEmpty()}
	 * post	{@code getState() == FanState.OFF}
	 * </pre>
	 * 
	 * @param solarPannelInboundPortURI	URI of the fan inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void initialise(String solarPannelInboundPortURI)
	throws Exception
	{
		assert	solarPannelInboundPortURI != null :
					new PreconditionException(
										"solarPannelInboundPortURI != null");
		assert	!solarPannelInboundPortURI.isEmpty() :
					new PreconditionException(
										"!solarPannelInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentBattery = 0;
		this.spip = new SolarPannelInboundPort(solarPannelInboundPortURI, this);
		this.spip.publishPort();

		if (SolarPannel.VERBOSE) {
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

	@Override
	public SolarPannelState getState() throws Exception {
		if (SolarPannel.VERBOSE) {
			this.traceMessage("Solar Pannel returns its state : " +
													this.currentState + ".\n");
		}
		return this.currentState;
	}

	@Override
	public void turnOn() throws Exception {
		if (SolarPannel.VERBOSE) {
			this.traceMessage("Solar Pannel is turned on.\n");
		}

		assert	this.getState() == SolarPannelState.OFF :
				new PreconditionException("getState() == SolarPannelState.OFF");

		this.currentState = SolarPannelState.ON;
	}

	@Override
	public void turnOff() throws Exception {
		if (SolarPannel.VERBOSE) {
			this.traceMessage("Solar Pannel is turned off.\n");
		}

		assert	this.getState() == SolarPannelState.ON :
				new PreconditionException("getState() == SolarPannelState.ON");

		this.currentState = SolarPannelState.OFF;
	}

	@Override
	public int getBattery() throws Exception {
		return this.currentBattery;
	}

}
