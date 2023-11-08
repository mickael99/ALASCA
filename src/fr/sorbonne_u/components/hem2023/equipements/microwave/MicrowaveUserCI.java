/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.microwave;

import fr.sorbonne_u.components.hem2023.timer.Timer;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * @author Yukhoi
 *
 */
public interface MicrowaveUserCI extends MicrowaveImplementationI, OfferedCI, RequiredCI {
	

	public MicrowaveState	getState() throws Exception;
	
	public void			turnOn() throws Exception;

	public void			turnOff() throws Exception;
	
	public void 		setHigh() throws Exception;
	
	public void 		setMeddium() throws Exception;

	public void 		setLow() throws Exception;

	public void 		setUnfreez() throws Exception;

	public void			setTimer(Timer newTimer) throws Exception;
}
//-------------------------------------------------------------------------------------------
