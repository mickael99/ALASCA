/**
 * 
 */
package fr.sorbonne_u.components.hem2023.gasGenerator;

import fr.sorbonne_u.components.hem2023.gasGenerator.GasGeneratorImplementationI.GasGeneratorState;
import fr.sorbonne_u.components.hem2023.gasGenerator.GasGeneratorImplementationI.GasGeneretorMode;

/**
 * @author Yukhoi
 *
 */
public interface GasGeneratorUserCI {

	public GasGeneratorState 	getState() throws Exception;
	
	public GasGeneretorMode 	getMode() throws Exception;

	public void	turnOn() throws Exception;
	
	public void	turnOff() throws Exception;
	
	public int getBattery() throws Exception;
}
