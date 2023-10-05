package fr.sorbonne_u.components.hem2023.equipements.dishWasher.Connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.DishWasherInternalControlCI;
import fr.sorbonne_u.components.hem2023.timer.Timer;

public class DishWasherInternalControlConnector extends AbstractConnector implements DishWasherInternalControlCI {

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

}
