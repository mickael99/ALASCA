/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.solarPannel;

import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * @author Yukhoi
 *
 */
public class SolarPannelConnector extends AbstractConnector implements SolarPannelUserCI {

	@Override
	public SolarPannelState getState() throws Exception {
		return ((SolarPannelUserCI)this.offering).getState();
	}

	@Override
	public void turnOn() throws Exception {
		((SolarPannelUserCI)this.offering).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((SolarPannelUserCI)this.offering).turnOff();
	}

	@Override
	public int getBattery() throws Exception {
		return ((SolarPannelUserCI)this.offering).getBattery();
	}

}
