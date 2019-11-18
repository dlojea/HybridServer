package es.uvigo.esei.dai.hybridserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Launcher {
	public static void main(String[] args) {
		// Si no contiene archivo de propiedades crea un servidor por defecto
		if (args.length == 0 ) {
			
			new HybridServer().start();
			
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
}
