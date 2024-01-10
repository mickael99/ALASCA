/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.interfaces.SolarPannelUserCI;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.interfaces.SolarPannelImplementationI.SolarPannelState;

/**
 * @author Yukhoi
 *
 */
public class SolarPannelConnector extends AbstractConnector implements SolarPannelUserCI {

	public SolarPannelState getState() throws Exception {
		return ((SolarPannelUserCI)this.offering).getState();
	}

	public void turnOn() throws Exception {
		((SolarPannelUserCI)this.offering).turnOn();
	}

	public void turnOff() throws Exception {
		((SolarPannelUserCI)this.offering).turnOff();
	}

	public double getBattery() throws Exception {
		return ((SolarPannelUserCI)this.offering).getBattery();
	}

}
