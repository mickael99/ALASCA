/**
 * 
 */
package fr.sorbonne_u.components.hem2023.test.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2023.equipements.fan.Fan;
import fr.sorbonne_u.components.hem2023.equipements.fan.FanTester;

/**
 * @author Yukhoi
 *
 */
public class CVMUnitTestFan extends AbstractCVM {

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				CVMUnitTestFan() throws Exception
	{
		
	}

	// -------------------------------------------------------------------------
	// CVM life-cycle
	// -------------------------------------------------------------------------

	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
					Fan.class.getCanonicalName(),
					new Object[]{});

		AbstractComponent.createComponent(
					FanTester.class.getCanonicalName(),
					new Object[]{true});

		super.deploy();
	}

	public static void		main(String[] args)
	{
		try {
			CVMUnitTestFan cvm = new CVMUnitTestFan();
			cvm.startStandardLifeCycle(100000L);
			Thread.sleep(1000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------
