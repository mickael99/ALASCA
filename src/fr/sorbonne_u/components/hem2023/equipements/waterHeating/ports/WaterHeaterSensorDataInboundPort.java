package fr.sorbonne_u.components.hem2023.equipements.waterHeating.ports;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.measures.WaterHeaterCompoundMeasure;

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

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.WaterHeater;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterSensorDataCI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterUserAndExternalControlI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.measures.WaterHeaterSensorData;
import fr.sorbonne_u.components.hem2023.utils.Measure;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>HeaterSensorDataInboundPort</code>
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
 * <p>Created on : 2023-11-27</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			WaterHeaterSensorDataInboundPort
extends		AbstractDataInboundPort
implements	WaterHeaterSensorDataCI.WaterHeaterSensorOfferedPullCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create the inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof HeaterInternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public				WaterHeaterSensorDataInboundPort(ComponentI owner)
	throws Exception
	{
		super(WaterHeaterSensorDataCI.WaterHeaterSensorOfferedPullCI.class,
			  DataOfferedCI.PushCI.class, owner);

		assert	owner instanceof WaterHeaterUserAndExternalControlI:
				new PreconditionException(
						"owner instanceof HeaterInternalControlI");
	}

	/**
	 * create the inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof HeaterInternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			unique identifier of the port.
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public				WaterHeaterSensorDataInboundPort(
		String uri, 
		ComponentI owner
		) throws Exception
	{
		super(uri, WaterHeaterSensorDataCI.WaterHeaterSensorOfferedPullCI.class,
			  DataOfferedCI.PushCI.class, owner);

		assert	owner instanceof WaterHeaterUserAndExternalControlI :
				new PreconditionException(
						"owner instanceof HeaterInternalControlI");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterSensorDataCI.HeaterSensorCI#heatingPullSensor()
	 */
	@Override
	public WaterHeaterSensorData<Measure<Boolean>>	heatingPullSensor()
	throws Exception
	{
		return this.getOwner().handleRequest(
							o -> ((WaterHeater)o).heatingPullSensor());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterSensorDataCI.HeaterSensorCI#targetTemperaturePullSensor()
	 */
	@Override
	public WaterHeaterSensorData<Measure<Double>>	targetTemperaturePullSensor()
	throws Exception
	{
		return this.getOwner().handleRequest(
							o -> ((WaterHeater)o).targetTemperaturePullSensor());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterSensorDataCI.HeaterSensorCI#currentTemperaturePullSensor()
	 */
	@Override
	public WaterHeaterSensorData<Measure<Double>>	currentTemperaturePullSensor()
	throws Exception
	{
		return this.getOwner().handleRequest(
							o -> ((WaterHeater)o).currentTemperaturePullSensor());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterSensorDataCI.HeaterSensorCI#startTemperaturesPushSensor(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void			startTemperaturesPushSensor(
		long controlPeriod,
		TimeUnit tu
		) throws Exception
	{
		this.getOwner().handleRequest(
			o -> { ((WaterHeater)o).startTemperaturesPushSensor(controlPeriod, tu);
					return null;
				 });
	}

	/**
	 * @see fr.sorbonne_u.components.interfaces.DataOfferedCI.PullCI#get()
	 */
	@Override
	public DataOfferedCI.DataI		get() throws Exception
	{
		return new WaterHeaterSensorData<HeaterCompoundMeasure>(
					new WaterHeaterCompoundMeasure(
							this.targetTemperaturePullSensor().getMeasure(),
							this.currentTemperaturePullSensor().getMeasure()));
	}
}
// -----------------------------------------------------------------------------
