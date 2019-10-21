package es.uvigo.esei.dai.hybridserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Launcher {
	public static void main(String[] args) {
		// Si no contiene archivo de propiedades crea un servidor por defecto
		if (args.length == 0 ) {
			
			HybridServer server = new HybridServer();
			server.start();
			
		} else if (args.length == 1) {
			
			Properties properties = new Properties();
			try (InputStream inStream = new FileInputStream(args[0])) {
				properties.load(inStream);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			HybridServer server = new HybridServer(properties);
			server.start();
			
		} else {
			System.out.println("Error: solo puede haber un parámetro");
		}
	}
}
