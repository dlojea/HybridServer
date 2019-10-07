package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
	
	private Socket socket;
	private PagesMap pages;
	
	public ServiceThread(Socket socket, PagesMap pages) {
		this.socket = socket;
		this.pages = pages;
	}

	@Override
	public void run() {
		try (Socket socket = this.socket){
			
			HTTPRequest request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
			HTTPResponse response = new HTTPResponse();
			System.out.print(request.toString() + "\n");
			
			HTTPRequestMethod method = request.getMethod();
			Map<String, String> resourceParameters = request.getResourceParameters();
			String resourceName = request.getResourceName();
			String uuid;
			
			response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
			
			if(resourceName.equals("html")) {
				switch (method) {
					case GET:
						
						uuid = resourceParameters.get("uuid");
						
						if (uuid == null) {
							response.setContent(pages.getList().toString());
							response.setStatus(HTTPResponseStatus.S200);
						} else {
							if (pages.containsPage(uuid)) {
								response.setContent(pages.getPage(uuid));
								response.setStatus(HTTPResponseStatus.S200);
							} else {
								response.setStatus(HTTPResponseStatus.S404);
							}
						}
						break;
						
					case POST:
						
						uuid = UUID.randomUUID().toString();
						
						if (resourceParameters.containsKey("html")) {
							pages.putPage(uuid, resourceParameters.get("html"));
							response.setStatus(HTTPResponseStatus.S200);
							response.setContent("<a href=\"html?uuid="+ uuid +"\">"+ uuid +"</a>");
						} else {
							response.setStatus(HTTPResponseStatus.S400);
						}
						break;
						
					case DELETE:
						
						uuid = resourceParameters.get("uuid");
						
						if (pages.containsPage(uuid)) {
							pages.deletePage(uuid);
							response.setStatus(HTTPResponseStatus.S200);
						} else {
							response.setStatus(HTTPResponseStatus.S404);
						}
						break;
						
					default:
						break;
				}
			} else if (resourceName.isEmpty()) {
				response.setContent("Hybrid Server");
				response.setStatus(HTTPResponseStatus.S200);
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
