/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.interfaces.SolarPannelImplementationI;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.interfaces.SolarPannelUserCI;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.interfaces.SolarPannelImplementationI.SolarPannelState;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * @author Yukhoi
 *
 */
public class SolarPannelInboundPort extends AbstractInboundPort {

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
	

	public SolarPannelState getState() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((SolarPannelImplementationI)o).getState());
	}

	public void turnOn() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((SolarPannelImplementationI)o).turnOn();
						return null;
				});
	}

	public void turnOff() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((SolarPannelImplementationI)o).turnOff();
						return null;
				});
	}

	public double getBattery() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((SolarPannelImplementationI)o).getBattery());
	}

}
