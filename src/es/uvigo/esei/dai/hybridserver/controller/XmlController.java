package es.uvigo.esei.dai.hybridserver.controller;

import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.dao.XmlDAO;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class XmlController implements Controller {
	
	private XmlDAO pages;
	private String content;
	private String type;
	private HTTPResponseStatus status;

	public XmlController(XmlDAO pages) {
		this.pages = pages;
	}

	@Override
	public void setResponse(HTTPRequest request) {
		HTTPRequestMethod method = request.getMethod();
		Map<String, String> resourceParameters = request.getResourceParameters();
		String uuid;
		
		switch (method) {
			case GET:
				
				uuid = resourceParameters.get("uuid");
				
				if (uuid == null) {
					StringBuilder sb = new StringBuilder();
					sb.append("<xml><h1>Local Server</h1>");
					sb.append("<ul>");
					for(String page: pages.list()) {
						sb.append("<li><a href=\"/xml?uuid="+ page +"\">"+ page +"</a></li>");
					}
					sb.append("</ul></xml>");
					
					content = sb.toString();
					type = MIME.APPLICATION_XML.getMime();
					status = HTTPResponseStatus.S200;
				} else {
					if (pages.contains(uuid)) {
						content = pages.get(uuid);
						type =  MIME.APPLICATION_XML.getMime();
						status = HTTPResponseStatus.S200;
					} else {
						status = HTTPResponseStatus.S404;
					}
				}
				break;
			case POST:
				uuid = UUID.randomUUID().toString();
				
				if (resourceParameters.containsKey("xml")) {
					pages.create(uuid, resourceParameters.get("xml"));
					status = HTTPResponseStatus.S200;
					content = "<xml><a href=\"xml?uuid=" + uuid + "\">" + uuid + "</a></xml>";
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

	@Override
	public String getContent() {
		return content;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public HTTPResponseStatus getStatus() {
		return status;
	}

}
