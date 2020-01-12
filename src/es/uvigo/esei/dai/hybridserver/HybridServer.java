package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HybridServer {
	private static final int SERVICE_PORT = 8888;
	private Thread serverThread;
	private boolean stop;
	private ExecutorService threadPool;
	private int numClients;
	private int port;
	private Properties properties;
	private Configuration config;
	

	public HybridServer() {
		this.numClients = 50;
		this.port = 8888;
		this.properties = new Properties();
		this.properties.put("db.url", "jdbc:mysql://localhost:3306/hstestdb");
		this.properties.put("db.user", "hsdb");
		this.properties.put("db.password", "hsdbpass");
	}

	public HybridServer(Properties properties) {
		this.numClients = Integer.parseInt(properties.getProperty("numClients"));
		this.port = Integer.parseInt(properties.getProperty("port"));
		this.properties = properties;
	}
	
	public HybridServer(Configuration config) {
		this.numClients = config.getNumClients();
		this.port = config.getHttpPort();	
		this.config = config;
	}

	public int getPort() {
		return SERVICE_PORT;
	}
	
	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(port)) {
					threadPool = Executors.newFixedThreadPool(numClients);
					while (true) {
						Socket socket = serverSocket.accept();
						
						if (stop) break;
						if(properties != null) threadPool.execute(new ServiceThread(socket, properties));	
						else if (config !=null) threadPool.execute(new ServiceThread(socket, config));	
						
							
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		this.stop = false;
		this.serverThread.start();
	}
	
	public void stop() {
		this.stop = true;
		
		try (Socket socket = new Socket("localhost", port)) {
			// Esta conexión se hace, simplemente, para "despertar" el hilo servidor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		try {
			this.serverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		threadPool.shutdownNow();
		 
		try {
		  threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
		  e.printStackTrace();
		}
		
		this.serverThread = null;
	}
}
