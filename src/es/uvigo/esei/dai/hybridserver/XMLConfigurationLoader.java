/**
 *  HybridServer
 *  Copyright (C) 2017 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLConfigurationLoader {
	
	private int http;
	private String webservice;
	private int numClients;
	
	private String user;
	private String password;
	private String url;
	
	private List<ServerConfiguration> servers = new ArrayList<ServerConfiguration>();
	private Configuration config;
	
	public Configuration load(File xmlFile) throws Exception {   
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		 
		Document document = builder.parse(xmlFile);
		
		Element connections = (Element) document.getElementsByTagName("connections").item(0);
		
		http = Integer.parseInt(connections.getElementsByTagName("http").item(0).getTextContent());
		webservice = connections.getElementsByTagName("webservice").item(0).getTextContent();
		numClients = Integer.parseInt(connections.getElementsByTagName("numClients").item(0).getTextContent());
		
		Element database = (Element) document.getElementsByTagName("database").item(0);
		
		user = database.getElementsByTagName("user").item(0).getTextContent();
		password = database.getElementsByTagName("password").item(0).getTextContent();
		url = database.getElementsByTagName("url").item(0).getTextContent();
		
		NodeList serversNode = (NodeList) document.getElementsByTagName("server");
		
		for (int i = 0; i < serversNode.getLength(); i++) {
			Element server = (Element) serversNode.item(i);
			
			String name = server.getAttribute("name");
			String wsdl = server.getAttribute("wsdl");
			String namespace = server.getAttribute("namespace");
			String service = server.getAttribute("service");
			String httpAddress = server.getAttribute("httpAddress");
			
			if (name == "" | wsdl == "" | namespace == "" | service == "" | httpAddress == "") {
				throw new java.lang.Exception();
			} else {
				ServerConfiguration serverConf = new ServerConfiguration(name,wsdl,namespace,service,httpAddress);
				servers.add(serverConf);
			}
		}
		
		config = new Configuration(http,numClients,webservice,user,password,url,servers);
		
		return config;
	}
}
