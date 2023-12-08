package fr.sorbonne_u.components.hem2023;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2023.equipements.dishWasher.DishWasher;
import fr.sorbonne_u.components.hem2023.equipements.hem.HEM;
import fr.sorbonne_u.components.hem2023.equipements.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.WaterHeater;
import fr.sorbonne_u.components.AbstractComponent;

public class CVMIntegrationTestModularEquipement extends AbstractCVM {
	
	public CVMIntegrationTestModularEquipement() throws Exception {
		super();
	}

	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				ElectricMeter.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				DishWasher.class.getCanonicalName(),
				new Object[]{"URI_DISHWASHER"});

		AbstractComponent.createComponent(
				WaterHeater.class.getCanonicalName(),
				new Object[]{"URI_WATER_HEATER"});

		AbstractComponent.createComponent(
				HEM.class.getCanonicalName(),
				new Object[]{});

		super.deploy();
	}

	public static void main(String[] args)
	{
		try {
			CVMIntegrationTestModularEquipement cvm = new CVMIntegrationTestModularEquipement();
			cvm.startStandardLifeCycle(12000L);
			Thread.sleep(30000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
//-----------------------------------------------------------------------------

