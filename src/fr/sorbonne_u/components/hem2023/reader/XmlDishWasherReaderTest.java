package fr.sorbonne_u.components.hem2023.reader;


import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class XmlDishWasherReaderTest {
	
	@Test
	public void testUid() throws Exception {
		XmlReader xml = new XmlReader("dishwasher-descriptor.xml");
		String uid = xml.getUid();
		assert uid.equals("DishWasher");
	}
	
	@Test
	public void testOffered() throws Exception {
		XmlReader xml = new XmlReader("dishwasher-descriptor.xml");
		String offered = xml.getOffered();
		assert offered.equals("fr.sorbonne_u.components.hem2023.equipements.dishWasher.interfaces.DishWasherInternalControlCI");
	}
	
	@Test
	public void testConsumption() throws Exception {
		XmlReader xml = new XmlReader("dishwasher-descriptor.xml");
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map = xml.getConsumption();
		assert map.get("nominal") ==  50;	
		assert map.get("min") == 45;
		assert map.get("max") == 60;
	}
	
	@Test
	public void testInstanceVar() throws Exception {
		XmlReader xml = new XmlReader("dishwasher-descriptor.xml");
		ArrayList<Attribute> attributes = xml.getInstanceVar();
		ArrayList<Attribute> res = new ArrayList<Attribute>();
		
		res.add(new Attribute("protected static final", "int", "MAX_MODE", "3"));
		res.add(new Attribute("protected", "boolean", "suspended", "false"));
		res.add(new Attribute("protected", "int", "currentMode", "0"));
		
		assert res.size() == attributes.size();
				
		for(int i = 0; i < res.size(); i++)
			assert res.get(i).equals(attributes.get(i));
	}
  	
	@Test
	public void printMethode() throws Exception {
		XmlReader xml = new XmlReader("dishwasher-descriptor.xml");
		ArrayList<Methode> methodes = xml.getMethode();
		System.out.println(methodes.get(3));
	}
}
