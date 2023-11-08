/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.fan;

import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * @author Yukhoi
 *
 */
public class FanConnector extends AbstractConnector implements FanUserCI {

	@Override
	public FanState getState() throws Exception {
		return ((FanUserCI)this.offering).getState();
	}

	@Override
	public FanMode getMode() throws Exception {
		return ((FanUserCI)this.offering).getMode();
	}

	@Override
	public FanMusic getMusicState() throws Exception {
		return ((FanUserCI)this.offering).getMusicState();
	}

	@Override
	public void turnOn() throws Exception {
		((FanUserCI)this.offering).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((FanUserCI)this.offering).turnOff();
	}

	@Override
	public void turnOnMusic() throws Exception {
		((FanUserCI)this.offering).turnOnMusic();
	}

	@Override
	public void turnOffMusic() throws Exception {
		((FanUserCI)this.offering).turnOffMusic();
	}

	@Override
	public void setHigh() throws Exception {
		((FanUserCI)this.offering).setHigh();
	}

	@Override
	public void setLow() throws Exception {
		((FanUserCI)this.offering).setLow();
	}

	@Override
	public void setMeddium() throws Exception {
		((FanUserCI)this.offering).setMeddium();
	}
}
//------------------------------------------------------------
