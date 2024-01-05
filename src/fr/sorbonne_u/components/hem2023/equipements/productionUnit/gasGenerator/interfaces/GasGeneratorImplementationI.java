/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.interfaces;

/**
 * @author Yukhoi
 *
 */
public interface GasGeneratorImplementationI {
	
	public static enum GasGeneratorState {
		ON,
		OFF
	}
	
	public static enum GasGeneretorMode {
		LOW,			
		MEDDIUM,
		HIGH
	}
	
	public GasGeneratorState getState() throws Exception;
	
	public GasGeneretorMode getMode() throws Exception;

	public void	turnOn() throws Exception;
	
	public void	turnOff() throws Exception;
	
	public double getBattery() throws Exception;
	
	public boolean addBattery(double quantity) throws Exception;
	
	public boolean sendBattery(double quantity) throws Exception;
	
	public void	setHigh() throws Exception;

	public void	setLow() throws Exception;
	
	public void	setMeddium() throws Exception;
	
	public void productionToElectricMeter(double quantity) throws Exception;
	
}
