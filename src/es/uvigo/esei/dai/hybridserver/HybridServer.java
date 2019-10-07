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
	private PagesMap pages;

	public HybridServer() {
		// TODO Auto-generated constructor stub
	}
	
	public HybridServer(Map<String, String> pages) {
		this.pages = new PagesMap(pages);
	}

	public HybridServer(Properties properties) {
		// TODO Auto-generated constructor stub
	}

	public int getPort() {
		return SERVICE_PORT;
	}
	
	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
					ExecutorService threadPool = Executors.newFixedThreadPool(50);
					while (true) {
						Socket socket = serverSocket.accept();
						
						if (stop) break;
						threadPool.execute(new ServiceThread(socket, pages));							
							
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
		
		try (Socket socket = new Socket("localhost", SERVICE_PORT)) {
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
