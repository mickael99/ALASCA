/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.solarPannel;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * @author Yukhoi
 *
 */
public interface SolarPannelUserCI extends SolarPannelImplementationI, OfferedCI, RequiredCI {

	public SolarPannelState	getState() throws Exception;

	public void	turnOn() throws Exception;
	
	public void	turnOff() throws Exception;
	
	public int getBattery() throws Exception;
	


}
