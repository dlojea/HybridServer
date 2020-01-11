package es.uvigo.esei.dai.hybridserver.controller;

import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.dao.XsltDAO;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class XsltController implements Controller {

	private XsltDAO pages;
	private String content;
	private String type;
	private HTTPResponseStatus status;

	public XsltController(XsltDAO pages) {
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
					sb.append("<xslt><h1>Local Server</h1>");
					sb.append("<ul>");
					for(String page: pages.list()) {
						sb.append("<li><a href=\"/xslt?uuid="+ page +"\">"+ page +"</a></li>");
					}
					sb.append("</ul></xslt>");
					
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
				
				if (resourceParameters.containsKey("xslt") && resourceParameters.containsKey("xsd")) {
					if (!pages.hasXsd(resourceParameters.get("xsd"))) 
						status = HTTPResponseStatus.S404;
					else {
						pages.create(uuid, resourceParameters.get("xsd"), resourceParameters.get("xslt"));
						status = HTTPResponseStatus.S200;
						content = "<xslt><a href=\"xslt?uuid=" + uuid + "\">" + uuid + "</a></xslt>";
					}
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
