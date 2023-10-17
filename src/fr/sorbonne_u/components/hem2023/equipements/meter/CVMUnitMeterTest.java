package fr.sorbonne_u.components.hem2023.equipements.meter;

import fr.sorbonne_u.components.AbstractComponent;

import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVMUnitMeterTest extends AbstractCVM {

	public CVMUnitMeterTest() throws Exception {
		super();
	}

	@Override
	public void			deploy() throws Exception {
		AbstractComponent.createComponent(
				ElectricMeter.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				ElectricMeterUnitTester.class.getCanonicalName(),
				new Object[]{});

		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVMUnitMeterTest cvm = new CVMUnitMeterTest();
			cvm.startStandardLifeCycle(1000L);
			Thread.sleep(100000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}