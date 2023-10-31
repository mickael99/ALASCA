package fr.sorbonne_u.components.hem2023.equipements.hem;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.bases.AdjustableCI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterUserAndExternalControlI.WaterHeaterPowerLevel;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterExternalControlCI;

public class WaterHeaterConnector extends AbstractConnector implements AdjustableCI {
	protected static final int MAX_MODE = 2;
	protected int currentMode = 0;
	protected boolean suspended = false;
	
	@Override
	public int maxMode() throws Exception {
		return MAX_MODE;
	}

	@Override
	public boolean upMode() throws Exception {
		WaterHeaterPowerLevel currentWaterHeaterPowerLevel = 
				((WaterHeaterExternalControlCI)this.offering).getWaterHeaterPowerLevel();
		switch(currentWaterHeaterPowerLevel) {
			case LOW:
				((WaterHeaterExternalControlCI)this.offering).setPowerLevel(WaterHeaterPowerLevel.NORMAL);
				break;
			case NORMAL:
				((WaterHeaterExternalControlCI)this.offering).setPowerLevel(WaterHeaterPowerLevel.HIGH);
				break;
			default:
				return false;
		}
		currentMode++;
		return true;
	}

	@Override
	public boolean downMode() throws Exception {
		WaterHeaterPowerLevel currentWaterHeaterPowerLevel = 
				((WaterHeaterExternalControlCI)this.offering).getWaterHeaterPowerLevel();
		switch(currentWaterHeaterPowerLevel) {
			case NORMAL:
				((WaterHeaterExternalControlCI)this.offering).setPowerLevel(WaterHeaterPowerLevel.LOW);
				break;
			case HIGH:
				((WaterHeaterExternalControlCI)this.offering).setPowerLevel(WaterHeaterPowerLevel.NORMAL);
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
				((WaterHeaterExternalControlCI)this.offering).setPowerLevel(WaterHeaterPowerLevel.LOW);
				break;
			case 1:
				((WaterHeaterExternalControlCI)this.offering).setPowerLevel(WaterHeaterPowerLevel.NORMAL);
				break;
			case 2:
				((WaterHeaterExternalControlCI)this.offering).setPowerLevel(WaterHeaterPowerLevel.HIGH);
				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	public int currentMode() throws Exception {
		WaterHeaterPowerLevel currentWaterHeaterPowerLevel = 
				((WaterHeaterExternalControlCI)this.offering).getWaterHeaterPowerLevel();
		switch(currentWaterHeaterPowerLevel) {
			case LOW:
				return 0;
			case NORMAL:
				return 1;
			case HIGH:
				return 2;
			default:
				throw new Exception("Le mode courant du chauffe eau n'est pas valide");
		}
	}

	@Override
	public boolean suspended() throws Exception {
		return suspended;
	}

	@Override
	public boolean suspend() throws Exception {
		if(((WaterHeaterExternalControlCI)this.offering).suspend()) {
			this.suspended = true;
			return true;
		}
		return false;
			
	}

	@Override
	public boolean resume() throws Exception {
		if(((WaterHeaterExternalControlCI)this.offering).resume()) {
			suspended = false;
			return true;
		}
		return false;
	}

	@Override
	public double emergency() throws Exception {
		return ((WaterHeaterExternalControlCI)this.offering).emergency();
	}
}
