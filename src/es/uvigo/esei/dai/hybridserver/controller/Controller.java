package es.uvigo.esei.dai.hybridserver.controller;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public interface Controller {

	void setResponse(HTTPRequest request);
	String getContent();
	String getType();
	HTTPResponseStatus getStatus();

}