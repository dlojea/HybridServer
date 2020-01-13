package es.uvigo.esei.dai.hybridserver;

import java.util.List;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.dao.HtmlDAO;
import es.uvigo.esei.dai.hybridserver.dao.XmlDAO;
import es.uvigo.esei.dai.hybridserver.dao.XsdDAO;
import es.uvigo.esei.dai.hybridserver.dao.XsltDAO;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.HybridServerService", serviceName = "HybridServerService")

public class DefaultHybridServerService implements HybridServerService {
	
	private HtmlDAO htmlDAO;
	private XmlDAO xmlDAO;
	private XsdDAO xsdDAO;
	private XsltDAO xsltDAO;

	public DefaultHybridServerService(HtmlDAO htmlDAO, XmlDAO xmlDAO, XsdDAO xsdDAO, XsltDAO xsltDAO) {
		this.htmlDAO = htmlDAO;
		this.xmlDAO = xmlDAO;
		this.xsdDAO = xsdDAO;
		this.xsltDAO = xsltDAO;
	}

	@Override
	public List<String> listHtml() {
		return htmlDAO.list();
	}

	@Override
	public List<String> listXml() {
		return xmlDAO.list();
	}

	@Override
	public List<String> listXsd() {
		return xsdDAO.list();
	}

	@Override
	public List<String> listXslt() {
		return xsltDAO.list();
	}

	@Override
	public String getUuidXsd(String UuidXslt) {
		return xsltDAO.getXsd(UuidXslt);
	}

	@Override
	public String getHtml(String uuid) {
		return htmlDAO.get(uuid);
	}

	@Override
	public String getXml(String uuid) {
		return xmlDAO.get(uuid);
	}

	@Override
	public String getXsd(String uuid) {
		return xsdDAO.get(uuid);
	}

	@Override
	public String getXslt(String uuid) {
		return xsltDAO.get(uuid);
	}

}
