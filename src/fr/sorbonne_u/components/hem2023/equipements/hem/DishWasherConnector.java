package fr.sorbonne_u.components.hem2023.equipements.hem;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.bases.AdjustableCI;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherInternalAndControlI.WashingMode;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherInternalControlCI;

public class DishWasherConnector extends AbstractConnector implements AdjustableCI {
	protected static final int MAX_MODE = 3;
	protected static final boolean VERBOSE = true;
	
	@Override
	public int maxMode() throws Exception {
		return MAX_MODE;
	}

	@Override
	public boolean upMode() throws Exception {
		WashingMode currentWashingMode = ((DishWasherInternalControlCI)this.offering).getWashingMode();
		switch(currentWashingMode) {
			case ECO:
				((DishWasherInternalControlCI)this.offering).setWashingMode(WashingMode.FAST);
				break;
			case FAST:
				((DishWasherInternalControlCI)this.offering).setWashingMode(WashingMode.NORMAL);
				break;
			case NORMAL:
				((DishWasherInternalControlCI)this.offering).setWashingMode(WashingMode.INTENSIF);
				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	public boolean downMode() throws Exception {
		WashingMode currentWashingMode = ((DishWasherInternalControlCI)this.offering).getWashingMode();
		switch(currentWashingMode) {
			case FAST:
				((DishWasherInternalControlCI)this.offering).setWashingMode(WashingMode.ECO);
				break;
			case NORMAL:
				((DishWasherInternalControlCI)this.offering).setWashingMode(WashingMode.FAST);
				break;
			case INTENSIF:
				((DishWasherInternalControlCI)this.offering).setWashingMode(WashingMode.NORMAL);
				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	public boolean setMode(int modeIndex) throws Exception {
		switch(modeIndex) {
			case 0:
				((DishWasherInternalControlCI)this.offering).setWashingMode(WashingMode.ECO);
				break;
			case 1:
				((DishWasherInternalControlCI)this.offering).setWashingMode(WashingMode.FAST);
				break;
			case 2:
				((DishWasherInternalControlCI)this.offering).setWashingMode(WashingMode.NORMAL);
				break;
			case 3:
				((DishWasherInternalControlCI)this.offering).setWashingMode(WashingMode.INTENSIF);
				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	public int currentMode() throws Exception {
		WashingMode currentWashingMode = ((DishWasherInternalControlCI)this.offering).getWashingMode();
		switch(currentWashingMode) {
			case ECO:
				return 0;
			case FAST:
				return 1;
			case NORMAL:
				return 2;
			case INTENSIF:
				return 3;
			default:
				throw new Exception("Le mode courant du lave vaisselle n'est pas valide");
		}
	}

	@Override
	public boolean suspended() throws Exception {
		return ((DishWasherInternalControlCI)this.offering).isSuspended();
	}

	@Override
	public boolean suspend() throws Exception {
		return ((AdjustableCI)this.offering).suspend();
	}

	@Override
	public boolean resume() throws Exception {
		return ((AdjustableCI)this.offering).resume();
	}

	@Override
	public double emergency() throws Exception {
		return ((AdjustableCI)this.offering).emergency();
	}
}
