package fr.sorbonne_u.components.hem2023.equipements.dishWasher;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.AbstractComponent;

public class CVMUnitDishWasherTest extends AbstractCVM {
	public CVMUnitDishWasherTest() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		AbstractComponent.createComponent(
				DishWasher.class.getCanonicalName(), 
				new Object[]{"URI_DISHWASHER"});
		
		AbstractComponent.createComponent(
				DishWasherTest.class.getCanonicalName(), 
				new Object[]{});
		
		super.deploy();
	}
	
	public static void main(String[] args) {
		try {
			CVMUnitDishWasherTest cvm = new CVMUnitDishWasherTest();
			cvm.startStandardLifeCycle(1000L);
			Thread.sleep(100000000L);
			System.exit(0);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
