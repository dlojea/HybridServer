package es.uvigo.esei.dai.hybridserver;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class HybridServer {
	private static final int SERVICE_PORT = 8888;
	private Thread serverThread;
	private boolean stop;
	private PagesDAO pages;
	private int numClients;
	private int port;
	private Properties properties;

	public HybridServer() {
		this.numClients = 50;
		this.port = 8888;
		this.properties = new Properties();
		this.properties.put("db.url", "jdbc:mysql://localhost:3306/hstestdb");
		this.properties.put("db.user", "hsdb");
		this.properties.put("db.password", "hsdbpass");
	}
	
	public HybridServer(Map<String, String> pages) {
		this.pages = new PagesMapDAO(pages);
	}

	public HybridServer(Properties properties) {
		this.numClients = Integer.parseInt(properties.getProperty("numClients", "50"));
		this.port = Integer.parseInt(properties.getProperty("port", "8888"));
		this.properties = properties;
	}

	public int getPort() {
		return SERVICE_PORT;
	}
	
	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(port)) {
					ExecutorService threadPool = Executors.newFixedThreadPool(numClients);
					while (true) {
						Socket socket = serverSocket.accept();
						
						if (stop) break;
						//threadPool.execute(new ServiceThread(socket, pages));	
						threadPool.execute(new ServiceThread(socket, properties));					
							
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
		
		this.serverThread = null;
	}
}
