package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
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
			
			Map<String, String> resourceParameters = request.getResourceParameters();
			String uuid = resourceParameters.get("uuid");
			
			if (uuid == null) {
				response.setStatus(HTTPResponseStatus.S200);
				response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
				response.setContent(pages.getList().toString());
			} else {
				String content = pages.getPage(uuid);
				
				response.setContent(content);
				response.setStatus(HTTPResponseStatus.S200);
				response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
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
