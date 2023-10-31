/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.gasGenerator;

/**
 * @author Yukhoi
 *
 */
public interface GasGeneratorImplementationI {
	
	public static enum GasGeneratorState
	{
		ON,
		OFF
	}
	
	public static enum GasGeneretorMode
	{
		LOW,			
		MEDDIUM,
		HIGH
	}
	
	public GasGeneratorState 	getState() throws Exception;
	
	public GasGeneretorMode 	getMode() throws Exception;

	public void	turnOn() throws Exception;
	
	public void	turnOff() throws Exception;
	
	public int getBattery() throws Exception;
	
	public void	setHigh() throws Exception;

	public void	setLow() throws Exception;
	
	public void	setMeddium() throws Exception;
}
