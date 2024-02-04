package fr.sorbonne_u.components.hem2023.equipements.waterHeating.connectors;


import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.equipements.waterHeating.interfaces.WaterHeaterActuatorCI;

public class			WaterHeaterActuatorConnector
extends		AbstractConnector
implements	WaterHeaterActuatorCI
{

	@Override
	public void			startHeating() throws Exception
	{
		((WaterHeaterActuatorCI)this.offering).startHeating();
	}

	@Override
	public void			stopHeating() throws Exception
	{
		((WaterHeaterActuatorCI)this.offering).stopHeating();
	}
}
//-----------------------------------------------------------------------------

