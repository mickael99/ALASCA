package fr.sorbonne_u.components.hem2023.equipements.waterHeating.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterUserControlCI;
import fr.sorbonne_u.components.hem2023.timer.Timer;

public class WaterHeaterUserControlConnector extends AbstractConnector implements WaterHeaterUserControlCI {

	@Override
	public void turnOn() throws Exception {
		((WaterHeaterUserControlCI)this.offering).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((WaterHeaterUserControlCI)this.offering).turnOff();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((WaterHeaterUserControlCI)this.offering).isOn();
	}

	@Override
	public void setTargetWaterTemperature(int degree) throws Exception {
		((WaterHeaterUserControlCI)this.offering).setTargetWaterTemperature(degree);
	}

	@Override
	public void scheduleHeating(Timer launchTime, Timer endTime) throws Exception {
		((WaterHeaterUserControlCI)this.offering).scheduleHeating(launchTime, endTime);
	}

	@Override
	public int getTargetTemperature() throws Exception {
		return ((WaterHeaterUserControlCI)this.offering).getTargetTemperature();
	}

	@Override
	public Timer getTimer() throws Exception {
		return ((WaterHeaterUserControlCI)this.offering).getTimer();
	}

	@Override
	public void setPowerLevel(WaterHeaterPowerLevel power) throws Exception {
		((WaterHeaterUserControlCI)this.offering).setPowerLevel(power);
	}

	@Override
	public double getPowerLevel() throws Exception {
		return ((WaterHeaterUserControlCI)this.offering).getPowerLevel();
	}
	
	public void removeTimer() throws Exception {
		((WaterHeaterUserControlCI)this.offering).removeTimer();
	}

	@Override
	public void startHeating() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopHeating() throws Exception {
		// TODO Auto-generated method stub
		
	}
}

