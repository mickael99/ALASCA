/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.microwave;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * @author Yukhoi
 *
 */
public class MicrowaveOutboundPort extends AbstractOutboundPort implements MicrowaveUserCI {

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
	public MicrowaveOutboundPort(ComponentI owner) 
			throws Exception {
		super(MicrowaveUserCI.class, owner);
	}

	/**
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public MicrowaveOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, MicrowaveUserCI.class, owner);
	}
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public MicrowaveState getState() throws Exception {
		return ((MicrowaveUserCI)this.getConnector()).getState();
	}
	

	@Override
	public MicrowaveMode getMode() throws Exception {
		return ((MicrowaveUserCI)this.getConnector()).getMode();
	}


	@Override
	public void turnOn() throws Exception {
		((MicrowaveUserCI)this.getConnector()).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((MicrowaveUserCI)this.getConnector()).turnOff();

	}

	@Override
	public void setTimer(Timer newTimer) throws Exception {
		((MicrowaveUserCI)this.getConnector()).setTimer(newTimer);

	}

	@Override
	public void setHigh() throws Exception {
		((MicrowaveUserCI)this.getConnector()).setHigh();
		
	}

	@Override
	public void setMeddium() throws Exception {
		((MicrowaveUserCI)this.getConnector()).setMeddium();
		
	}

	@Override
	public void setLow() throws Exception {
		((MicrowaveUserCI)this.getConnector()).setLow();
		
	}

	@Override
	public void setUnfreez() throws Exception {
		((MicrowaveUserCI)this.getConnector()).setUnfreez();
		
	}

}
