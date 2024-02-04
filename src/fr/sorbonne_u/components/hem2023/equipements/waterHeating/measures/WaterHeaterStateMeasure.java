package fr.sorbonne_u.components.hem2023.equipements.waterHeating.measures;


import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterUserAndExternalControlI.WaterHeaterState;
import fr.sorbonne_u.components.hem2023.utils.Measure;

import fr.sorbonne_u.utils.aclocks.AcceleratedClock;

public class			WaterHeaterStateMeasure
extends		Measure<WaterHeaterState>
implements	WaterHeaterMeasureI
{
	private static final long serialVersionUID = 1L;

	public			WaterHeaterStateMeasure(
		AcceleratedClock ac,
		WaterHeaterState data
		)
	{
		super(ac, data);			
	}

	public			WaterHeaterStateMeasure(WaterHeaterState data)
	{
		super(data);
	}

	@Override
	public boolean	isStateMeasure()	{ return true; }
}
//-----------------------------------------------------------------------------

