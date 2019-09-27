package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPRequest {
	
	private HTTPRequestMethod method;
	private String resourceChain;
	private String [] resourcePath;
	private String resourceName;
	private Map<String, String> resourceParameters = new LinkedHashMap<>();
	private String httpVersion;
	private Map<String, String> headerParameters = new LinkedHashMap<>();
	private String content;
	private int contentLength;
	
	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
		BufferedReader buffer = new BufferedReader(reader);
		
		String line = buffer.readLine();
		String [] field = line.split(" ");
		
		this.method = HTTPRequestMethod.valueOf(field[0]);
		this.resourceChain = field[1];
		this.httpVersion = field[2];
		
		String [] resource = this.resourceChain.split("\\?");
		this.resourceName = resource[0].substring(1);
		this.resourcePath = this.resourceName.split("/");
		
		if (resource[0] != this.resourceChain) {
			String[] resourceParameters = resource[1].split("&");
			for (int i = 0; i < resourceParameters.length; i++) {
				String[] parameter = resourceParameters[i].split("=");
	
				String key = parameter[0];
				String value = parameter[1];
	
				this.resourceParameters.put(key, value);
			} 
		}
		
		while (!(line = buffer.readLine()).equals("")) {
			
			String [] headerParameters = line.split(":");
			String key = headerParameters[0].trim();
			String value = headerParameters[1].trim();
			
			this.headerParameters.put(key, value);
			
			System.out.print(key + ": " + value);
			
		}	
	}

	public HTTPRequestMethod getMethod() {
		return this.method;
	}

	public String getResourceChain() {
		return this.resourceChain;
	}

	public String[] getResourcePath() {
		return this.resourcePath;
	}

	public String getResourceName() {
		return this.resourceName;
	}

	public Map<String, String> getResourceParameters() {
		return this.resourceParameters;
	}

	public String getHttpVersion() {
		return this.httpVersion;
	}

	public Map<String, String> getHeaderParameters() {
		return this.headerParameters;
	}

	public String getContent() {
		return this.content;
	}

	public int getContentLength() {
		return this.contentLength;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}
}
