package fr.sorbonne_u.components.hem2023.test.productionUnit.gasGenerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2023.equipements.battery.Battery;
import fr.sorbonne_u.components.hem2023.equipements.battery.interfaces.BatteryI.TEST_TYPE;
import fr.sorbonne_u.components.hem2023.equipements.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.GasGenerator;

public class CVMTestGasGenerator extends AbstractCVM {
	public static final String	TEST_CLOCK_URI = "test-clock";
	public static final long	DELAY_TO_START_IN_MILLIS = 3000;

	public	CVMTestGasGenerator() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		
		AbstractComponent.createComponent(
				Battery.class.getCanonicalName(),
				new Object[]{"batteryURI", TEST_TYPE.PRODUCTION_UNIT});
		
		AbstractComponent.createComponent(
				ElectricMeter.class.getCanonicalName(),
				new Object[]{"electricMeterURI"});
	
		AbstractComponent.createComponent(
				GasGenerator.class.getCanonicalName(),
				new Object[]{"gasGeneratorInboundPortURI",
						 		"reflectionInboundPortURI",
						 		"energyTransferOutboundPortURI",
						 		"productionUnitProductionOutboundPort"});


		super.deploy();
	}
		
	public static void main(String[] args)
	{
		try {
			CVMTestGasGenerator cvm = 
						new CVMTestGasGenerator();
			cvm.startStandardLifeCycle(12000L);
			Thread.sleep(30000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
