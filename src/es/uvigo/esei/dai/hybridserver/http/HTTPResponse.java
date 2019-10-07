package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HTTPResponse {
	
	private HTTPResponseStatus status;
	private String version;
	private String content;
	private Map<String, String> parameters;
	
	public HTTPResponse() {
		parameters = new LinkedHashMap<>();
	}

	public HTTPResponseStatus getStatus() {
		return this.status;
	}

	public void setStatus(HTTPResponseStatus status) {
		this.status = status;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
		parameters.put("Content-Length", Integer.toString(content.length()));
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public String putParameter(String name, String value) {
		return this.parameters.put(name, value);
	}

	public boolean containsParameter(String name) {
		return this.parameters.containsKey(name);
	}

	public String removeParameter(String name) {
		return this.parameters.remove(name);
	}

	public void clearParameters() {
		this.parameters.clear();
	}

	public List<String> listParameters() {
		return (List<String>) this.parameters.values();
	}

	public void print(Writer writer) throws IOException {
		
		writer.write(this.getVersion() + " ");
		writer.write(String.format("%d", this.getStatus().getCode()) + " ");
		writer.write(this.getStatus().getStatus());
		writer.write("\r\n");
		
		Iterator<String> headers = parameters.keySet().iterator();
		String key;
		
		while (headers.hasNext()) {
			key = headers.next();
			writer.write(key + ": " + parameters.get(key) + "\r\n");
		}
		
		writer.write("\r\n");
		if(this.content != null) {
			writer.write(this.content);
		}
		writer.flush();
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
