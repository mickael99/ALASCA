package fr.sorbonne_u.components.hem2023.equipements.hem;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.bases.AdjustableCI;

public class WaterHeaterConnector extends AbstractConnector implements AdjustableCI {

	@Override
	public int maxMode() throws Exception {
		return 0;
	}

	@Override
	public boolean upMode() throws Exception {
		return false;
	}

	@Override
	public boolean downMode() throws Exception {
		return false;
	}

	@Override
	public boolean setMode(int modeIndex) throws Exception {
		return false;
	}

	@Override
	public int currentMode() throws Exception {
		return 0;
	}

	@Override
	public boolean suspended() throws Exception {
		return false;
	}

	@Override
	public boolean suspend() throws Exception {
		return false;
	}

	@Override
	public boolean resume() throws Exception {
		return false;
	}

	@Override
	public double emergency() throws Exception {
		return 0;
	}
}
