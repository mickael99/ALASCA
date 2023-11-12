package fr.sorbonne_u.components.hem2023.classCreator;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2023.bases.AdjustableCI;
import fr.sorbonne_u.components.hem2023.reader.Attribute;
import fr.sorbonne_u.components.hem2023.reader.Methode;
import fr.sorbonne_u.components.hem2023.reader.Parameter;
import fr.sorbonne_u.components.hem2023.reader.XmlReader;
import javassist.*;


public class ClassCreator {
	protected XmlReader xmlReader;
	
	public ClassCreator(String pathToXmlFile) throws Exception {
		this.xmlReader = new XmlReader(pathToXmlFile);
	}	
	
	public int getModifierAsInteger(String modifier) {
		int res = 0;
		if(modifier.contains("public"))
			res |= Modifier.PUBLIC;
		if(modifier.contains("private"))
			res |= Modifier.PRIVATE;
		if(modifier.contains("protected"))
			res |= Modifier.PROTECTED;
		if(modifier.contains("static"))
			res |= Modifier.STATIC;
		if(modifier.contains("final"))
			res |= Modifier.FINAL;
		
		return res;
	}
	
	public Class<?> createClass() throws Exception {
		ClassPool pool = ClassPool.getDefault() ;
		
		//création de la classe
		CtClass cs = pool.get(AbstractConnector.class.getCanonicalName()) ;
		CtClass cii = pool.get(AdjustableCI.class.getCanonicalName()) ;
		CtClass oi = pool.get(Class.forName(xmlReader.getOffered()).getCanonicalName()) ;
		CtClass ctClass = pool.makeClass(xmlReader.getUid() + "Connector");	

		// constructeur sans paramètres avec un corps vide
		CtConstructor constructor = CtNewConstructor.make("public " + ctClass.getName() + "() { }", ctClass);
		ctClass.addConstructor(constructor);
		
		//superclass et interface
		ctClass.setSuperclass(cs);
		ctClass.setInterfaces(new CtClass[]{cii});
		
		//consommation
		HashMap<String, Integer> consumption = xmlReader.getConsumption();
		
		//attributs 
		ArrayList<Attribute> attributeArray = xmlReader.getInstanceVar();
		for(Attribute attribute : attributeArray) {
			CtClass ctFieldType = pool.get(attribute.getType());
			CtField field = new CtField(ctFieldType, attribute.getName(), ctClass);
			field.setModifiers(getModifierAsInteger(attribute.getModifiers()));
			ctClass.addField(field, attribute.getInitValue());
		}
		
		//methodes 
		ArrayList<Methode> methodeArray = xmlReader.getMethode();
		for(Methode methode : methodeArray) {
			//signature
			String source = "";
			source += methode.getModifiers() + " " + 
						methode.getType() + " " + 
						methode.getName();
			source += "(";
			for(int i = 0; i < methode.getParameters().size(); i++) {
				Parameter parameter = methode.getParameters().get(i);
				source += parameter.getType() + " " + parameter.getName();
				if(i < (methode.getParameters().size() - 1))
					source += ", ";
			}
			source += ") ";
			
			//throws
			if(!methode.getThrown().equals("")) 
				source += "throws " + methode.getThrown();
			source += " {\n";
			
			//body
			String offeredPackage = xmlReader.getOffered();
			String[] offeredPackageSplit = offeredPackage.split("\\.");
			String offered = offeredPackageSplit[offeredPackageSplit.length - 1];
			
			String newBody = methode.getBody().replace(offered, offeredPackage);
			source += newBody + "\n}\n\n";
			
			//ajout de la methode dans la classe
			CtMethod newMethod = CtNewMethod.make(source, ctClass);
			ctClass.addMethod(newMethod);
		}
		
        
		//free
		cii.detach(); 
		cs.detach(); 
		oi.detach();
		
		/**CustomClassLoader customClassLoader = new CustomClassLoader(this.getClass().getClassLoader());
	    byte[] classBytes = ctClass.toBytecode();
	    Class<?> ret = customClassLoader.defineClass(ctClass.getName(), classBytes); */
		
	    Class<?> ret = ctClass.toClass();
	    
		ctClass.writeFile(".");

	    // Décharge ctClass
	    ctClass.detach();

	    return ret;
	}
}
