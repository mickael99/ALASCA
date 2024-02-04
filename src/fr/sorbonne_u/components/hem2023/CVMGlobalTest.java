package fr.sorbonne_u.components.hem2023;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// real time distributed applications in the Java programming language.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2023.equipements.fan.Fan;
import fr.sorbonne_u.components.hem2023.equipements.fan.FanUser;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanStateModel;
import fr.sorbonne_u.components.hem2023.equipements.fan.mil.FanUserModel;
import fr.sorbonne_u.components.hem2023.utils.ExecutionType;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

// -----------------------------------------------------------------------------
/**
 * The class <code>CVMGlobalTest</code> defines an execution CVM script to
 * test the HEM application.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-11-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CVMGlobalTest
extends		AbstractCVM
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** delay before starting the test scenarios, leaving time to build
	 *  and initialise the components and their simulators.				*/
	public static final long			DELAY_TO_START = 3000L;
	/** delay to start the real time simulations on every model at the
	 *  same moment (the order is delivered to the models during this
	 *  delay; this delay must be ample enough to give the time to notify
	 *  all models of their start time and to initialise them before starting,
	 *  a value that depends upon the complexity of the simulation architecture
	 *  to be traversed and the component deployment (deployments on several
	 *  JVM and even more several computers require a larger delay.			*/
	public static final long			DELAY_TO_START_SIMULATION = 5000L;
	/** duration  of the simulation, in simulated time.						*/
	public static final double			SIMULATION_DURATION = 3.0;
	/** time unit in which {@code SIMULATION_DURATION} is expressed.		*/
	public static final TimeUnit		SIMULATION_TIME_UNIT = TimeUnit.HOURS;
	/** for real time simulations, the acceleration factor applied to the
	 *  the simulated time to get the execution time of the simulations. 	*/
	public static final double			ACCELERATION_FACTOR = 180.0;

	/** the type of execution, to select among the values of the
	 *  enumeration {@code ExecutionType}.									*/
	public static final ExecutionType	CURRENT_EXECUTION_TYPE =
											//ExecutionType.INTEGRATION_TEST;
											//ExecutionType.MIL_SIMULATION;
											//ExecutionType.MIL_RT_SIMULATION;
											ExecutionType.SIL_SIMULATION;
	/** the control mode of the heater controller for the next run.			*/
