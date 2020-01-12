package es.uvigo.esei.dai.hybridserver.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

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
		String xslt;
		
		switch (method) {
			case GET:
				
				uuid = resourceParameters.get("uuid");
				xslt = resourceParameters.get("xslt");
				
				if (uuid == null) {
					StringBuilder sb = new StringBuilder();
					sb.append("<html><h1>Local Server</h1>");
					sb.append("<ul>");
					for(String page: pages.list()) {
						sb.append("<li><a href=\"/xml?uuid="+ page +"\">"+ page +"</a></li>");
					}
					sb.append("</ul></html>");
					
					content = sb.toString();
					type = MIME.APPLICATION_XML.getMime();
					status = HTTPResponseStatus.S200;
				} else if (uuid != null && xslt != null) { 
					if (pages.contains(uuid) && pages.containsTemplate(xslt)) {
						String xsd = pages.getSchema(xslt);
						String xml = pages.get(uuid);
						
						if (this.validateAgainstXSD(xml, xsd)) {
							try {
								String xsltContent = pages.getXslt(xslt);
								content = this.transformXML(xml, xsltContent);
							} catch (TransformerException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							type = MIME.TEXT_HTML.getMime();
							status = HTTPResponseStatus.S200;
						} else {
							status = HTTPResponseStatus.S400;
						}	
					} else {
						status = HTTPResponseStatus.S404;
					}
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
}
