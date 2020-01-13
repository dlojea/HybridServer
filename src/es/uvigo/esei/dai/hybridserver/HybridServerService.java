package es.uvigo.esei.dai.hybridserver;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface HybridServerService {
	@WebMethod
	List<String> listHtml();
	@WebMethod
	List<String> listXml();
	@WebMethod
	List<String> listXsd();
	@WebMethod
	List<String> listXslt();
	@WebMethod
	String getUuidXsd(String UuidXslt);
	@WebMethod
	String getHtml(String uuid);
	@WebMethod
	String getXml(String uuid);
	@WebMethod
	String getXsd(String uuid);
	@WebMethod
	String getXslt(String uuid);
	
}