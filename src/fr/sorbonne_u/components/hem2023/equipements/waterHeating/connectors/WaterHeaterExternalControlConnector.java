package fr.sorbonne_u.components.hem2023.equipements.waterHeating.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterExternalControlCI;
import fr.sorbonne_u.components.hem2023.timer.Timer;

public class WaterHeaterExternalControlConnector extends AbstractConnector implements WaterHeaterExternalControlCI {

	@Override
	public int getCurrentTemperature() throws Exception {
		return ((WaterHeaterExternalControlCI)this.offering).getCurrentTemperature();
	}

	@Override
	public int getEnergyConsumption() throws Exception {
		return ((WaterHeaterExternalControlCI)this.offering).getEnergyConsumption();
	}

	@Override
	public void heating() throws Exception {
		((WaterHeaterExternalControlCI)this.offering).heating();
	}

	@Override
	public void startHeating() throws Exception {
		((WaterHeaterExternalControlCI)this.offering).startHeating();
	}

	@Override
	public void stopHeating() throws Exception {
		((WaterHeaterExternalControlCI)this.offering).stopHeating();
	}

	@Override
	public int getTargetTemperature() throws Exception {
		return ((WaterHeaterExternalControlCI)this.offering).getTargetTemperature();
	}

	@Override
	public Timer getTimer() throws Exception {
		return ((WaterHeaterExternalControlCI)this.offering).getTimer();
	}

	@Override
	public void setPowerLevel(WaterHeaterPowerLevel power) throws Exception {
		((WaterHeaterExternalControlCI)this.offering).setPowerLevel(power);
	}

	@Override
	public double getPowerLevel() throws Exception {
		return ((WaterHeaterExternalControlCI)this.offering).getPowerLevel();
	}

}
