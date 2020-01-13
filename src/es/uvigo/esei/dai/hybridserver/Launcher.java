package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class Launcher {
	public static void main(String[] args) {
			
		//Si no hay ningun argumento arranca con la configuracion por defecto
		if (args.length == 0 ) {
			new HybridServer().start();
			
		//Si hay un argumento lo valida con el XSD y arranca con esa configuracion
		} else if (args.length == 1) {
			
			InputStream xmlStream = null;
			InputStream xsdStream = null;
			
			try {
				xmlStream = new FileInputStream(args[0]);
				xsdStream = new FileInputStream("./configuration.xsd");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Configuration conf = new Configuration();
			XMLConfigurationLoader xmlLoader = new XMLConfigurationLoader();
	
			if (validateAgainstXSD(xmlStream, xsdStream)) {
				try {
					File xml = new File(args[0]);
					conf = xmlLoader.load(xml);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				new HybridServer(conf).start();
			} else {
				System.out.println("Error: fallo al validar el archivo de configuracion");
			}
		
		//Si hay mas de un argumento da error
		} else {
			System.out.println("Error: solo puede haber un parámetro");
		}
		
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
