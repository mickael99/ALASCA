/**
 * 
 */
package fr.sorbonne_u.components.hem2023;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2023.equipements.fan.Fan;
import fr.sorbonne_u.components.hem2023.equipements.fan.FanTester;
import fr.sorbonne_u.components.hem2023.equipements.microwave.Microwave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.MicrowaveTester;

/**
 * @author Yukhoi
 *
 */
public class CVMIntegrationTest extends AbstractCVM {
	public static final String	TEST_CLOCK_URI = "test-clock";
	public static final long	DELAY_TO_START_IN_MILLIS = 3000;

	public				CVMIntegrationTest() throws Exception
	{
		super();
	}

	@Override
	public void			deploy() throws Exception
	{
		
		AbstractComponent.createComponent(
				Fan.class.getCanonicalName(),
				new Object[]{});
		
		AbstractComponent.createComponent(
				Microwave.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				FanTester.class.getCanonicalName(),
				new Object[]{true});
		
		AbstractComponent.createComponent(
				MicrowaveTester.class.getCanonicalName(),
				new Object[]{true});

		super.deploy();
	}
		
	public static void	main(String[] args)
	{
		try {
			CVMIntegrationTest cvm = new CVMIntegrationTest();
			cvm.startStandardLifeCycle(12000L);
			Thread.sleep(30000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
