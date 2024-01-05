/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.interfaces;

/**
 * @author Yukhoi
 *
 */
public interface SolarPannelImplementationI {

	public static enum SolarPannelState {
		ON,
		OFF
	}

	public SolarPannelState	getState() throws Exception;
	public void	turnOn() throws Exception;
	public void	turnOff() throws Exception;
	public double getBattery() throws Exception;

	public void turnOffIfSolarIntensityIsToHigh() throws Exception;
	
	public boolean addBattery(double quantity) throws Exception;
	public boolean sendBattery(double quantity) throws Exception;
	
	public void productionToElectricMeter(double quantity) throws Exception;
}