//	public static final ControlMode		CONTROL_MODE = ControlMode.PULL;

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

	/**
	 * create an instance of CVM.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public				CVMGlobalTest() throws Exception
	{
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		assert	!CURRENT_EXECUTION_TYPE.isUnitTest() :
				new PreconditionException(
						"!CURRENT_EXECUTION_TYPE.isUnitTest()");

		// Set the main execution run parameters, depending on the type of
		// execution that is required.
		// URI of the simulation architecture for the current run, if relevant.
		String architectureURI = "";
		String fanLocalSimulatorURI = "";
		String fanUserLocalSimulatorURI = "";
		String heaterLocalSimulatorURI = "";
		String meterLocalSimulatorURI = "";
		// acceleration factor for the current run, if relevant.
		double accelerationFactor = 0.0;
		// start time in Unix epoch time in nanoseconds.
		long unixEpochStartTimeInMillis = 0L;
		// start instant used for time-triggered synchronisation in unit tests
		// and SIL simulation runs.
		Instant	startInstant = null;
		// start time of the simulation, in simulated logical time, if relevant.
		double simulatedStartTime = 0.0;
		// duration of the simulation, in simulated logical time, if relevant.
		double simulationDuration = 0.0;

		switch (CURRENT_EXECUTION_TYPE) {
		case MIL_SIMULATION:
			architectureURI = GlobalSupervisor.MIL_SIM_ARCHITECTURE_URI;
			fanLocalSimulatorURI = FanStateModel.MIL_URI;
			fanUserLocalSimulatorURI = FanUserModel.MIL_URI;
//			heaterLocalSimulatorURI = HeaterCoupledModel.MIL_URI;
//			meterLocalSimulatorURI = ElectricMeterCoupledModel.MIL_URI;
			accelerationFactor = ACCELERATION_FACTOR;
			unixEpochStartTimeInMillis =
					System.currentTimeMillis() + DELAY_TO_START_SIMULATION;
			simulationDuration = SIMULATION_DURATION;
			startInstant = Instant.parse(START_INSTANT);
			break;
		case MIL_RT_SIMULATION:
			architectureURI = GlobalSupervisor.MIL_SIM_ARCHITECTURE_URI;
			fanLocalSimulatorURI = FanStateModel.MIL_RT_URI;
			fanUserLocalSimulatorURI = FanUserModel.MIL_RT_URI;
//			heaterLocalSimulatorURI = HeaterCoupledModel.MIL_RT_URI;
//			meterLocalSimulatorURI = ElectricMeterCoupledModel.MIL_RT_URI;
			accelerationFactor = ACCELERATION_FACTOR;
			unixEpochStartTimeInMillis =
					System.currentTimeMillis() + DELAY_TO_START_SIMULATION;
			simulationDuration = SIMULATION_DURATION;
			startInstant = Instant.parse(START_INSTANT);
			break;
		case SIL_SIMULATION:
			architectureURI = GlobalSupervisor.SIL_SIM_ARCHITECTURE_URI;
			fanLocalSimulatorURI = FanStateModel.SIL_URI;
			fanUserLocalSimulatorURI = "not-used";
//			heaterLocalSimulatorURI = HeaterCoupledModel.SIL_URI;
//			meterLocalSimulatorURI = ElectricMeterCoupledModel.SIL_URI;
			accelerationFactor = ACCELERATION_FACTOR;
			unixEpochStartTimeInMillis =
					System.currentTimeMillis() + DELAY_TO_START_SIMULATION;
			simulationDuration = SIMULATION_DURATION;
			startInstant = Instant.parse(START_INSTANT);
			break;
		case INTEGRATION_TEST:
			accelerationFactor = ACCELERATION_FACTOR;
			unixEpochStartTimeInMillis =
					System.currentTimeMillis() + DELAY_TO_START;
			startInstant = Instant.parse(START_INSTANT);
			break;
		case STANDARD:
		default:
		}

		AbstractComponent.createComponent(
				Fan.class.getCanonicalName(),
				new Object[]{Fan.REFLECTION_INBOUND_PORT_URI,
							Fan.INBOUND_PORT_URI,
							 CURRENT_EXECUTION_TYPE,
							 architectureURI,
							 fanLocalSimulatorURI,
							 accelerationFactor});
		AbstractComponent.createComponent(
				FanUser.class.getCanonicalName(),
				new Object[]{FanUser.REFLECTION_INBOUND_PORT_URI,
							 Fan.INBOUND_PORT_URI,
							 CURRENT_EXECUTION_TYPE,
							 architectureURI,
							 fanUserLocalSimulatorURI,
							 accelerationFactor});
//		AbstractComponent.createComponent(
//				Heater.class.getCanonicalName(),
//				new Object[]{Heater.REFLECTION_INBOUND_PORT_URI,
//							 Heater.USER_INBOUND_PORT_URI,
//							 Heater.INTERNAL_CONTROL_INBOUND_PORT_URI,
//							 Heater.EXTERNAL_CONTROL_INBOUND_PORT_URI,
//							 Heater.SENSOR_INBOUND_PORT_URI,
//							 Heater.ACTUATOR_INBOUND_PORT_URI,
//							 CURRENT_EXECUTION_TYPE,
//							 architectureURI,
//							 heaterLocalSimulatorURI,
//							 accelerationFactor,
//							 CLOCK_URI});
//		AbstractComponent.createComponent(
//				HeaterController.class.getCanonicalName(),
//				new Object[]{Heater.SENSOR_INBOUND_PORT_URI,
//							 Heater.ACTUATOR_INBOUND_PORT_URI,
//							 HeaterController.STANDARD_HYSTERESIS,
//							 HeaterController.STANDARD_CONTROL_PERIOD,
//							 CONTROL_MODE,
//							 CURRENT_EXECUTION_TYPE,
//							 CLOCK_URI});
//		AbstractComponent.createComponent(
//				HeaterUser.class.getCanonicalName(),
//				new Object[]{Heater.USER_INBOUND_PORT_URI,
//							 Heater.INTERNAL_CONTROL_INBOUND_PORT_URI,
//							 Heater.EXTERNAL_CONTROL_INBOUND_PORT_URI,
//							 Heater.SENSOR_INBOUND_PORT_URI,
//							 Heater.ACTUATOR_INBOUND_PORT_URI,
//							 CURRENT_EXECUTION_TYPE,
//							 CLOCK_URI});
//		AbstractComponent.createComponent(
//				ElectricMeter.class.getCanonicalName(),
//				new Object[]{ElectricMeter.REFLECTION_INBOUND_PORT_URI,
//							 ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI,
//							 CURRENT_EXECUTION_TYPE,
//							 architectureURI,
//							 meterLocalSimulatorURI,
//							 accelerationFactor});
//		AbstractComponent.createComponent(
//				HEM.class.getCanonicalName(),
//				new Object[]{CURRENT_EXECUTION_TYPE});

//		if (CURRENT_EXECUTION_TYPE.isIntegrationTest()) {
//			AbstractComponent.createComponent(
//				ElectricMeterUnitTester.class.getCanonicalName(),
//				new Object[]{});
//		}

		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[]{
						// URI of the clock to retrieve it
						CLOCK_URI,
						// start time in Unix epoch time
						TimeUnit.MILLISECONDS.toNanos(
										 		unixEpochStartTimeInMillis),
						// start instant synchronised with the start time
						startInstant,
						accelerationFactor});

		if (CURRENT_EXECUTION_TYPE.isSimulated()) {
			AbstractComponent.createComponent(
					GlobalCoordinator.class.getCanonicalName(),
					new Object[]{});
			AbstractComponent.createComponent(
					GlobalSupervisor.class.getCanonicalName(),
					new Object[]{CURRENT_EXECUTION_TYPE,
								 architectureURI,
								 unixEpochStartTimeInMillis,
								 simulatedStartTime,
								 simulationDuration,
								 SIMULATION_TIME_UNIT,
								 accelerationFactor});
		}

		super.deploy();
	}

	/**
	 * start the execution.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param args	commend-line arguments.
	 */
	public static void	main(String[] args)
	{
		try {
			CVMGlobalTest cvm = new CVMGlobalTest();
			// compute the execution duration in milliseconds from the
			// simulation duration in hours and the acceleration factor
			// i.e., the simulation duration times 3600 seconds per hour
			// times 1000 milliseconds per second divided by the acceleration
			// factor
			long executionDuration = 0L;
			long sleepDuration = 100000L;
			switch (CURRENT_EXECUTION_TYPE) {
			case MIL_SIMULATION:
				executionDuration = DELAY_TO_START_SIMULATION + 3000L;
				break;
			case MIL_RT_SIMULATION:
			case SIL_SIMULATION:
				executionDuration = ((long)
						(((double)SIMULATION_TIME_UNIT.toMillis(1))*
								(SIMULATION_DURATION/ACCELERATION_FACTOR)))
					+ DELAY_TO_START_SIMULATION + 2000L;
				break;
			case INTEGRATION_TEST:
				executionDuration =
						DELAY_TO_START +
							((long)(TimeUnit.SECONDS.toMillis(1)
											* (660.0/ACCELERATION_FACTOR)))
																	+ 2000L;
				break;
			case STANDARD:
			default:
			}
			System.out.println("starting for " + executionDuration);
			cvm.startStandardLifeCycle(executionDuration);
			// delay to look at the results before closing the trace windows
			Thread.sleep(sleepDuration);
			// force the exit
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
