/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.equipements.battery.Battery;
import fr.sorbonne_u.components.hem2023.equipements.consomation.ports.ConsomationEquimentOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2023.equipements.production.connectors.ProductionEquipmentConnector;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.connectors.productionElectricMeterConnector;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.ports.ProductionUnitProductionOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.interfaces.SolarPannelImplementationI;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.ports.SolarPannelInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * @author Yukhoi
 *
 */
public class SolarPannel extends AbstractComponent implements SolarPannelImplementationI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** Solar intensity in W/m^2 compute with a sensor.								*/
	public static double solarIntensity = 600;
	/** Maximum solar intensity before to turn off the device to avoid to break it  */
	public final static double MAX_SOLAR_INTENSITY = 1500.0;
	
	/** URI of the solar pannel inbound port used in tests.					*/
	public static final String INBOUND_PORT_URI =
									"SOLAR-PANNEL-INBOUND-PORT-URI";	
	
	/** when true, methods trace their actions.								*/
	public static final boolean	VERBOSE = true;
	
	public static final SolarPannelState INITIAL_STATE = SolarPannelState.OFF;
		
	/** current state (on, off) of the solar pannel.							*/
	protected SolarPannelState currentState;
	/** Current solar panel power storage 		*/
	protected double currentBattery;
	/** Maximum solar panel power storage 		*/
	protected final double MAX_BATTERY = 1000.0;
	/** Initial energy 							*/
	protected final double INIT_BATTERY = 1000.0;

	/** inbound port offering the <code>SolarPannelUserCI</code> interface.		*/
	protected SolarPannelInboundPort		spip;
	
	/** 
	 * outbound port offerunt the <code>EnergyTransferCI</code> interface for sending 
	 * energy to the battery
	 */
	protected ConsomationEquimentOutboundPort consomationEquimentOutboundPort;
	
	/** outbound port offert by the <code>SolarPannelProductionCI</code> interface for
	 *  sending the device's quantity production
	 */
	protected ProductionUnitProductionOutboundPort productionUnitProductionOutboundPort;
	
	protected boolean isUnitTest = false;
	

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
	
	protected SolarPannel(boolean isUnitTest)
		throws Exception {
		
			super(1, 0);
			this.isUnitTest = isUnitTest;
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
	throws Exception {
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
		String reflectionInboundPortURI) throws Exception {
			super(reflectionInboundPortURI, 1, 0);
			this.initialise(solarPannelInboundPortURI);
	}
	
	protected SolarPannel(String solarPannelInboundPortURI,
			String reflectionInboundPortURI,
			String transferEnergyOutboundPortURI,
			String productionOutboudPortURI) throws Exception {
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(solarPannelInboundPortURI, transferEnergyOutboundPortURI,
				productionOutboudPortURI);
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
		this.currentBattery =INIT_BATTERY;
		this.spip = new SolarPannelInboundPort(solarPannelInboundPortURI, this);
		this.spip.publishPort();

		if (SolarPannel.VERBOSE) {
			this.tracer.get().setTitle("Solar Pannel component");
			this.tracer.get().setRelativePosition(1, 0);
			this.toggleTracing();
		}
	}
	
	protected void initialise(String solarPannelInboundPotURI, 
			String transferEnergyOutboundPortURI,
			String productionOutboudPortURI) throws Exception {
		initialise(solarPannelInboundPotURI);
		
		assert	transferEnergyOutboundPortURI != null :
			new PreconditionException(
								"transferEnergyOutboundPortURI != null");
		assert	!transferEnergyOutboundPortURI.isEmpty() :
			new PreconditionException(
								"!transferEnergyOutboundPortURI.isEmpty()"); 
		
		assert	productionOutboudPortURI != null :
			new PreconditionException(
						"transferEnergyOutboundPortURI != null");
		assert	!productionOutboudPortURI.isEmpty() :
			new PreconditionException(
						"!transferEnergyOutboundPortURI.isEmpty()"); 
		
		this.consomationEquimentOutboundPort = new ConsomationEquimentOutboundPort(
												transferEnergyOutboundPortURI, this);
		this.consomationEquimentOutboundPort.publishPort();
		
		
		this.productionUnitProductionOutboundPort = new ProductionUnitProductionOutboundPort(
													productionOutboudPortURI, this);
		this.productionUnitProductionOutboundPort.publishPort();
	}
	
	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------
	
	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();		
		try {
			if(VERBOSE)
				this.traceMessage("connexion des ports du panneau solaire\n\n");
			
			if(!isUnitTest) {
				this.doPortConnection(this.consomationEquimentOutboundPort.getPortURI(), 
						Battery.URI_PRODUCTION, 
						ProductionEquipmentConnector.class.getCanonicalName());
				
				this.doPortConnection(this.productionUnitProductionOutboundPort.getPortURI(), 
						ElectricMeter.PRODUCTION_URI, 
						productionElectricMeterConnector.class.getCanonicalName());
				
				if(sendBattery(1000.0))
					productionToElectricMeter(1000.0);
			}
			
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}
	
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		
		if(VERBOSE)
			this.traceMessage("debut des tests\n\n");		
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		if(VERBOSE)
			this.traceMessage("déconnexion des liaisons entre les ports\n\n");
		
		if(!isUnitTest) {
			this.doPortDisconnection(this.consomationEquimentOutboundPort.getPortURI());
			this.doPortDisconnection(this.productionUnitProductionOutboundPort.getPortURI());
		}
		
		super.finalise();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.spip.unpublishPort();
			
			if(!isUnitTest) {
				this.consomationEquimentOutboundPort.unpublishPort();
				this.productionUnitProductionOutboundPort.unpublishPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
	
	// -------------------------------------------------------------------------
	// Component Methods
	// -------------------------------------------------------------------------
	
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
				new PreconditionException("getState() == SolarPannelState.OFF\n");

		this.currentState = SolarPannelState.ON;
	}

	@Override
	public void turnOff() throws Exception {
		if (SolarPannel.VERBOSE) {
			this.traceMessage("Solar Pannel is turned off.\n");
		}

		assert	this.getState() == SolarPannelState.ON :
				new PreconditionException("getState() == SolarPannelState.ON\n");

		this.currentState = SolarPannelState.OFF;
	}

	public double getBattery() throws Exception {
		return this.currentBattery;
	}

	public boolean addBattery(double quantity) throws Exception {
		assert this.currentState == SolarPannelState.ON;
		
		
		if(this.currentBattery + quantity > this.MAX_BATTERY) {
			if(VERBOSE)
				this.traceMessage("Solar pannel cannot get energy\n");
			return false;
		}

		if(VERBOSE)
			this.traceMessage("Solar pannel get " + quantity + " watts of energy\n");
		
		this.currentBattery += quantity;
		return true;
	}

	/**
	 * have to complete
	 */
	public boolean sendBattery(double quantity) throws Exception {
		assert this.currentState == SolarPannelState.ON;
		
		if(this.currentBattery - quantity < 0) {
			if(VERBOSE)
				this.traceMessage("Solar pannel cannot send energy to the battery\n");
			return false;
		}
		
		if(VERBOSE)
			this.traceMessage("Solar pannel try to give energy to the battery\n");
		this.currentBattery -= quantity;		
		
		return this.consomationEquimentOutboundPort.sendBattery(quantity);
	}
	
	public void turnOffIfSolarIntensityIsToHigh() throws Exception {
		if(SolarPannel.solarIntensity > SolarPannel.MAX_SOLAR_INTENSITY) {
			if(VERBOSE)
				this.traceMessage("Solar pannel needs to turn off because "
									+ "solar intensity is too high\n");
			this.turnOff();
		}
		
		if(VERBOSE)
			this.traceMessage("Solar pannel doesn't need to turn off\n");
	}

	@Override
	public void productionToElectricMeter(double quantity) throws Exception {
		if(VERBOSE)
			this.traceMessage("Warn electric meter about production\n");
		
		this.productionUnitProductionOutboundPort.productionToElectricMeter(quantity);
	}
}
