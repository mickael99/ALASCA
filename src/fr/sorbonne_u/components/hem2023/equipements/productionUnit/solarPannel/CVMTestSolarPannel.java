package fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2023.equipements.battery.Battery;
import fr.sorbonne_u.components.hem2023.equipements.meter.ElectricMeter;

public class CVMTestSolarPannel extends AbstractCVM {
	public static final String	TEST_CLOCK_URI = "test-clock";
	public static final long	DELAY_TO_START_IN_MILLIS = 3000;

	public	CVMTestSolarPannel() throws Exception {
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
				SolarPannel.class.getCanonicalName(),
				new Object[]{"solarPannelInboundPortURI",
						 		"reflectionInboundPortURI",
						 		"transferEnergyOutboundPortURI",
						 		"productionOutboundPortURI"});

		super.deploy();
	}
		
	public static void main(String[] args)
	{
		try {
			CVMTestSolarPannel cvm = 
						new CVMTestSolarPannel();
			cvm.startStandardLifeCycle(12000L);
			Thread.sleep(30000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
