/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.microwave;

import fr.sorbonne_u.components.hem2023.timer.Timer;

/**
 * @author Yukhoi
 *
 */
public interface MicrowaveImplementationI {
	
	public static enum MicrowaveState
	{
		ON,
		OFF
	}
	
	public static enum MicrowaveMode
	{
		UNFREEZE,
		LOW,			
		MEDDIUM,
		HIGH
	}

	public MicrowaveState	getState() throws Exception;
	
	public MicrowaveMode 	getMode() throws Exception;
	
	
	/**
	 * turn on the microwave
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 *
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			turnOn() throws Exception;

	/**
	 * turn off the microwave.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * 
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			turnOff() throws Exception;
	
	public void 		setHigh() throws Exception;
	
	public void 		setMeddium() throws Exception;

	public void 		setLow() throws Exception;

	public void 		setUnfreez() throws Exception;

		
	public void			setTimer(Timer newTimer) throws Exception;


	
	
	
}
