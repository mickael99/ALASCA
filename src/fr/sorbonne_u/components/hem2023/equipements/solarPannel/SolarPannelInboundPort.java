/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.solarPannel;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * @author Yukhoi
 *
 */
public class SolarPannelInboundPort extends AbstractInboundPort implements SolarPannelUserCI {

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
	public SolarPannelInboundPort(ComponentI owner) 
			throws Exception {
		super(SolarPannelUserCI.class, owner);
		assert	owner instanceof SolarPannelImplementationI;

	}

	/**
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public SolarPannelInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, SolarPannelUserCI.class, owner);
		assert	owner instanceof SolarPannelImplementationI;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	@Override
	public SolarPannelState getState() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((SolarPannelImplementationI)o).getState());
	}

	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((SolarPannelImplementationI)o).turnOn();
						return null;
				});
	}

	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((SolarPannelImplementationI)o).turnOff();
						return null;
				});
	}

	@Override
	public int getBattery() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((SolarPannelImplementationI)o).getBattery());
	}

}
