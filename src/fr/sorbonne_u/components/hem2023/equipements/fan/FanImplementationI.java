/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan;



/**
 * @author Yukhoi
 *
 */
public interface FanImplementationI {

	public static enum FanState
	{
		ON,
		OFF
	}
	
	public static enum FanMode
	{
		LOW,			
		MEDDIUM,
		HIGH
	}
	
	public static enum FanMusic
	{
		ON,
		OFF
	}
	
	public FanState	getState() throws Exception;
	
	public FanMode	getMode() throws Exception;
	
	public FanMusic getMusicState() throws Exception;
	
	/**
	 * turn on the fan
	 * 
	 * <p><strong>Contract</strong></p>
	 * @throws Exception	<i>to do</i>.
	 */
	public void			turnOn() throws Exception;

	/**
	 * turn off the fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * @throws Exception	<i>to do</i>.
	 */
	public void			turnOff() throws Exception;
	
	/**
	 * turn on the music of the fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * @throws Exception	<i>to do</i>.
	 */
	public void			turnOnMusic() throws Exception;

	/**
	 * turn off the music of the fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * @throws Exception	<i>to do</i>.
	 */
	public void			turnOffMusic() throws Exception;

	/**
	 * set the fan in high mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * @throws Exception	<i>to do</i>.
	 */
	
	public void			setHigh() throws Exception;

	/**
	 * set the fan in low mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setLow() throws Exception;
	
	/**
	 * set the fan in medium mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setMeddium() throws Exception;
	
	

}

	

