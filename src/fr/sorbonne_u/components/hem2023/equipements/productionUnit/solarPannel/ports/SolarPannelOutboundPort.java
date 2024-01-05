/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.interfaces.SolarPannelUserCI;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.interfaces.SolarPannelImplementationI.SolarPannelState;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * @author Yukhoi
 *
 */
public class SolarPannelOutboundPort extends AbstractOutboundPort 
	implements SolarPannelUserCI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * @param owner
	 * @throws Exception
	 */
	public SolarPannelOutboundPort(ComponentI owner) throws Exception {
		super(SolarPannelUserCI.class, owner);
	}

	/**
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public SolarPannelOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, SolarPannelUserCI.class, owner);
	}
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public SolarPannelState getState() throws Exception {
		return ((SolarPannelUserCI)this.getConnector()).getState();
	}

	
	public void turnOn() throws Exception {
		((SolarPannelUserCI)this.getConnector()).turnOn();
	}

	public void turnOff() throws Exception {
		((SolarPannelUserCI)this.getConnector()).turnOff();
	}

	public double getBattery() throws Exception {
		return ((SolarPannelUserCI)this.getConnector()).getBattery();
	}
}
