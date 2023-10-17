/**
 * 
 */
package fr.sorbonne_u.components.hem2023.gasGenerator;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.gasGenerator.GasGeneratorImplementationI.GasGeneratorState;
import fr.sorbonne_u.components.hem2023.gasGenerator.GasGeneratorImplementationI.GasGeneretorMode;

/**
 * @author Yukhoi
 *
 */
public class GasGeneratorConnector extends AbstractConnector implements GasGeneratorUserCI {

	@Override
	public GasGeneratorState getState() throws Exception {
		return ((GasGeneratorUserCI)this.offering).getState();
	}

	@Override
	public GasGeneretorMode getMode() throws Exception {
		return ((GasGeneratorUserCI)this.offering).getMode();
	}

	@Override
	public void turnOn() throws Exception {
		((GasGeneratorUserCI)this.offering).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		((GasGeneratorUserCI)this.offering).turnOff();
	}

	@Override
	public int getBattery() throws Exception {
		return ((GasGeneratorUserCI)this.offering).getBattery();
	}

}
