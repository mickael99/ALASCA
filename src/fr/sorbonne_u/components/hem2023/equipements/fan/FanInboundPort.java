/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * @author Yukhoi
 *
 */
public class 	FanInboundPort 
extends 		AbstractInboundPort 
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
	public FanInboundPort(ComponentI owner) 
			throws Exception {
		super(FanUserCI.class, owner);
		assert	owner instanceof FanImplementationI;

	}

	/**
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public FanInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, FanUserCI.class, owner);
		assert	owner instanceof FanImplementationI;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	@Override
	public FanState getState() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((FanImplementationI)o).getState());
	}

	@Override
	public FanMode getMode() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((FanImplementationI)o).getMode());
	}
	
	@Override
	public FanMusic getMusicState() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((FanImplementationI)o).getMusicState());
	}

	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((FanImplementationI)o).turnOn();
						return null;
				});
	}

	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((FanImplementationI)o).turnOff();
						return null;
				});
	}

	@Override
	public void turnOnMusic() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((FanImplementationI)o).turnOnMusic();
						return null;
				});
	}

	@Override
	public void turnOffMusic() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((FanImplementationI)o).turnOffMusic();
						return null;
				});
	}

	@Override
	public void setHigh() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((FanImplementationI)o).setHigh();
						return null;
				});
	}

	@Override
	public void setLow() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((FanImplementationI)o).setLow();
						return null;
				});
	}

	@Override
	public void setMeddium() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((FanImplementationI)o).setMeddium();
						return null;
				});
	}


}
//-----------------------------------------------------------------------------
