/**
 * 
 */
package fr.sorbonne_u.components.hem2023.gasGenerator;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * @author Yukhoi
 *
 */
public interface GasGeneratorUserCI extends GasGeneratorImplementationI, OfferedCI, RequiredCI {

	public GasGeneratorState 	getState() throws Exception;
	
	public GasGeneretorMode 	getMode() throws Exception;

	public void	turnOn() throws Exception;
	
	public void	turnOff() throws Exception;
	
	public int getBattery() throws Exception;
	
	public void	setHigh() throws Exception;

	public void	setLow() throws Exception;
	
	public void	setMeddium() throws Exception;
}
