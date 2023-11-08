package fr.sorbonne_u.components.hem2023.equipements.hem.registration;

public interface RegistrationI {
	public boolean registered(String uid) throws Exception;
	public boolean register(String uid,String controlPortURI,String path2xmlControlAdapter) throws Exception;
	public void	unregister(String uid) throws Exception;
}
