package es.uvigo.esei.dai.hybridserver.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import es.uvigo.esei.dai.hybridserver.HybridServerService;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.dao.HtmlDAO;
import es.uvigo.esei.dai.hybridserver.http.*;

public class HtmlController implements Controller{
	
	private HtmlDAO pages;
	private List<ServerConfiguration> servers;
	private String content;
	private String type;
	private HTTPResponseStatus status;
	
	public HtmlController(HtmlDAO pages) {
		this.pages = pages;
	}

	public HtmlController(HtmlDAO pages, List<ServerConfiguration> servers) {
		this.pages = pages;
		this.servers = servers;
	}
	
	@Override
	public void setResponse(HTTPRequest request) {
		HTTPRequestMethod method = request.getMethod();
		Map<String, String> resourceParameters = request.getResourceParameters();
		String uuid;
		
		switch (method) {
		
			case GET:
				
				uuid = resourceParameters.get("uuid");
				
				//Lista todas las paginas
				if (uuid == null) {
					
					//Busca en local
					StringBuilder sb = new StringBuilder();
					sb.append("<html><h1>Local Server</h1>");
					sb.append("<ul>");
					for(String page: pages.list()) {
						sb.append("<li><a href=\"/html?uuid="+ page +"\">"+ page +"</a></li>");
					}
					sb.append("</ul>");
					
					//Busca en el resto de servidores
					if (servers != null) {
						for (ServerConfiguration server : servers) {
							try {
								HybridServerService webService = getWebService(server);
								if (webService != null) {
									sb.append("<h1>" + server.getName() + "</h1>");
									sb.append("<ul>");
									for (String page : webService.listHtml()) {
										sb.append("<li><a href=\"/html?uuid="+ page +"\">"+ page +"</a></li>");
									}
									sb.append("</ul>");
								}
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
					sb.append("</html>");
					
					content = sb.toString();
					type = MIME.TEXT_HTML.getMime();
					status = HTTPResponseStatus.S200;
					
				//Muestra una pagina en concreto
				} else {
					
					//Busca en local
					if (pages.contains(uuid)) {
						content = pages.get(uuid);
						type =  MIME.TEXT_HTML.getMime();
						status = HTTPResponseStatus.S200;
						
					//Busca en el resto de servidores	
					} else if (servers != null) {
						status = HTTPResponseStatus.S404;
						for (ServerConfiguration server : servers) {
							try {
								HybridServerService webService = getWebService(server);
								if (webService != null) {
									if (webService.getHtml(uuid) != null) {
										content = webService.getHtml(uuid);
										type =  MIME.TEXT_HTML.getMime();
										status = HTTPResponseStatus.S200;
										break;
									}
								}
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					//Si no se encuentra
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
	
	private HybridServerService getWebService(ServerConfiguration server) throws MalformedURLException {
		URL url = new URL(server.getWsdl());
		QName name = new QName(server.getNamespace(), server.getService());
		try {
			Service service = Service.create(url, name);
			HybridServerService webService = service.getPort(HybridServerService.class);
			return webService;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
}
