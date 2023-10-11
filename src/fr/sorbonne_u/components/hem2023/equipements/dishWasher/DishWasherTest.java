package fr.sorbonne_u.components.hem2023.equipements.dishWasher;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors.DishWasherInternalControlConnector;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors.DishWasherInternalControlOutboundPort;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors.DishWasherUserControlConnector;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors.DishWasherUserControlOutboundPort;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

@RequiredInterfaces(required={DishWasherUserControlCI.class, DishWasherInternalControlCI.class, ClocksServerCI.class})
public class DishWasherTest extends AbstractComponent {
	public static final String URI_INTERNAL_CONTROL_OUTBOUND_PORT
		= "URI_INTERNAL_CONTROL_OUTBOUND_PORT";
	
	public static final String URI_USER_CONTROL_OUTBOUND_PORT
		= "URI_USER_CONTROL_OUTBOUND_PORT";
	
	protected DishWasherInternalControlOutboundPort dishWasherInternalControlOutboundPort;
	protected DishWasherUserControlOutboundPort dishWasherUserControlOutboundPort;
	protected ClocksServerOutboundPort clocksServerOutboundPort;

	
	protected DishWasherTest() throws Exception {
		super(1, 0);
		initialisePort();
	}
	
	protected DishWasherTest(String UriID) throws Exception {
		super(UriID, 1, 0);
		initialisePort();
	}
	
	private void initialisePort() throws Exception {		
		dishWasherInternalControlOutboundPort = 
				new DishWasherInternalControlOutboundPort(URI_INTERNAL_CONTROL_OUTBOUND_PORT, this);
		dishWasherInternalControlOutboundPort.publishPort();
		
		dishWasherUserControlOutboundPort =
				new DishWasherUserControlOutboundPort(URI_USER_CONTROL_OUTBOUND_PORT, this);
		dishWasherUserControlOutboundPort.publishPort();
		
		this.tracer.get().setTitle("Testeur du lave vaisselle");
		this.tracer.get().setRelativePosition(1, 1);
		this.toggleTracing();
	}
	
	public synchronized void start() throws ComponentStartException {
		super.start();
			
		try {
			this.doPortConnection(dishWasherInternalControlOutboundPort.getPortURI(), 
					DishWasher.URI_INTERNAL_CONTROL_INBOUND_PORT, 
					DishWasherInternalControlConnector.class.getCanonicalName());
			
			this.doPortConnection(dishWasherUserControlOutboundPort.getPortURI(), 
					DishWasher.URI_USER_CONTROL_INBOUND_PORT, 
					DishWasherUserControlConnector.class.getCanonicalName());
		}catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}
	public synchronized void execute() throws Exception {
		super.execute();
	}
	
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(dishWasherInternalControlOutboundPort.getPortURI());
		this.doPortDisconnection(dishWasherUserControlOutboundPort.getPortURI());
		super.finalise();
	}
	
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			dishWasherInternalControlOutboundPort.unpublishPort();
			dishWasherUserControlOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}
