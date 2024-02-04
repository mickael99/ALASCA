package fr.sorbonne_u.components.hem2023.equipements.waterHeating.measures;


import fr.sorbonne_u.components.hem2023.utils.CompoundMeasure;
import fr.sorbonne_u.components.hem2023.utils.Measure;
import fr.sorbonne_u.components.hem2023.utils.MeasureI;
import fr.sorbonne_u.components.hem2023.utils.MeasurementUnit;

import fr.sorbonne_u.exceptions.PreconditionException;

public class			WaterHeaterCompoundMeasure
extends		CompoundMeasure
implements	WaterHeaterMeasureI
{
	private static final long serialVersionUID = 1L;
	protected static final int	TARGET_TEMPERATURE_INDEX = 0;
	protected static final int	CURRENT_TEMPERATURE_INDEX = 1;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public			WaterHeaterCompoundMeasure(
		Measure<Double> targetTemperature,
		Measure<Double> currentTemperature
		)
	{
		super(new MeasureI[]{targetTemperature, currentTemperature});

		assert	targetTemperature.getData() == this.getTargetTemperature() :
				new PreconditionException(
						"targetTemperature.getData() == "
						+ "getCurrentTemperature()");
		assert	targetTemperature.getMeasurementUnit() ==
								this.getTargetTemperatureMeasurementUnit() :
				new PreconditionException(
						"targetTemperature.getMeasurementUnit() == "
						+ "getTargetTemperatureMeasurementUnit()");
		assert	currentTemperature.getData() == this.getCurrentTemperature() :
				new PreconditionException(
						"currentTemperature.getData() == "
						+ "getCurrentTemperature()");
		assert	currentTemperature.getMeasurementUnit() ==
								this.getCurrentTemperatureMeasurementUnit() :
				new PreconditionException(
						"currentTemperature.getMeasurementUnit() == "
						+ "getCurrentTemperatureMeasurementUnit()");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean		isTemperatureMeasures()
	{
		return true;
	}

	@SuppressWarnings("unchecked")
	public double		getTargetTemperature()
	{
		return ((Measure<Double>)
						this.getMeasure(TARGET_TEMPERATURE_INDEX)).getData();
	}

	@SuppressWarnings("unchecked")
	public MeasurementUnit	getTargetTemperatureMeasurementUnit()
	{
		return ((Measure<Double>)
						this.getMeasure(TARGET_TEMPERATURE_INDEX)).
														getMeasurementUnit();
	}

	@SuppressWarnings("unchecked")
	public double		getCurrentTemperature()
	{
		return ((Measure<Double>)
						this.getMeasure(CURRENT_TEMPERATURE_INDEX)).getData();
	}

	@SuppressWarnings("unchecked")
	public MeasurementUnit	getCurrentTemperatureMeasurementUnit()
	{
		return ((Measure<Double>)
						this.getMeasure(CURRENT_TEMPERATURE_INDEX)).
														getMeasurementUnit();
	}
}
//-----------------------------------------------------------------------------

