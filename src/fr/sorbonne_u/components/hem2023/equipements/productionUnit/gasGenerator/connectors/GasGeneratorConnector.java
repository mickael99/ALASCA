/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.interfaces.GasGeneratorImplementationI.GasGeneratorState;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.interfaces.GasGeneratorImplementationI.GasGeneretorMode;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.interfaces.GasGeneratorUserCI;

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
	public double getBattery() throws Exception {
		return ((GasGeneratorUserCI)this.offering).getBattery();
	}

	@Override
	public void setHigh() throws Exception {
		((GasGeneratorUserCI)this.offering).setHigh();		
	}

	@Override
	public void setLow() throws Exception {
		((GasGeneratorUserCI)this.offering).setLow();		
	}

	@Override
	public void setMeddium() throws Exception {
		((GasGeneratorUserCI)this.offering).setMeddium();		
	}
	
	@Override
	public boolean addBattery(double quantity) throws Exception {
		return ((GasGeneratorUserCI)this.offering).addBattery(quantity);
	}
}
