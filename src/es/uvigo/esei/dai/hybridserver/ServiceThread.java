package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class ServiceThread implements Runnable {
	
	private Socket socket;
	private HtmlDAO pages;
	private HTTPResponse response;
	
	public ServiceThread (Socket socket, HtmlDAO pages) {
		this.socket = socket;
		this.pages = pages;
		response = new HTTPResponse();
	}
	
	public ServiceThread (Socket socket, Properties properties) {
		this.socket = socket;
		this.pages = new HtmlDBDAO(properties);
		response = new HTTPResponse();
		
	}
	
	public void setResponse (String content, String type, HTTPResponseStatus status) {
		if (content != null) {
			response.setContent(content);
			response.putParameter("Content-Type", type);
		}
		
		response.setStatus(status);
		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
	}

	@Override
	public void run() {
		try (Socket socket = this.socket) {
			
			HTTPRequest request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
			System.out.print(request.toString() + "\n");
			
			
			String resourceName = request.getResourceName();
			
			
			response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
			
			if (resourceName.equals("html")) {
				try {
					HtmlController controller = new HtmlController(pages);
					controller.setResponse(request);
					this.setResponse(controller.getContent(), controller.getType(), controller.getStatus());
				} catch (Exception e) {
					response.setStatus(HTTPResponseStatus.S500);
				}
				
			} else if (resourceName.isEmpty()) {
				response.setContent("<html><h1> Hybrid Server</h1><br/>"
									+ "Autores: Daniel L&oacute;pez Ojea y Jordan Oreiro Vieites<br/>"
									+ "<a href=\"/html\"> Lista </a></html>");
				response.setStatus(HTTPResponseStatus.S200);
				response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
		    } else {
				response.setStatus(HTTPResponseStatus.S400);
			}
		
			response.print(new OutputStreamWriter(socket.getOutputStream()));
			System.out.print(response.toString() + "\n\n");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HTTPParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
