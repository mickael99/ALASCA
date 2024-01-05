package fr.sorbonne_u.components.hem2023.equipements.productionUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2023.equipements.battery.Battery;
import fr.sorbonne_u.components.hem2023.equipements.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.CVMTestGasGenerator;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.GasGenerator;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.SolarPannel;

public class CVMTestProductionUnitTogether extends AbstractCVM {
	public static final String	TEST_CLOCK_URI = "test-clock";
	public static final long	DELAY_TO_START_IN_MILLIS = 3000;

	public	CVMTestProductionUnitTogether() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		
		AbstractComponent.createComponent(
				Battery.class.getCanonicalName(),
				new Object[]{"batteryURI"});
		
		AbstractComponent.createComponent(
				ElectricMeter.class.getCanonicalName(),
				new Object[]{"electricMeterURI"});
	
		AbstractComponent.createComponent(
				GasGenerator.class.getCanonicalName(),
				new Object[]{"gasGeneratorInboundPortURI",
						 		"gasGeneratorReflectionInboundPortURI",
						 		"gasGeneratorEnergyTransferOutboundPortURI",
						 		"gasGeneratorproductionUnitProductionOutboundPort"});
		AbstractComponent.createComponent(
				SolarPannel.class.getCanonicalName(),
				new Object[]{"solarPannelInboundPortURI",
						 		"solarPannelreflectionInboundPortURI",
						 		"solarPanneltransferEnergyOutboundPortURI",
						 		"solarPannelproductionOutboundPortURI"});

		super.deploy();
	}
		
	public static void main(String[] args)
	{
		try {
			CVMTestProductionUnitTogether cvm = 
						new CVMTestProductionUnitTogether();
			cvm.startStandardLifeCycle(12000L);
			Thread.sleep(30000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
