/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.solarPannel;

/**
 * @author Yukhoi
 *
 */
public interface SolarPannelImplementationI {

	public static enum SolarPannelState
	{
		ON,
		OFF
	}
	
	public SolarPannelState	getState() throws Exception;

	public void	turnOn() throws Exception;
	
	public void	turnOff() throws Exception;
	
	public int getBattery() throws Exception;
	


	
}
