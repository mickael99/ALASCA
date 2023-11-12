/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.microwave;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.timer.Timer;

/**
 * @author Yukhoi
 *
 */
public class MicrowaveConnector extends AbstractConnector implements MicrowaveUserCI {

	@Override
	public MicrowaveState getState() throws Exception {
		return ((MicrowaveUserCI)this.offering).getState();

	}
	
	@Override
	public MicrowaveMode getMode() throws Exception {
		return ((MicrowaveUserCI)this.offering).getMode();

	}
	
	@Override
	public void turnOn() throws Exception {
		((MicrowaveUserCI)this.offering).turnOn();

	}

	@Override
	public void turnOff() throws Exception {
		((MicrowaveUserCI)this.offering).turnOff();

	}

	@Override
	public void setTimer(Timer newTimer) throws Exception {
		((MicrowaveUserCI)this.offering).setTimer(newTimer);

	}

	@Override
	public void setHigh() throws Exception {
		((MicrowaveUserCI)this.offering).setHigh();
	}

	@Override
	public void setMeddium() throws Exception {
		((MicrowaveUserCI)this.offering).setMeddium();
		
	}

	@Override
	public void setLow() throws Exception {
		((MicrowaveUserCI)this.offering).setLow();
		
	}

	@Override
	public void setUnfreez() throws Exception {
		((MicrowaveUserCI)this.offering).setUnfreez();
		
	}

}
