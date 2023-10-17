/**
 * 
 */
package fr.sorbonne_u.components.hem2023.gasGenerator;

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
}
