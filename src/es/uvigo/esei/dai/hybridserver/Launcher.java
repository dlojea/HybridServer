package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;

public class Launcher {
	public static void main(String[] args) {
		
		HybridServer hs = null;
		
		File xml = new File("./configuration.xml");
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
			hs = new HybridServer(conf);
		} else {
			System.out.println("Error de validacion del fichero de configuración");
		}
		
		hs.start();
		
		
		// Si no contiene archivo de propiedades crea un servidor por defecto
//		if (args.length == 0 ) {
//			
//			new HybridServer().start();
//			
//		} else if (args.length == 1) {
//			
//			Properties properties = new Properties();
//			try (InputStream inStream = new FileInputStream(args[0])) {
//				properties.load(inStream);
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			if (hasAllProperties(properties)) {
//				new HybridServer(properties).start();
//			} else {
//				System.out.println("Error: faltan propiedades");
//			}
//			
//		} else {
//			System.out.println("Error: solo puede haber un parámetro");
//		}
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
