package fr.sorbonne_u.components.hem2023.equipments.meter.sil;

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

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2023.equipements.meter.mil.ElectricMeterElectricityModel;
import fr.sorbonne_u.components.hem2023.utils.Measure;
import fr.sorbonne_u.components.hem2023.utils.MeasurementUnit;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>ElectricMeterElectricitySILModel</code> defines the SIL
 * simulation model for the electric meter electricity consumption.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Compared to the MIL model, this model uses the reference to the
 * {@code ElectricMeter} passed as a run time simulation parameter to set
 * the total power consumption in the corresponding variable in the
 * component. Hence, the sensor method in the component will just have
 * to return the latest value stored in the variable.
 * </p>
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
 * <p>Created on : 2023-11-18</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ElectricMeterElectricitySILModel
extends		ElectricMeterElectricityModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for an instance model in SIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	SIL_URI = ElectricMeterElectricityModel.class.
													getSimpleName() + "-SIL";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an <code>ElectricMeterElectricitySILModel</code> instance.
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition
	 * post	{@code true}	// no more postcondition
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do</i>.
	 */
	public				ElectricMeterElectricitySILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.meter.mil.ElectricMeterElectricityModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		// update the current consumption since the last consumption update.
		// must be done before recomputing the instantaneous intensity.
		this.updateCumulativeConsumption(elapsedTime);
		// recompute the current total intensity
		double old = this.currentPowerConsumption.getValue();
		double i = this.computePowerConsumption();
		this.currentPowerConsumption.setNewValue(i, this.getCurrentStateTime());

		// here, the difference with the MIL model; the new value is set
		// directly in the component to be retrieved by its sensor methods.
		this.ownerComponent.setCurrentPowerConsumption(
							new Measure<Double>(i, MeasurementUnit.AMPERES));
		
		if (Math.abs(old - i) > 0.000001) {
			// Tracing
			StringBuffer message =
						new StringBuffer("current power consumption: ");
			message.append(this.currentPowerConsumption.getValue());
			message.append(" at ");
			message.append(this.getCurrentStateTime());
			message.append('\n');
			this.logMessage(message.toString());
		}
	}
}
// -----------------------------------------------------------------------------
