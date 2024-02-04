package fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2023.equipements.waterHeating.measures.WaterHeaterSensorData;
import fr.sorbonne_u.components.hem2023.utils.Measure;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface		WaterHeaterSensorDataCI
extends		DataOfferedCI,
			DataRequiredCI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	public static interface	WaterHeaterSensorCI
	extends		OfferedCI,
				RequiredCI
	{
		public WaterHeaterSensorData<Measure<Boolean>>	heatingPullSensor()
				throws Exception;

		public WaterHeaterSensorData<Measure<Double>>	targetTemperaturePullSensor()
				throws Exception;

		public WaterHeaterSensorData<Measure<Double>>	currentTemperaturePullSensor()
				throws Exception;

		public void			startTemperaturesPushSensor(
			long controlPeriod,
			TimeUnit tu
			) throws Exception;
	}

	public static interface		WaterHeaterSensorRequiredPullCI
	extends		WaterHeaterSensorCI,
				DataRequiredCI.PullCI
	{
	}

	public static interface		WaterHeaterSensorOfferedPullCI
	extends		WaterHeaterSensorCI,
				DataOfferedCI.PullCI
	{
	}
}
