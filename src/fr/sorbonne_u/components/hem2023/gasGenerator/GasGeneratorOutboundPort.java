/**
 * 
 */
package fr.sorbonne_u.components.hem2023.gasGenerator;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * @author Yukhoi
 *
 */
public class GasGeneratorOutboundPort extends AbstractOutboundPort implements GasGeneratorUserCI {

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
	public GasGeneratorOutboundPort(ComponentI owner) throws Exception {
		super(GasGeneratorUserCI.class, owner);
	}

	/**
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public GasGeneratorOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, GasGeneratorUserCI.class, owner);
	}
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public GasGeneratorState getState() throws Exception {
		return ((GasGeneratorUserCI)this.getConnector()).getState();
	}

	@Override
	public GasGeneretorMode getMode() throws Exception {
		return ((GasGeneratorUserCI)this.getConnector()).getMode();
	}

	@Override
	public void turnOn() throws Exception {
		((GasGeneratorUserCI)this.getConnector()).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((GasGeneratorUserCI)this.getConnector()).turnOff();
	}

	@Override
	public int getBattery() throws Exception {
		return ((GasGeneratorUserCI)this.getConnector()).getBattery();
	}

	@Override
	public void setHigh() throws Exception {
		((GasGeneratorUserCI)this.getConnector()).setHigh();
	}

	@Override
	public void setLow() throws Exception {
		((GasGeneratorUserCI)this.getConnector()).setLow();
	}

	@Override
	public void setMeddium() throws Exception {
		((GasGeneratorUserCI)this.getConnector()).setMeddium();
	}

}
