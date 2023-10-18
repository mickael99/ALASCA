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
	 * @throws Exception	<i>to do</i>.
	 */
	public void			turnOn() throws Exception;

	/**
	 * turn off the microwave.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	public void			turnOff() throws Exception;
	
	/**
	 * set the microwave in mode high.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	public void 		setHigh() throws Exception;
	
	/**
	 * set the microwave in mode low.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	public void 		setMeddium() throws Exception;

	/**
	 * set the microwave in mode medium.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	public void 		setLow() throws Exception;

	/**
	 * set the microwave in mode unfreeze.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	public void 		setUnfreez() throws Exception;

	/**
	 * set the timer of the microwave.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */	
	public void			setTimer(Timer newTimer) throws Exception;


	
	
	
}
