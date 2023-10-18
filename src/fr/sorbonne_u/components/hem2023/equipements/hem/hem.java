package fr.sorbonne_u.components.hem2023.equipements.hem;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.hem2023.bases.AdjustableCI;
import fr.sorbonne_u.components.hem2023.equipements.meter.interfaces.ElectricMeterCI;

public class hem extends AbstractComponent implements ElectricMeterCI, AdjustableCI {

	/**
	 * 
	 * 				CONSTRUCTORS
	 */
	protected hem() throws Exception {
		super(1, 0);
	}
	
	protected hem(String uriId) throws Exception {
		super(uriId, 1, 0);
	}
	
	@Override
	public int maxMode() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean upMode() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean downMode() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setMode(int modeIndex) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int currentMode() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean suspended() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean suspend() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean resume() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double emergency() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getCurrentConsumption() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getCurrentProduction() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
