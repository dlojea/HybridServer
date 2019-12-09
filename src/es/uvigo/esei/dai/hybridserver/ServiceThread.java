package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Properties;

import es.uvigo.esei.dai.hybridserver.controller.Controller;
import es.uvigo.esei.dai.hybridserver.controller.HtmlController;
import es.uvigo.esei.dai.hybridserver.controller.XmlController;
import es.uvigo.esei.dai.hybridserver.controller.XsdController;
import es.uvigo.esei.dai.hybridserver.controller.XsltController;
import es.uvigo.esei.dai.hybridserver.dao.HtmlDAO;
import es.uvigo.esei.dai.hybridserver.dao.XmlDAO;
import es.uvigo.esei.dai.hybridserver.dao.XsdDAO;
import es.uvigo.esei.dai.hybridserver.dao.XsltDAO;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class ServiceThread implements Runnable {
	
	private Socket socket;
	private Properties properties;
	private HTTPResponse response;
	private String [] resources = {"html","xml","xsd","xslt"};
	private Controller controller;
	
	public ServiceThread (Socket socket, Properties properties) {
		this.socket = socket;
		response = new HTTPResponse();
		this.properties = properties;	
	}
	
	public void setResponse (String content, String type, HTTPResponseStatus status) {
		if (content != null) {
			response.setContent(content);
			response.putParameter("Content-Type", type);
		}
		
		response.setStatus(status);
		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
	}
	
	public void setResponse (HTTPResponseStatus status) {
		response.setStatus(status);
		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
	}

	@Override
	public void run() {
		try (Socket socket = this.socket) {
			
			HTTPRequest request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
			System.out.print(request.toString() + "\n");
			String resourceName = request.getResourceName();
			
			if (Arrays.asList(resources).contains(resourceName)) {
				try {
					switch (resourceName) {
						case "html":
							controller = new HtmlController (new HtmlDAO (properties));
							break;
						case "xml":
							controller = new XmlController (new XmlDAO (properties));
							break;
						case "xsd":
							controller = new XsdController (new XsdDAO (properties));
							break;
						case "xslt":
							controller = new XsltController (new XsltDAO (properties));
							break;
					}
					controller.setResponse(request);
					setResponse(controller.getContent(), controller.getType(), controller.getStatus());
					
				} catch (Exception e) {
					setResponse(HTTPResponseStatus.S500);
				}
				
			} else if (resourceName.isEmpty()) {
				setResponse(
					"<html><h1> Hybrid Server</h1><br/>" +
					"Autores: Daniel L&oacute;pez Ojea y Jordan Oreiro Vieites<br/>" +
					"<a href=\"/html\"> Lista </a></html>", 
					MIME.TEXT_HTML.getMime(), 
					HTTPResponseStatus.S200
				);
		    } else {
				setResponse(HTTPResponseStatus.S400);
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
