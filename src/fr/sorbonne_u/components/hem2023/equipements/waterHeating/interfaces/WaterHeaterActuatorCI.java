package fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface		WaterHeaterActuatorCI
extends		OfferedCI,
			RequiredCI
{
	public void			startHeating() throws Exception;

	public void			stopHeating() throws Exception;
}


