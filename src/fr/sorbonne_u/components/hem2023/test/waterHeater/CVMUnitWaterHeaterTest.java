package fr.sorbonne_u.components.hem2023.test.waterHeater;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.WaterHeater;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.WaterHeaterTest;

public class CVMUnitWaterHeaterTest extends AbstractCVM {
	public CVMUnitWaterHeaterTest() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		AbstractComponent.createComponent(
				WaterHeater.class.getCanonicalName(), 
				new Object[]{"URI_WATER_HEATER", false});
		
		AbstractComponent.createComponent(
				WaterHeaterTest.class.getCanonicalName(), 
				new Object[]{});
		
		super.deploy();
	}
	
	public static void main(String[] args) {
		try {
			CVMUnitWaterHeaterTest cvm = new CVMUnitWaterHeaterTest();
			cvm.startStandardLifeCycle(1000L);
			Thread.sleep(100000000L);
			System.exit(0);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
