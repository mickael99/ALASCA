package fr.sorbonne_u.components.hem2023.equipements.meter;


import java.util.concurrent.atomic.AtomicReference;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterCI;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2023.equipements.meter.ports.ElectricMeterConsomationInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.meter.ports.ElectricMeterInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.meter.ports.ElectricMeterProductionInboundPort;
import fr.sorbonne_u.components.hem2023.utils.Measure;
import fr.sorbonne_u.components.hem2023.utils.MeasurementUnit;
import fr.sorbonne_u.components.hem2023.utils.SensorData;
import fr.sorbonne_u.components.hem2023.CVMGlobalTest;
import fr.sorbonne_u.components.hem2023.equipements.meter.mil.MILSimulationArchitectures;
import fr.sorbonne_u.components.hem2023.equipments.meter.sil.SILSimulationArchitectures;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.components.hem2023.utils.ExecutionType;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

@OfferedInterfaces(offered={ElectricMeterCI.class})
@RequiredInterfaces(required = {ClocksServerCI.class})
public class ElectricMeter 
	extends		AbstractCyPhyComponent
	implements ElectricMeterImplementationI
{
	public static final Measure<Double>	ELECTRIC_METER_VOLTAGE =
															new Measure<Double>(220.0, MeasurementUnit.VOLTS);
	public static final String	REFLECTION_INBOUND_PORT_URI =
															"ELECTRIC-METER-RIP-URI";
	public static final String	ELECTRIC_METER_INBOUND_PORT_URI =
															"ELECTRIC-METER";
	public static final String PRODUCTION_URI = "PRODUCTION_URI";
	public static final String CONSOMATION_URI = "CONSOMATION_URI";
	
	public static final boolean	VERBOSE = true;

	protected ElectricMeterInboundPort	emip;
	protected ElectricMeterProductionInboundPort electricMeterProductionInboundPort;
	protected ElectricMeterConsomationInboundPort electricMeterConsomationInboundPort;
	
	protected double electricProduction;
	protected double electricConsumption;
	
	/** current total electric power consumption measured at the electric
	 *  meter in amperes.												 	*/
	protected AtomicReference<SensorData<Measure<Double>>>
											currentPowerConsumption;
	/** current total electric power production measured at the electric
	 *  meter in watts.													 	*/
	protected AtomicReference<SensorData<Measure<Double>>>
											currentPowerProduction;
	
	// Execution/Simulation

	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;
	/** accelerated clock, in integration and SIL simulation tests.			*/
	protected AcceleratedClock			clock;
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
	protected			ElectricMeter() throws Exception
	{
		this(ELECTRIC_METER_INBOUND_PORT_URI);
	}
	
	protected			ElectricMeter(
		String electricMeterInboundPortURI
		) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, electricMeterInboundPortURI,
			 ExecutionType.STANDARD, null, null, 0.0);
	}
	
	protected			ElectricMeter(
		String reflectionInboundPortURI,
		String electricMeterInboundPortURI,
		ExecutionType currentExecutionType,
		String simArchitectureURI,
		String localSimulatorURI,
		double accFactor
		) throws Exception
	{
		super(reflectionInboundPortURI, 2, 0);

		assert	electricMeterInboundPortURI != null &&
										!electricMeterInboundPortURI.isEmpty() :
				new PreconditionException(
						"electricMeterInboundPortURI != null && "
						+ "!electricMeterInboundPortURI.isEmpty()");
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

		this.initialise(electricMeterInboundPortURI);
	}
	
	protected void		initialise(String electricMeterInboundPortURI)
	throws Exception
	{
		assert	electricMeterInboundPortURI != null &&
										!electricMeterInboundPortURI.isEmpty() :
				new PreconditionException(
						"electricMeterInboundPortURI != null && "
						+ "!electricMeterInboundPortURI.isEmpty()");

		this.emip =
				new ElectricMeterInboundPort(electricMeterInboundPortURI, this);
		this.emip.publishPort();

		this.currentPowerConsumption =
			new AtomicReference<>(
					new SensorData<>(
							new Measure<Double>(0.0, MeasurementUnit.AMPERES)));
		this.currentPowerProduction =
			new AtomicReference<>(
					new SensorData<>(
							new Measure<Double>(0.0, MeasurementUnit.WATTS)));

		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			Architecture architecture =
					MILSimulationArchitectures.
									createElectricMeterMILArchitecture();
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
						createElectricMeterMILRTArchitecture(this.accFactor);
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
					new AssertionError(
							"local simulator " + this.localSimulatorURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
						put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		case SIL_SIMULATION:
			architecture =
					SILSimulationArchitectures.
						createElectricMeterSILArchitecture(this.accFactor);
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

		if (VERBOSE) {
			this.tracer.get().setTitle("Electric meter component");
			this.tracer.get().setRelativePosition(1, 0);
			this.toggleTracing();
		}
	}
	
	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	/**
	 * set the current power consumption, a method that is meant to be called
	 * only by the simulator in SIL runs, otherwise a hardware sensor would be
	 * used in standard executions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code power != null}
	 * pre	{@code power.getMeasurementUnit().equals(MeasurementUnit.AMPERES)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param power					electric power consumption in amperes.
	 */
	public void			setCurrentPowerConsumption(Measure<Double> power)
	{
		
		assert	power != null : new PreconditionException("power != null");
		assert	power.getMeasurementUnit().equals(MeasurementUnit.AMPERES) :
				new PreconditionException(
						"power.getMeasurementUnit().equals("
						+ "MeasurementUnit.AMPERES)");

		double old;
		double i;

		if (VERBOSE) {
			old = ((Measure<Double>)this.currentPowerConsumption.get().
														getMeasure()).getData();
		}
		SensorData<Measure<Double>> sd = null;
		if (this.currentExecutionType.isSIL()) {
			// in SIL simulation, an accelerated clock is used as time reference
			// for measurements and sensor data time stamps
			sd = new SensorData<>(this.clock, 
								  new Measure<Double>(
										  this.clock,
										  power.getData(),
										  power.getMeasurementUnit()));
		} else {
			sd = new SensorData<>(power);
		}
		this.currentPowerConsumption.set(sd);

		if (VERBOSE) {
			i = power.getData();
			if (Math.abs(old - i) > 0.000001) {
				this.traceMessage(
					"Electric meter sets its current consumption at "
					+ this.currentPowerConsumption.get().getMeasure() + ".\n");
			}
		}
	}
	
	/**
	 * set the current power consumption, a method that is meant to be called
	 * only by the simulator in SIL runs, otherwise a hardware sensor would be
	 * used in standard executions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code power != null}
	 * pre	{@code power.getMeasurementUnit().equals(MeasurementUnit.AMPERES)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param power					electric power production in watts.
	 */
	public void			setCurrentPowerProduction(Measure<Double> power)
	{
		assert	power != null : new PreconditionException("power != null");
		assert	power.getMeasurementUnit().equals(MeasurementUnit.WATTS) :
				new PreconditionException(
						"power.getMeasurementUnit().equals("
						+ "MeasurementUnit.WATTS)");

		SensorData<Measure<Double>> sd = null;
		if (this.currentExecutionType.isSIL()) {
			// in SIL simulation, an accelerated clock is used as time reference
			// for measurements and sensor data time stamps
			sd = new SensorData<>(this.clock,
								  new Measure<Double>(
										  this.clock,
										  power.getData(),
										  power.getMeasurementUnit()));
		} else {
			sd = new SensorData<>(power);
		}
		this.currentPowerProduction.set(sd);

		if (VERBOSE) {
			this.traceMessage(
					"Electric meter sets its current production at "
					+ this.currentPowerProduction.get().getMeasure() + ".\n");
		}
	}




//	protected ElectricMeter() throws Exception {
//		super(1, 0);
//		initialise();
//		
//		if(VERBOSE)
//			this.traceMessage("electric meter ready\n");
//	}
//
//	protected ElectricMeter(String uriId) throws Exception
//	{
//		super(uriId, 1, 0);
//		initialise();
//		
//		if(VERBOSE)
//			this.traceMessage("electric meter ready\n");
//	}
//
//	protected void initialise() throws Exception {
//		this.electricMeterInboundPort =
//				new ElectricMeterInboundPort(ELECTRIC_METER_INBOUND_PORT_URI, this);
//		this.electricMeterInboundPort.publishPort();
//		
//		this.electricMeterProductionInboundPort =
//				new ElectricMeterProductionInboundPort(PRODUCTION_URI, this);
//		this.electricMeterProductionInboundPort.publishPort();
//		
//		this.electricMeterConsomationInboundPort =
//				new ElectricMeterConsomationInboundPort(CONSOMATION_URI, this);
//		this.electricMeterConsomationInboundPort.publishPort();
//		
//		electricProduction = 0.0;
//		electricConsumption = 0.0;
//
//		if (VERBOSE) {
//			this.tracer.get().setTitle("Electric meter component");
//			this.tracer.get().setRelativePosition(2, 1);
//			this.toggleTracing();
//		}
//	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------


	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
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
				rtasp.setPluginURI(architecture.getRootModelURI());
				rtasp.setSimulationArchitecture(architecture);
				this.installPlugin(rtasp);
				break;
			case SIL_SIMULATION:
				rtasp = new RTAtomicSimulatorPlugin();
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
	public void			execute() throws Exception
	{
		// In the electric meter, the accelerated clock is only used to get
		// properly time stamped sensor data in simulated time instants.
		this.clock = null;
		if (this.currentExecutionType.isIntegrationTest() ||
											this.currentExecutionType.isSIL()) {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			this.logMessage("ElectricMeter gets the clock.");
			this.clock =
				this.clocksServerOutboundPort.getClock(CVMGlobalTest.CLOCK_URI);
			this.doPortDisconnection(this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			this.logMessage("ElectricMeter got the clock " + this.clock);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.emip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}


	/**
	 * 		METHODES
	 */
	
	@Override
	public double		getCurrentConsumption() throws Exception {
		if (VERBOSE) 
			this.traceMessage("current consumption -> " +  electricConsumption + " watts\n");

		return electricConsumption;
	}

	@Override
	public double getCurrentProduction() throws Exception {
		if (VERBOSE) 
			this.traceMessage("current production -> " +  electricProduction + " watts\n");
		
		return electricProduction;
	}

	@Override
	public synchronized void addElectricProduction(double quantity) throws Exception {
		this.electricProduction += quantity;
		
		if(VERBOSE) {
			this.traceMessage("add " + quantity + "watts to the production\n");
			this.traceMessage("the total production quantity is " + this.electricProduction + "\n");
		}
	}

	@Override
	public synchronized void addElectricConsumption(double quantity) throws Exception {
		this.electricConsumption += quantity;
		
		if(VERBOSE) {
			this.traceMessage("add " + quantity + "watts to the consumption\n");
			this.traceMessage("the total consomation quantity is " + this.electricConsumption + "\n");
		}
	}
}