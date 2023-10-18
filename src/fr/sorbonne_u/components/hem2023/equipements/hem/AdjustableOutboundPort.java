package fr.sorbonne_u.components.hem2023.equipements.hem;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.bases.AdjustableCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class AdjustableOutboundPort extends	AbstractOutboundPort implements AdjustableCI {

	private static final long serialVersionUID = 1L;

	public AdjustableOutboundPort(ComponentI owner) throws Exception {
		super(AdjustableCI.class, owner);
	}

	public AdjustableOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, AdjustableCI.class, owner);
	}

	@Override
	public int maxMode() throws Exception {
		return ((AdjustableCI)this.getConnector()).maxMode();
	}

	@Override
	public boolean upMode() throws Exception {
		return ((AdjustableCI)this.getConnector()).upMode();
	}

	@Override
	public boolean downMode() throws Exception {
		return ((AdjustableCI)this.getConnector()).downMode();
	}

	@Override
	public boolean setMode(int modeIndex) throws Exception {
		return ((AdjustableCI)this.getConnector()).setMode(modeIndex);
	}

	@Override
	public int currentMode() throws Exception {
		return ((AdjustableCI)this.getConnector()).currentMode();
	}

	@Override
	public boolean suspended() throws Exception {
		return ((AdjustableCI)this.getConnector()).suspended();
	}

	@Override
	public boolean suspend() throws Exception {
		return ((AdjustableCI)this.getConnector()).suspend();
	}

	@Override
	public boolean resume() throws Exception {
		return ((AdjustableCI)this.getConnector()).resume();
	}

	@Override
	public double emergency() throws Exception {
		return ((AdjustableCI)this.getConnector()).emergency();
	}
}