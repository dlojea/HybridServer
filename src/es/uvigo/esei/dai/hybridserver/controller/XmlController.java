package es.uvigo.esei.dai.hybridserver.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.Service;

import es.uvigo.esei.dai.hybridserver.HybridServerService;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.dao.XmlDAO;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class XmlController implements Controller {
	
	private XmlDAO pages;
	private List<ServerConfiguration> servers;
	private String content;
	private String type;
	private HTTPResponseStatus status;

	public XmlController(XmlDAO pages) {
		this.pages = pages;
	}
	
	public XmlController(XmlDAO pages, List<ServerConfiguration> servers) {
		this.pages = pages;
		this.servers = servers;
	}

	@Override
	public void setResponse(HTTPRequest request) {
		HTTPRequestMethod method = request.getMethod();
		Map<String, String> resourceParameters = request.getResourceParameters();
		String uuid;
		String xslt;

		
		switch (method) {
		
			case GET:
				
				uuid = resourceParameters.get("uuid");
				xslt = resourceParameters.get("xslt");
				
				//Lista todas las paginas
				if (uuid == null) {
					
					//Busca en local
					StringBuilder sb = new StringBuilder();
					sb.append("<html><h1>Local Server</h1>");
					sb.append("<ul>");
					for(String page: pages.list()) {
						sb.append("<li><a href=\"/xml?uuid="+ page +"\">"+ page +"</a></li>");
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
									for (String page : webService.listXml()) {
										sb.append("<li><a href=\"" + server.getHttpAddress() + "xml?uuid="+ page +"\">"+ page +"</a></li>");
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
					
				//Muestra una pagina en concreto transformada con xslt
				} else if (uuid != null && xslt != null) { 
					//Instanciar las variables para comprobar si existen en local
					String xmlContent = pages.get(uuid);
					String xsltContent = pages.getXslt(xslt);
					String xsdContent = pages.getSchema(xslt);
					//Si el xml no esta en local lo busca en el resto de servidores
					if (xmlContent == null) {

						if (servers != null) {
							for (ServerConfiguration server : servers) {
								try {
									HybridServerService webService = getWebService(server);
									if (webService != null) {
										if (webService.getXml(uuid) != null) {
											xmlContent = webService.getXml(uuid);
											break;
										}
									}
								} catch (MalformedURLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
					
					//Si el xml existe comprobamos si existen el resto
					if (xmlContent != null) {
						
						//Si el xslt no esta en local se busca en el resto de ervidores
						if (xsltContent == null) {
							if (servers != null) {
								for (ServerConfiguration server : servers) {
									try {
										HybridServerService webService = getWebService(server);
										if (webService != null) {
											
											if (webService.getXslt(xslt) != null) {
												
												xsltContent = webService.getXslt(xslt);
												break;
											}
										}
									} catch (MalformedURLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}
						//Si el xslt existe comprobamos si existe el xsd
						if (xsltContent != null) {
							
							//Si el xsd no es en local se busca en el resto de servidores
							if (xsdContent == null) {
								if (servers != null) {
									for (ServerConfiguration server : servers) {
										try {
											HybridServerService webService = getWebService(server);
											if (webService != null) {
												String uuidXsd = webService.getUuidXsd(xslt);
												
												if (uuidXsd != null && webService.getXsd(uuidXsd) != null) {
													xsdContent = webService.getXsd(uuidXsd);
													break;
												}
											}
										} catch (MalformedURLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
					
					//Si existen los tres archivos, el xml se valida con el xsd y se transforma con el xslt
					if (xmlContent != null && xsltContent != null && xsdContent != null) {
						System.out.println("---------------DEBUG-----------------------");

						if (this.validateAgainstXSD(xmlContent, xsdContent)) {
							try {
								content = this.transformXML(xmlContent, xsltContent);
								type = MIME.TEXT_HTML.getMime();
								status = HTTPResponseStatus.S200;
							} catch (TransformerException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							status = HTTPResponseStatus.S400;
						}
					} else if (xmlContent != null && xsltContent != null && xsdContent == null) {
						status = HTTPResponseStatus.S400;
					} else {
						status = HTTPResponseStatus.S404;
					}
					
				//Muestra una pagina en concreto sin xslt
				} else {
					//Busca en local
					if (pages.contains(uuid)) {
						content = pages.get(uuid);
						type =  MIME.APPLICATION_XML.getMime();
						status = HTTPResponseStatus.S200;
						
					//Busca en el resto de servidores	
					} else if (servers != null) {
						status = HTTPResponseStatus.S404;
						for (ServerConfiguration server : servers) {
							try {
								HybridServerService webService = getWebService(server);
								if (webService != null) {
									if ((content = webService.getXml(uuid)) != null) {
										type =  MIME.APPLICATION_XML.getMime();
										status = HTTPResponseStatus.S200;
										break;
									}
								}
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
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
	
	private boolean validateAgainstXSD(String xmlString, String xsdString) {
		
		InputStream xml = new ByteArrayInputStream(xmlString.getBytes());
		InputStream xsd = new ByteArrayInputStream(xsdString.getBytes());
		
	    try {
	        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema = factory.newSchema(new StreamSource(xsd));
	        Validator validator = schema.newValidator();
	        validator.validate(new StreamSource(xml));
	        return true;
	    } catch(Exception ex) {
	        return false;
	    }
	}
	
	private String transformXML(String xml, String xslt) throws TransformerException {
		
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(new StringReader(xslt)));
		StringWriter writer = new StringWriter();
		transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));

		return writer.toString();
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
