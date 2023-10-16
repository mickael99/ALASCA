/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.microwave;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * @author Yukhoi
 *
 */
public class MicrowaveInboundPort 
extends AbstractInboundPort 
implements MicrowaveUserCI {

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
	public MicrowaveInboundPort(ComponentI owner) throws Exception {
		super(MicrowaveUserCI.class, owner);
		assert	owner instanceof MicrowaveImplementationI;
	}

	/**
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public MicrowaveInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, MicrowaveUserCI.class, owner);
		assert	owner instanceof MicrowaveImplementationI;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	@Override
	public MicrowaveState getState() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((MicrowaveImplementationI)o).getState());
	}

	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MicrowaveImplementationI)o).turnOn();
						return null;
				});
	}

	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MicrowaveImplementationI)o).turnOn();
						return null;
				});
	}

	@Override
	public void setTimer(Timer newTimer) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MicrowaveImplementationI)o).setTimer(newTimer);
						return null;
				});
	}

	@Override
	public MicrowaveMode getMode() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((MicrowaveImplementationI)o).getMode());
	}

	@Override
	public void setHigh() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MicrowaveImplementationI)o).setHigh();
						return null;
				});		
	}

	@Override
	public void setMeddium() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MicrowaveImplementationI)o).setMeddium();
						return null;
				});		
	}

	@Override
	public void setLow() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MicrowaveImplementationI)o).setLow();
						return null;
				});		
	}

	@Override
	public void setUnfreez() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MicrowaveImplementationI)o).setUnfreez();
						return null;
				});		
	}
}
//-----------------------------------------------
