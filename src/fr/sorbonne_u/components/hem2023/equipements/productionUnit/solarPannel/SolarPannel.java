/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
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
import fr.sorbonne_u.components.hem2023.utils.ExecutionType;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil.MILSimulationArchitectures;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil.SolarPannelStateModel;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil.events.SwitchOffSolarPannel;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.mil.events.SwitchOnSolarPannel;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
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
	
	/** Maximum solar intensity (lux) before to turn off the device to avoid to break it  */
	public final static double MAX_SOLAR_INTENSITY = 5000.0;
	
	public static final String			REFLECTION_INBOUND_PORT_URI =
			"SOLAR-PANNEL-RIP-URI";
	/** URI of the solar pannel inbound port used in tests.					*/
	public static final String INBOUND_PORT_URI =
									"SOLAR-PANNEL-INBOUND-PORT-URI";
		
	/** when true, methods trace their actions.								*/
	public static boolean	VERBOSE = true;
	
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
	
	/**current power producted by the solar pannel*/
	protected double CurrentPowerProducted; 
	
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
		this(INBOUND_PORT_URI);

	}
	
	protected SolarPannel(String SolarPannelInboundPortURI,
			String transferEnergyOutboundPortURI,
			String productionOutboudPortURI)
		throws Exception {
		
		this(REFLECTION_INBOUND_PORT_URI, SolarPannelInboundPortURI,
				 ExecutionType.STANDARD, null, null, 0.0, transferEnergyOutboundPortURI,
					 productionOutboudPortURI);
		}
	
	protected SolarPannel(String SolarPannelInboundPortURI)
		throws Exception {
		
		this(REFLECTION_INBOUND_PORT_URI, SolarPannelInboundPortURI,
				 ExecutionType.STANDARD, null, null, 0.0);
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
	protected			SolarPannel(
			String reflectionInboundPortURI,
			String solarPannelInboundPortURI,
			ExecutionType currentExecutionType,
			String simArchitectureURI,
			String localSimulatorURI,
			double accFactor,
			String transferEnergyOutboundPortURI,
			String productionOutboudPortURI
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 0);

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

			if (this.currentExecutionType.isIntegrationTest()) {
				SolarPannel.VERBOSE = true;
			}

			this.initialise(solarPannelInboundPortURI, transferEnergyOutboundPortURI,
					 productionOutboudPortURI);
		}
	
	protected			SolarPannel(
			String reflectionInboundPortURI,
			String solarPannelInboundPortURI,
			ExecutionType currentExecutionType,
			String simArchitectureURI,
			String localSimulatorURI,
			double accFactor
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 0);

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

			if (this.currentExecutionType.isIntegrationTest()) {
				SolarPannel.VERBOSE = true;
			}

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
	protected void initialise(String solarPannelInboundPortURI,
							String transferEnergyOutboundPortURI,
							String productionOutboudPortURI)
	throws Exception
	{
		assert	solarPannelInboundPortURI != null :
			new PreconditionException("hairDryerInboundPortURI != null");
	assert	!solarPannelInboundPortURI.isEmpty() :
			new PreconditionException(
					"!hairDryerInboundPortURI.isEmpty()");

	this.currentState = INITIAL_STATE;
	this.spip = new SolarPannelInboundPort(solarPannelInboundPortURI, this);
	this.spip.publishPort();

	switch (this.currentExecutionType) {
//	case MIL_SIMULATION:
//		Architecture architecture =
//			MILSimulationArchitectures.createSolarPannelMILArchitecture();
//		assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
//				new AssertionError(
//						"local simulator " + this.localSimulatorURI
//						+ " does not exist!");
//		this.addLocalSimulatorArchitecture(architecture);
//		this.architecturesURIs2localSimulatorURIS.
//					put(this.simArchitectureURI, this.localSimulatorURI);
//		break;
	case MIL_RT_SIMULATION:
//	case SIL_SIMULATION:
//		architecture =
//			MILSimulationArchitectures.
//						createHairDryerRTArchitecture(
//								this.currentExecutionType,
//								this.accFactor);
//		assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
//				new AssertionError(
//						"local simulator " + this.localSimulatorURI
//						+ " does not exist!");
//		this.addLocalSimulatorArchitecture(architecture);
//		this.architecturesURIs2localSimulatorURIS.
//				put(this.simArchitectureURI, this.localSimulatorURI);
//		break;
	case STANDARD:
	case INTEGRATION_TEST:
	default:
	}

	if (SolarPannel.VERBOSE) {
		this.tracer.get().setTitle("Solar Pannel component");
		this.tracer.get().setRelativePosition(1, 1);
		this.toggleTracing();
	}
	
		this.consomationEquimentOutboundPort = new ConsomationEquimentOutboundPort(
												transferEnergyOutboundPortURI, this);
		this.consomationEquimentOutboundPort.publishPort();
		
		
		this.productionUnitProductionOutboundPort = new ProductionUnitProductionOutboundPort(
													productionOutboudPortURI, this);
		this.productionUnitProductionOutboundPort.publishPort();
	}
	
	protected void initialise(String solarPannelInboundPortURI)
	throws Exception
	{
		assert	solarPannelInboundPortURI != null :
		new PreconditionException("hairDryerInboundPortURI != null");
		assert	!solarPannelInboundPortURI.isEmpty() :
		new PreconditionException(
			"!hairDryerInboundPortURI.isEmpty()");
		
		this.currentState = INITIAL_STATE;
		this.spip = new SolarPannelInboundPort(solarPannelInboundPortURI, this);
		this.spip.publishPort();
		
		switch (this.currentExecutionType) {
//			case MIL_SIMULATION:
//			Architecture architecture =
//			MILSimulationArchitectures.createSolarPannelMILArchitecture();
//			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
//			new AssertionError(
//					"local simulator " + this.localSimulatorURI
//					+ " does not exist!");
//			this.addLocalSimulatorArchitecture(architecture);
//			this.architecturesURIs2localSimulatorURIS.
//				put(this.simArchitectureURI, this.localSimulatorURI);
//			break;
			case MIL_RT_SIMULATION:
//			case SIL_SIMULATION:
//			architecture =
//			MILSimulationArchitectures.
//					createHairDryerRTArchitecture(
//							this.currentExecutionType,
//							this.accFactor);
//			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
//			new AssertionError(
//					"local simulator " + this.localSimulatorURI
//					+ " does not exist!");
//			this.addLocalSimulatorArchitecture(architecture);
//			this.architecturesURIs2localSimulatorURIS.
//			put(this.simArchitectureURI, this.localSimulatorURI);
//			break;
			case STANDARD:
			case INTEGRATION_TEST:
			default:
		}
		
		if (SolarPannel.VERBOSE) {
		this.tracer.get().setTitle("Solar Pannel component");
		this.tracer.get().setRelativePosition(1, 1);
		this.toggleTracing();
		}
		
	}
	
	
	
	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------
	
	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();		

		try {
			switch (this.currentExecutionType) {
//			case MIL_SIMULATION:
//				this.asp = new AtomicSimulatorPlugin();
//				String uri = this.architecturesURIs2localSimulatorURIS.
//												get(this.simArchitectureURI);
//				Architecture architecture =
//					(Architecture) this.localSimulatorsArchitectures.get(uri);
//				this.asp.setPluginURI(uri);
//				this.asp.setSimulationArchitecture(architecture);
//				this.installPlugin(this.asp);
//				break;
			case MIL_RT_SIMULATION:
//			case SIL_SIMULATION:
//				this.asp = new RTAtomicSimulatorPlugin();
//				uri = this.architecturesURIs2localSimulatorURIS.
//												get(this.simArchitectureURI);
//				architecture =
//						(Architecture) this.localSimulatorsArchitectures.get(uri);
//				((RTAtomicSimulatorPlugin)this.asp).setPluginURI(uri);
//				((RTAtomicSimulatorPlugin)this.asp).
//										setSimulationArchitecture(architecture);
//				this.installPlugin(this.asp);
//				break;
			case STANDARD:
			case INTEGRATION_TEST:
			default:
			}		
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
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
		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												SolarPannelStateModel.SIL_URI,
												t -> new SwitchOnSolarPannel(t));
		}
	}

	@Override
	public void turnOff() throws Exception {
		if (SolarPannel.VERBOSE) {
			this.traceMessage("Solar Pannel is turned off.\n");
		}

		assert	this.getState() == SolarPannelState.ON :
				new PreconditionException("getState() == SolarPannelState.ON\n");

		this.currentState = SolarPannelState.OFF;
		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												SolarPannelStateModel.SIL_URI,
												t -> new SwitchOffSolarPannel(t));
		}
	}

	public double getBattery() throws Exception {
		return this.currentBattery;
	}

	public boolean addBattery(double quantity) throws Exception {
//		assert this.currentState == SolarPannelState.ON;
		
		
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
//		assert this.currentState == SolarPannelState.ON;
		
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
