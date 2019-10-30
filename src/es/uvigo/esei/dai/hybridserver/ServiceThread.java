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
	private PagesDAO pages;
	
	public ServiceThread (Socket socket, PagesDAO pages) {
		this.socket = socket;
		this.pages = pages;
	}
	
	public ServiceThread (Socket socket, Properties properties) {
		this.socket = socket;
		this.pages = new PagesDBDAO(properties);
		
	}

	@Override
	public void run() {
		try (Socket socket = this.socket){
			
			HTTPRequest request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
			HTTPResponse response = new HTTPResponse();
			//System.out.print(request.toString() + "\n");
			
			HTTPRequestMethod method = request.getMethod();
			Map<String, String> resourceParameters = request.getResourceParameters();
			String resourceName = request.getResourceName();
			String uuid;
			
			response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
			
			if(resourceName.equals("html")) {
				try {
					switch (method) {
					
						case GET:
							
							uuid = resourceParameters.get("uuid");
							
							if (uuid == null) {
								StringBuilder sb = new StringBuilder();
								sb.append("<h1>Local Server</h1>");
								sb.append("<ul>");
								for(String page: pages.list()) {
									sb.append("<li><a href=\"/html?uuid="+ page +"\">"+ page +"</a></li>");
								}
								sb.append("</ul>");
								
								response.setContent(sb.toString());
								response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
								response.setStatus(HTTPResponseStatus.S200);
							} else {
								if (pages.contains(uuid)) {
									response.setContent(pages.get(uuid));
									response.setStatus(HTTPResponseStatus.S200);
									response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
								} else {
									response.setStatus(HTTPResponseStatus.S404);
								}
							}
							break;
							
						case POST:
							
							uuid = UUID.randomUUID().toString();
							
							if (resourceParameters.containsKey("html")) {
								pages.create(uuid, resourceParameters.get("html"));
								response.setStatus(HTTPResponseStatus.S200);
								response.setContent("<a href=\"html?uuid=" + uuid + "\">" + uuid + "</a>");
								
							} else {
								response.setStatus(HTTPResponseStatus.S400);
							}
							break;
							
						case DELETE:
							
							uuid = resourceParameters.get("uuid");
							
							if (pages.contains(uuid)) {
								pages.delete(uuid);
								response.setStatus(HTTPResponseStatus.S200);
							} else {
								response.setStatus(HTTPResponseStatus.S404);
							}
							break;
							
						default:
							break;
					
					}
				} catch (Exception e){
					response.setStatus(HTTPResponseStatus.S500);
				}
				
			} else if (resourceName.isEmpty()) {
				response.setContent("<h1> Hybrid Server</h1><br/>"
									+ "Autores: Daniel L&oacute;pez Ojea y Jordan Oreiro Vieites<br/>"
									+ "<a href=\"/html\"> Lista </a>");
				response.setStatus(HTTPResponseStatus.S200);
				response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
		    } else {
				response.setStatus(HTTPResponseStatus.S400);
			}
		
			response.print(new OutputStreamWriter(socket.getOutputStream()));
			//System.out.print(response.toString() + "\n\n");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HTTPParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
