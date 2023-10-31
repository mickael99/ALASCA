/**
 * 
 */
package fr.sorbonne_u.components.hem2023.equipements.gasGenerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

/**
 * @author Yukhoi
 *
 */
public class CVMUnitTestGasGenerator extends AbstractCVM {

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public CVMUnitTestGasGenerator() throws Exception
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
					GasGenerator.class.getCanonicalName(),
					new Object[]{});

		AbstractComponent.createComponent(
					GasGeneratorTester.class.getCanonicalName(),
					new Object[]{true});

		super.deploy();
	}

	public static void main(String[] args)
	{
		try {
			CVMUnitTestGasGenerator cvm = new CVMUnitTestGasGenerator();
			cvm.startStandardLifeCycle(100000L);
			Thread.sleep(1000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
