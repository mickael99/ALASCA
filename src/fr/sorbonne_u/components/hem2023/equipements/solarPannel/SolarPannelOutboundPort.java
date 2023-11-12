/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.solarPannel;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * @author Yukhoi
 *
 */
public class SolarPannelOutboundPort extends AbstractOutboundPort implements SolarPannelUserCI {

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

	@Override
	public SolarPannelState getState() throws Exception {
		return ((SolarPannelUserCI)this.getConnector()).getState();
	}

	@Override
	public void turnOn() throws Exception {
		((SolarPannelUserCI)this.getConnector()).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((SolarPannelUserCI)this.getConnector()).turnOff();
	}

	@Override
	public int getBattery() throws Exception {
		return ((SolarPannelUserCI)this.getConnector()).getBattery();
	}

}
