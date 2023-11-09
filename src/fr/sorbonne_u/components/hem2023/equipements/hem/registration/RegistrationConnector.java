package fr.sorbonne_u.components.hem2023.equipements.hem.registration;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.bases.RegistrationCI;

public class RegistrationConnector extends AbstractConnector implements RegistrationCI {

	@Override
	public boolean registered(String uid) throws Exception {
		System.out.println("valeur de uid dans le connecteur " + uid);
		return ((RegistrationCI)this.offering).registered(uid);
	}

	@Override
	public boolean register(String uid, String controlPortURI, String path2xmlControlAdapter) throws Exception {
		return ((RegistrationCI)this.offering).register(uid, controlPortURI, path2xmlControlAdapter);
	}

	@Override
	public void unregister(String uid) throws Exception {
		((RegistrationCI)this.offering).unregister(uid);
	}

}
