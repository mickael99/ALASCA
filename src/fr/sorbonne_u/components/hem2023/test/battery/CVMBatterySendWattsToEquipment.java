package fr.sorbonne_u.components.hem2023.test.battery;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2023.equipements.battery.Battery;
import fr.sorbonne_u.components.hem2023.equipements.battery.interfaces.BatteryI.TEST_TYPE;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.DishWasher;
import fr.sorbonne_u.components.hem2023.equipements.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.gasGenerator.GasGenerator;
import fr.sorbonne_u.components.hem2023.equipements.productionUnit.solarPannel.SolarPannel;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.WaterHeater;

public class CVMBatterySendWattsToEquipment extends AbstractCVM {
	public static final String	TEST_CLOCK_URI = "test-clock";
	public static final long	DELAY_TO_START_IN_MILLIS = 3000;

	public CVMBatterySendWattsToEquipment() throws Exception {
		super();
	}

	@Override
	public void	deploy() throws Exception {
		
		//equipments
		AbstractComponent.createComponent(
				DishWasher.class.getCanonicalName(), 
				new Object[]{"URI_DISHWASHER", false});
		AbstractComponent.createComponent(
				WaterHeater.class.getCanonicalName(), 
				new Object[]{"URI_WATER_HEATER", false});
		
		//battery
		AbstractComponent.createComponent(
				Battery.class.getCanonicalName(),
				new Object[]{"batteryURI", TEST_TYPE.ALL});
		
		//meter
		AbstractComponent.createComponent(
				ElectricMeter.class.getCanonicalName(),
				new Object[]{"electricMeterURI"});
	
		//production unit
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
		
	public static void	main(String[] args)
	{
		try {
			CVMBatterySendWattsToEquipment cvm = new CVMBatterySendWattsToEquipment();
			cvm.startStandardLifeCycle(12000L);
			Thread.sleep(30000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
