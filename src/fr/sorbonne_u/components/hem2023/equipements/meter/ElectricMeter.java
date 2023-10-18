package fr.sorbonne_u.components.hem2023.equipements.meter;


import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterCI;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2023.equipements.meter.ports.ElectricMeterInboundPort;

@OfferedInterfaces(offered={ElectricMeterCI.class})
public class ElectricMeter extends	AbstractComponent implements ElectricMeterImplementationI
{
	public static final String	ELECTRIC_METER_INBOUND_PORT_URI =
															"ELECTRIC-METER";
	public static final boolean	VERBOSE = true;

	protected ElectricMeterInboundPort	electricMeterInboundPort;

	/**
	 * 				CONSTRUCTEURS
	 */

	protected ElectricMeter() throws Exception {
		super(1, 0);
		initialise();
	}

	protected ElectricMeter(String uriId) throws Exception
	{
		super(uriId, 1, 0);
		initialise();
	}

	protected void initialise() throws Exception {
		this.electricMeterInboundPort =
				new ElectricMeterInboundPort(ELECTRIC_METER_INBOUND_PORT_URI, this);
		this.electricMeterInboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Electric meter component");
			this.tracer.get().setRelativePosition(0, 1);
			this.toggleTracing();
		}
	}

	/**
	 * 			CYCLE DE VIE
	 */

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.electricMeterInboundPort.unpublishPort();
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
			this.traceMessage("consommation courrante -> 0 watts\n");

		return 0.0;
	}

	@Override
	public double getCurrentProduction() throws Exception {
		if (VERBOSE) 
			this.traceMessage("production courrante -> 0 watts\n");
		
		return 0.0;
	}
}