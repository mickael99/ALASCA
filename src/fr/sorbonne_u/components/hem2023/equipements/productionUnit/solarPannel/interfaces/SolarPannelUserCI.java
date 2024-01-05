/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.interfaces;

import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.interfaces.SolarPannelImplementationI.SolarPannelState;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * @author Yukhoi
 *
 */
public interface SolarPannelUserCI extends OfferedCI, RequiredCI {

	public SolarPannelState	getState() throws Exception;
	public void	turnOn() throws Exception;
	public void	turnOff() throws Exception;
	
	public double getBattery() throws Exception;
}
