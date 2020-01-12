package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class Launcher {
	public static void main(String[] args) {
		
		File xml = new File("./configuration.xml");
		
		//Comprueba si existe el archivo xml de configuracion
		if (xml.exists()) {
			InputStream xmlStream = null;
			InputStream xsdStream = null;
			try {
				xmlStream = new FileInputStream("./configuration.xml");
				xsdStream = new FileInputStream("./configuration.xsd");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Configuration conf = new Configuration();
			XMLConfigurationLoader xmlLoader = new XMLConfigurationLoader();
	
			if (validateAgainstXSD(xmlStream, xsdStream)) {
				try {
					conf = xmlLoader.load(xml);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				new HybridServer(conf).start();
			} else {
				System.out.println("Error: fallo al validar el archivo de configuracion");
			}
			
		//Si no existe el archivo de configuracion comprueba si se le ha pasado uno de propiedades com argumento
		} else if (args.length == 1) {
			
			Properties properties = new Properties();
			try (InputStream inStream = new FileInputStream(args[0])) {
				properties.load(inStream);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (hasAllProperties(properties)) {
				new HybridServer(properties).start();
			} else {
				System.out.println("Error: faltan propiedades");
			}
			
		//Si no hay archivo de configuracion de propiedades comienza con valores por defecto
		} else if (args.length == 0 ) {
			new HybridServer().start();
			
		//Si hay mas de un argumento da error
		} else {
			System.out.println("Error: solo puede haber un parámetro");
		}
		
	}
	
	private static boolean hasAllProperties(Properties properties) {
		String[] propertiesNames = {"numClients","port","db.url","db.user","db.password"};
		
		for (String property: propertiesNames) {
			// Comprueba si no existe la propiedad o si su valor es vacío
			if (properties.getProperty(property) == null || properties.getProperty(property).isEmpty()) {
				
				return false;
			}
		}
		return true;
	}
		
	private static boolean validateAgainstXSD(InputStream xml, InputStream xsd) {
	    try {
	        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema = factory.newSchema(new StreamSource(xsd));
	        Validator validator = schema.newValidator();
	        validator.validate(new StreamSource(xml));
	        return true;
	    } catch(Exception ex) {
	        return false;
	    }
	}
	
}
