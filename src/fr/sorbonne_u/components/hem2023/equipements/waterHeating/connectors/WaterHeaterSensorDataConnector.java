package fr.sorbonne_u.components.hem2023.equipements.waterHeating.connectors;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2023.utils.Measure;

import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterSensorDataCI;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.measures.WaterHeaterSensorData;

public class			WaterHeaterSensorDataConnector
extends		DataConnector
implements	WaterHeaterSensorDataCI.WaterHeaterSensorRequiredPullCI
{
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public WaterHeaterSensorData<Measure<Boolean>>	heatingPullSensor()
	throws Exception
	{
		return ((WaterHeaterSensorDataCI.WaterHeaterSensorOfferedPullCI)this.offering).heatingPullSensor();
	}

	@Override
	public WaterHeaterSensorData<Measure<Double>>	targetTemperaturePullSensor()
	throws Exception
	{
		return ((WaterHeaterSensorDataCI.WaterHeaterSensorOfferedPullCI)this.offering).targetTemperaturePullSensor();
	}

	@Override
	public WaterHeaterSensorData<Measure<Double>>	currentTemperaturePullSensor()
	throws Exception
	{
		return ((WaterHeaterSensorDataCI.WaterHeaterSensorOfferedPullCI)this.offering).currentTemperaturePullSensor();
	}

	@Override
	public void			startTemperaturesPushSensor(
		long controlPeriod,
		TimeUnit tu
		) throws Exception
	{
		((WaterHeaterSensorDataCI .WaterHeaterSensorOfferedPullCI)this.offering).
								startTemperaturesPushSensor(controlPeriod, tu);
	}
}
