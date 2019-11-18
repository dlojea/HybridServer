package es.uvigo.esei.dai.hybridserver;

import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.http.*;

public class HtmlController {
	
	private HtmlDAO pages;
	private String content;
	private String type;
	private HTTPResponseStatus status;

	public HtmlController(HtmlDAO pages) {
		this.pages = pages;
	}
	
	public void setResponse(HTTPRequest request) {
		HTTPRequestMethod method = request.getMethod();
		Map<String, String> resourceParameters = request.getResourceParameters();
		String uuid;
		
		switch (method) {
		
			case GET:
				
				uuid = resourceParameters.get("uuid");
				
				if (uuid == null) {
					StringBuilder sb = new StringBuilder();
					sb.append("<html><h1>Local Server</h1>");
					sb.append("<ul>");
					for(String page: pages.list()) {
						sb.append("<li><a href=\"/html?uuid="+ page +"\">"+ page +"</a></li>");
					}
					sb.append("</ul></html>");
					
					content = sb.toString();
					type = MIME.TEXT_HTML.getMime();
					status = HTTPResponseStatus.S200;
				} else {
					if (pages.contains(uuid)) {
						content = pages.get(uuid);
						type =  MIME.TEXT_HTML.getMime();
						status = HTTPResponseStatus.S200;
					} else {
						status = HTTPResponseStatus.S404;
					}
				}
				break;
				
			case POST:
				
				uuid = UUID.randomUUID().toString();
				
				if (resourceParameters.containsKey("html")) {
					pages.create(uuid, resourceParameters.get("html"));
					status = HTTPResponseStatus.S200;
					content = "<html><a href=\"html?uuid=" + uuid + "\">" + uuid + "</a></html>";
					
				} else {
					status = HTTPResponseStatus.S400;
				}
				break;
				
			case DELETE:
				
				uuid = resourceParameters.get("uuid");
				
				if (pages.contains(uuid)) {
					pages.delete(uuid);
					status = HTTPResponseStatus.S200;
				} else {
					status = HTTPResponseStatus.S404;
				}
				break;
				
			default:
				break;
		}
	}
	
	public String getContent() {
		return content;
	}
	
	public String getType() {
		return type;
	}
	
	public HTTPResponseStatus getStatus() {
		return status;
	}
	
}
