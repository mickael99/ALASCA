package fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.DishWasherInternalControlCI;
import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class DishWasherInternalControlInboundPort extends AbstractInboundPort implements DishWasherInternalControlCI {
	
	private static final long serialVersionUID = 1L;

	public DishWasherInternalControlInboundPort(ComponentI owner) throws Exception{
		super(DishWasherInternalControlCI.class, owner);
		
		//verifier la provenance du composant owner
	}
	
	public DishWasherInternalControlInboundPort(String uri,ComponentI owner) throws Exception{
		super(uri, DishWasherInternalControlCI.class, owner);
		
		//verifier la provenance du composant owner
	}
	
	@Override
	public boolean isDoorOpen() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public WashingMode getWashingMode() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timer getTimer() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDryingModeEnable() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getEnergyConsumption() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getWaterQuantity() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void startWashing() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopWashing() throws Exception {
		// TODO Auto-generated method stub

	}
	
	public boolean removeWaterQuantity(double waterQuantityToRemove) throws Exception {
		return false;
	}
}
