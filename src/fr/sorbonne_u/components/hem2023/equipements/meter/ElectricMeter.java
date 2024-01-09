package fr.sorbonne_u.components.hem2023.equipements.meter;


import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterCI;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2023.equipements.meter.ports.ElectricMeterConsomationInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.meter.ports.ElectricMeterInboundPort;
import fr.sorbonne_u.components.hem2023.equipements.meter.ports.ElectricMeterProductionInboundPort;

@OfferedInterfaces(offered={ElectricMeterCI.class})
public class ElectricMeter extends	AbstractComponent implements ElectricMeterImplementationI
{
	public static final String	ELECTRIC_METER_INBOUND_PORT_URI =
															"ELECTRIC-METER";
	public static final String PRODUCTION_URI = "PRODUCTION_URI";
	public static final String CONSOMATION_URI = "CONSOMATION_URI";
	
	public static final boolean	VERBOSE = true;

	protected ElectricMeterInboundPort	electricMeterInboundPort;
	protected ElectricMeterProductionInboundPort electricMeterProductionInboundPort;
	protected ElectricMeterConsomationInboundPort electricMeterConsomationInboundPort;
	
	protected double electricProduction;
	protected double electricConsumption;

	/**
	 * 				CONSTRUCTEURS
	 */

	protected ElectricMeter() throws Exception {
		super(1, 0);
		initialise();
		
		if(VERBOSE)
			this.traceMessage("electric meter ready\n");
	}

	protected ElectricMeter(String uriId) throws Exception
	{
		super(uriId, 1, 0);
		initialise();
		
		if(VERBOSE)
			this.traceMessage("electric meter ready\n");
	}

	protected void initialise() throws Exception {
		this.electricMeterInboundPort =
				new ElectricMeterInboundPort(ELECTRIC_METER_INBOUND_PORT_URI, this);
		this.electricMeterInboundPort.publishPort();
		
		this.electricMeterProductionInboundPort =
				new ElectricMeterProductionInboundPort(PRODUCTION_URI, this);
		this.electricMeterProductionInboundPort.publishPort();
		
		this.electricMeterConsomationInboundPort =
				new ElectricMeterConsomationInboundPort(CONSOMATION_URI, this);
		this.electricMeterConsomationInboundPort.publishPort();
		
		electricProduction = 0.0;
		electricConsumption = 0.0;

		if (VERBOSE) {
			this.tracer.get().setTitle("Electric meter component");
			this.tracer.get().setRelativePosition(2, 1);
			this.toggleTracing();
		}
	}

	/**
	 * 			CYCLE DE VIE
	 */

	@Override
	public synchronized void shutdown() throws ComponentShutdownException
	{
		try {
			if(VERBOSE)
				this.traceMessage("déconnexion des ports du compteur éléctrique");
			this.electricMeterInboundPort.unpublishPort();
			this.electricMeterProductionInboundPort.unpublishPort();
			this.electricMeterConsomationInboundPort.unpublishPort();
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