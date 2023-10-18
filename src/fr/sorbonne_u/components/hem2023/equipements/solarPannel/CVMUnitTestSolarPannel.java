/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.solarPannel;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

/**
 * @author Yukhoi
 *
 */
public class CVMUnitTestSolarPannel extends AbstractCVM {

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public CVMUnitTestSolarPannel() throws Exception
	{
		
	}

	// -------------------------------------------------------------------------
	// CVM life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void	deploy() throws Exception
	{
		AbstractComponent.createComponent(
					SolarPannel.class.getCanonicalName(),
					new Object[]{});

		AbstractComponent.createComponent(
					SolarPannelTester.class.getCanonicalName(),
					new Object[]{true});

		super.deploy();
	}

	public static void main(String[] args)
	{
		try {
			CVMUnitTestSolarPannel cvm = new CVMUnitTestSolarPannel();
			cvm.startStandardLifeCycle(100000L);
			Thread.sleep(1000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
