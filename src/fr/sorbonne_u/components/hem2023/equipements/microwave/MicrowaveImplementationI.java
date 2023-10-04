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
	

	public MicrowaveState	getState() throws Exception;
	
	
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
	

	
	
	
	public void			setPower(int newPower) throws Exception;
	
	public void			setTimer(Timer newTimer) throws Exception;


	
	
	
}
