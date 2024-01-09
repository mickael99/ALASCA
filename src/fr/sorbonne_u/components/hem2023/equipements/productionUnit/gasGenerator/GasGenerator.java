/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.equipements.battery.Battery;
import fr.sorbonne_u.components.hem2023.equipements.consomation.ports.ConsomationEquimentOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2023.equipements.meter.connectors.productionElectricMeterConnector;
import fr.sorbonne_u.components.hem2023.equipements.production.connectors.ProductionEquipmentConnector;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.interfaces.GasGeneratorImplementationI;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.ports.GasGeneratorInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.ports.ProductionUnitProductionOutboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * @author Yukhoi
 *
 */
public class GasGenerator extends AbstractComponent implements GasGeneratorImplementationI {

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
	public static final double INITIAL_CURRENT_BATTERY = 1000.0;
	
	/** current state (on, off) of the solar pannel.							*/
	protected GasGeneratorState				currentState;
	/** current mode (high, low, meddium) of the solar pannel.							*/
	protected GasGeneretorMode				currentMode;
	/** Current solar panel power storage 		*/
	protected double 							currentBattery;
	/** Maximum solar panel power storage 		*/
	protected static final double MAX_BATTERY = 1000.0;

	/** inbound port offering the <code>SolarPannelUserCI</code> interface.		*/
	protected GasGeneratorInboundPort		spip;
	/** 
	 * outbound port offerunt the <code>EnergyTransferCI</code> interface for sending 
	 * energy to the battery
	 */
	protected ConsomationEquimentOutboundPort consomationEquimentOutboundPort;
	
	/** outbound port offert by the <code>SolarPannelProductionCI</code> interface for
	 *  sending the device's quantity production
	 */
	protected ProductionUnitProductionOutboundPort productionUnitProductionOutboundPort;
	

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
		
		if (GasGenerator.VERBOSE) {
			this.tracer.get().setTitle("Gaz generator component");
			this.tracer.get().setRelativePosition(1, 0);
			this.toggleTracing();
		}
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
		
		if (GasGenerator.VERBOSE) {
			this.tracer.get().setTitle("Gaz generator component");
			this.tracer.get().setRelativePosition(5, 0);
			this.toggleTracing();
		}
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
		String reflectionInboundPortURI) throws Exception {
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(gasGeneratorInboundPortURI);
		
		if (GasGenerator.VERBOSE) {
			this.tracer.get().setTitle("Gaz generator component");
			this.tracer.get().setRelativePosition(5, 0);
			this.toggleTracing();
		}
	}
	
	protected GasGenerator(
		String gasGeneratorInboundPortURI,
		String reflectionInboundPortURI,
		String energyTransferOutboundPortURI,
		String productionUnitProductionOutboundPort) throws Exception {
			super(reflectionInboundPortURI, 1, 0);
			this.initialise(gasGeneratorInboundPortURI, energyTransferOutboundPortURI,
					productionUnitProductionOutboundPort);
			
			if (GasGenerator.VERBOSE) {
				this.tracer.get().setTitle("Gaz generator component");
				this.tracer.get().setRelativePosition(5, 0);
				this.toggleTracing();
			}
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
		this.currentBattery = INITIAL_CURRENT_BATTERY;
		this.spip = new GasGeneratorInboundPort(gasGeneratorInboundPortURI, this);
		this.spip.publishPort();
	}
	
	protected void initialise(String gasGeneratorInboundPortURI,
			String energyTransferOutboundPortURI,
			String productionUnitProductionOutboundPortURI)
	throws Exception
	{
		assert	energyTransferOutboundPortURI != null :
			new PreconditionException(
								"energyTransferOutboundPortURI != null");
		assert	!energyTransferOutboundPortURI.isEmpty() :
			new PreconditionException(
								"!energyTransferOutboundPortURI.isEmpty()");
		assert	productionUnitProductionOutboundPortURI != null :
			new PreconditionException(
								"productionUnitProductionOutboundPort != null");
		assert	!productionUnitProductionOutboundPortURI.isEmpty() :
			new PreconditionException(
								"!productionUnitProductionOutboundPort.isEmpty()");
		
		initialise(gasGeneratorInboundPortURI);
		
		this.consomationEquimentOutboundPort = new ConsomationEquimentOutboundPort
				(energyTransferOutboundPortURI, this);
		this.consomationEquimentOutboundPort.publishPort();
		
		this.productionUnitProductionOutboundPort = new ProductionUnitProductionOutboundPort
				(productionUnitProductionOutboundPortURI, this);
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
				this.traceMessage("connexion des ports du generateur de gaz\n\n");
			
			this.doPortConnection(this.productionUnitProductionOutboundPort.getPortURI(), 
					ElectricMeter.PRODUCTION_URI, 
					productionElectricMeterConnector.class.getCanonicalName());
			
			this.doPortConnection(this.consomationEquimentOutboundPort.getPortURI(), 
					Battery.URI_PRODUCTION, 
					ProductionEquipmentConnector.class.getCanonicalName());
			
			if(sendBattery(1000.0))
				productionToElectricMeter(1000.0);
			
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
		this.doPortDisconnection(this.consomationEquimentOutboundPort.getPortURI());
		this.doPortDisconnection(this.productionUnitProductionOutboundPort.getPortURI());
		
		super.finalise();
	}
	
	@Override
	public synchronized void shutdown() 
			throws ComponentShutdownException {
		try {
			this.spip.unpublishPort();
			this.consomationEquimentOutboundPort.unpublishPort();
			this.productionUnitProductionOutboundPort.unpublishPort();
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
	public double getBattery() throws Exception {
		if (GasGenerator.VERBOSE) {
			this.traceMessage("Gas Generator returns its current power .\n");
		}
		return this.currentBattery;
	}
	
	@Override 
	public boolean addBattery(double quantity) throws Exception {
		assert this.currentState == GasGeneratorState.ON;
		
		if(this.currentBattery + quantity > GasGenerator.MAX_BATTERY) {
			if(VERBOSE)
				this.traceMessage("impossible to add battery to the gaz generator\n");
			return false;
		}
		
		if(VERBOSE)
			this.traceMessage("add " + quantity + " watts to the device\n");
		
		this.currentBattery += quantity;
		return true;
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
	
	@Override
	public boolean sendBattery(double quantity) throws Exception {
		assert this.currentState == GasGeneratorState.ON;
		
		if(this.currentBattery - quantity < 0) {
			if(VERBOSE)
				this.traceMessage("Gas generator cannot send energy to the battery\n");
			return false;
		}
		
		if(VERBOSE)
			this.traceMessage("Gas generator try to give energy to the battery\n");
		this.currentBattery -= quantity;	
		
		return this.consomationEquimentOutboundPort.sendBattery(quantity);
	}
	
	@Override
	public void productionToElectricMeter(double quantity) throws Exception {
		if(VERBOSE)
			this.traceMessage("Warn electric meter about production\n");
		
		this.productionUnitProductionOutboundPort.productionToElectricMeter(quantity);
	}
}
