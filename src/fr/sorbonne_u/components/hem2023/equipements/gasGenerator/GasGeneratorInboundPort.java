/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.gasGenerator;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * @author Yukhoi
 *
 */
public class GasGeneratorInboundPort extends AbstractInboundPort implements GasGeneratorUserCI {

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
	public GasGeneratorInboundPort(ComponentI owner) 
			throws Exception {
		super(GasGeneratorUserCI.class, owner);
		assert	owner instanceof GasGeneratorImplementationI;

	}

	/**
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public GasGeneratorInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, GasGeneratorUserCI.class, owner);
		assert	owner instanceof GasGeneratorImplementationI;
	}
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	@Override
	public GasGeneratorState getState() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((GasGeneratorImplementationI)o).getState());
	}

	@Override
	public GasGeneretorMode getMode() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((GasGeneratorImplementationI)o).getMode());
	}

	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((GasGeneratorImplementationI)o).turnOn();
						return null;
				});
	}

	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((GasGeneratorImplementationI)o).turnOff();
						return null;
				});
	}

	@Override
	public int getBattery() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((GasGeneratorImplementationI)o).getBattery());
	}

	@Override
	public void setHigh() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((GasGeneratorImplementationI)o).setHigh();
						return null;
				});		
	}

	@Override
	public void setLow() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((GasGeneratorImplementationI)o).setLow();
						return null;
				});		
	}

	@Override
	public void setMeddium() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((GasGeneratorImplementationI)o).setMeddium();
						return null;
				});		
	}

}
