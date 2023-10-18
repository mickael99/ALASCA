/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * @author Yukhoi
 *
 */
public class 	FanOutboundPort
extends 		AbstractOutboundPort
implements 		FanUserCI {

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
	public FanOutboundPort(ComponentI owner) 
			throws Exception {
		super(FanUserCI.class, owner);
	}

	/**
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public FanOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, FanUserCI.class, owner);
	}
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public FanState getState() throws Exception {
		return ((FanUserCI)this.getConnector()).getState();
	}

	@Override
	public FanMode getMode() throws Exception {
		return ((FanUserCI)this.getConnector()).getMode();
	}

	@Override
	public FanMusic getMusicState() throws Exception {
		return ((FanUserCI)this.getConnector()).getMusicState();
	}
	
	@Override
	public void turnOn() throws Exception {
		((FanUserCI)this.getConnector()).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((FanUserCI)this.getConnector()).turnOff();

	}

	@Override
	public void turnOnMusic() throws Exception {
		((FanUserCI)this.getConnector()).turnOnMusic();

	}

	@Override
	public void turnOffMusic() throws Exception {
		((FanUserCI)this.getConnector()).turnOffMusic();

	}

	@Override
	public void setHigh() throws Exception {
		((FanUserCI)this.getConnector()).setHigh();

	}

	@Override
	public void setLow() throws Exception {
		((FanUserCI)this.getConnector()).setLow();

	}

	@Override
	public void setMeddium() throws Exception {
		((FanUserCI)this.getConnector()).setMeddium();

	}

}
