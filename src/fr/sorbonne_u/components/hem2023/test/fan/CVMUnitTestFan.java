/**
 * 
 */
package fr.sorbonne_u.components.hem2023.test.fan;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2023.equipements.fan.Fan;
import fr.sorbonne_u.components.hem2023.equipements.fan.FanTester;
import fr.sorbonne_u.components.hem2023.equipements.fan.FanUser;
import fr.sorbonne_u.components.hem2023.utils.ExecutionType;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

/**
 * @author Yukhoi
 *
 */
public class CVMUnitTestFan extends AbstractCVM {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** delay before starting the test scenarios, leaving time to build
	 *  and initialise the components and their simulators.				*/
	public static final long			DELAY_TO_START = 3000L;
	/** for unit tests and SIL simulation tests, a {@code Clock} is
	 *  used to get a time-triggered synchronisation of the actions of
	 *  the components in the test scenarios.								*/
	public static final String			CLOCK_URI = "hem-clock";
	/** start instant in test scenarios, as a string to be parsed.			*/
	public static final String			START_INSTANT =
													"2023-11-22T00:00:00.00Z";


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
					FanUser.class.getCanonicalName(),
					new Object[]{Fan.INBOUND_PORT_URI,
							 ExecutionType.UNIT_TEST});

		long unixEpochStartTimeInMillis =
				System.currentTimeMillis() + DELAY_TO_START;
		
		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[]{
						// URI of the clock to retrieve it
						CLOCK_URI,
						// start time in Unix epoch time
						TimeUnit.MILLISECONDS.toNanos(
									 		unixEpochStartTimeInMillis),
						// start instant synchronised with the start time
						Instant.parse(START_INSTANT),
						1.0});
		
		super.deploy();
	}

	public static void		main(String[] args)
	{
		try {
			CVMUnitTestFan cvm = new CVMUnitTestFan();
			cvm.startStandardLifeCycle(DELAY_TO_START + 1000L);
			Thread.sleep(1000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------
