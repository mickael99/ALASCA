package fr.sorbonne_u.components.hem2023.equipements.waterHeating.measures;

import fr.sorbonne_u.components.hem2023.utils.MeasureI;

public interface		WaterHeaterMeasureI
extends		MeasureI
{
	default boolean		isStateMeasure()		{ return false; }
	default boolean		isTemperatureMeasures()	{ return false; }
}

//-----------------------------------------------------------------------------

