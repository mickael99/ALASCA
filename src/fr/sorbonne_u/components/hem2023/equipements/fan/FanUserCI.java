/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * @author Yukhoi
 *
 */
public interface FanUserCI extends FanImplementationI, OfferedCI, RequiredCI {

	public FanState		getState() throws Exception;
	
	public FanMode		getMode() throws Exception;
	
	public FanMusic getMusicState() throws Exception;

	public void			turnOn() throws Exception;

	public void			turnOff() throws Exception;
	
	public void			turnOnMusic() throws Exception;

	public void			turnOffMusic() throws Exception;
	
	public void			setHigh() throws Exception;

	public void			setLow() throws Exception;
	
	public void			setMeddium() throws Exception;
	
	
}
