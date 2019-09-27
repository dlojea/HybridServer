package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class HTTPResponse {
	
	private HTTPResponseStatus status;
	private String version;
	private String content;
	private Map<String, String> parameters;
	
	public HTTPResponse() {
	}

	public HTTPResponseStatus getStatus() {
		return this.status;
	}

	public void setStatus(HTTPResponseStatus status) {
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public String putParameter(String name, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean containsParameter(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	public String removeParameter(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public void clearParameters() {
	}

	public List<String> listParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public void print(Writer writer) throws IOException {
	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
		}

		return writer.toString();
	}
}
