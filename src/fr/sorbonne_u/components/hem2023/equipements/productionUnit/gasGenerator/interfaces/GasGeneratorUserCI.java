/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.interfaces;

import fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.interfaces.GasGeneratorImplementationI.GasGeneratorState;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.interfaces.GasGeneratorImplementationI.GasGeneretorMode;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * @author Yukhoi
 *
 */
public interface GasGeneratorUserCI extends OfferedCI, RequiredCI {

	public GasGeneratorState 	getState() throws Exception;
	
	public GasGeneretorMode 	getMode() throws Exception;

	public void	turnOn() throws Exception;
	
	public void	turnOff() throws Exception;
	
	public double getBattery() throws Exception;
	
	public boolean addBattery(double quantity) throws Exception;
	
	public void	setHigh() throws Exception;

	public void	setLow() throws Exception;
	
	public void	setMeddium() throws Exception;
}
