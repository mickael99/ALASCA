/**
 * 
 */
package fr.sorbonne_u.components.hem2023.test.microwave;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2023.equipements.microwave.Microwave;
import fr.sorbonne_u.components.hem2023.equipements.microwave.MicrowaveTester;

/**
 * @author Yukhoi
 *
 */
public class CVMUnitTestMicroWave extends AbstractCVM {

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				CVMUnitTestMicroWave() throws Exception
	{
		
	}

	// -------------------------------------------------------------------------
	// CVM life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
					Microwave.class.getCanonicalName(),
					new Object[]{});

		AbstractComponent.createComponent(
				MicrowaveTester.class.getCanonicalName(),
					new Object[]{true});

		super.deploy();
	}

	public static void		main(String[] args)
	{
		try {
			CVMUnitTestMicroWave cvm = new CVMUnitTestMicroWave();
			cvm.startStandardLifeCycle(100000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------
