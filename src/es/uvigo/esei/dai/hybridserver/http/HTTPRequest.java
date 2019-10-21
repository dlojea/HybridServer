package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPRequest {
	
	private HTTPRequestMethod method; // Puede ser GET, POST, PUT, DELETE, OPTIONS, TRACE, CONNECT, HEAD, aunque en este proyecto solo utilizaremos los cuatro primeros.
	private String resourceChain; // Recurso solicitado + parametros
	private String [] resourcePath; // Contiene el path con cada directorio en una posición del array.
	private String resourceName; // Nombre del recurso solicitado (sin los parámetros): por ejemplo, en una petición para /index.php?param1=value1, el nombre del recurso será index.php.
	private Map<String, String> resourceParameters; // Parámetros de la consulta: en el caso de GET formarán parte del rescurso solicitado y en el caso de POST formarán parte del recurso solicitado o del cuerpo de la petición.
	private String httpVersion;
	private Map<String, String> headerParameters; // Parámetros de la cabecera: se encuentran después de la primera línea y siguen el formato Cabecera: Valor. La cabecera finaliza cuando hay una línea en blanco.
	private String content;
	private int contentLength;
	
	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
		
		resourcePath = new String[0];
		resourceParameters = new LinkedHashMap<>();
		headerParameters = new LinkedHashMap<>();
		
		BufferedReader buffer = new BufferedReader(reader);
		
		String line = buffer.readLine();
		String [] field = line.split(" "); //Separa lo valor de la primera linea por espacios
		
		try {	
			this.method = HTTPRequestMethod.valueOf(field[0]);
			this.resourceChain = field[1];
			this.httpVersion = field[2];
			
		} catch (IllegalArgumentException e) {
			throw new HTTPParseException(e.getLocalizedMessage());
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new HTTPParseException(e.getLocalizedMessage());
		}
		
		String [] resource = this.resourceChain.split("\\?"); // Separa la resourceChain por una ? quedando el recurso y los parámetros separados
		this.resourceName = resource[0].substring(1); // El nombre del recurso va antes de la ?
		if (!this.resourceName.equals("")) {
			this.resourcePath = this.resourceName.split("/"); // Se almacena en cada posicion del array cada directorio
		}
		
		try {
			// Se almacenan las cabeceras hasta que se encuentre una linea vacia que implica final de cabecera
			while (!(line = buffer.readLine()).equals("")) { 
				
				String [] headerParameters = line.split(":"); // Separa cabecera de valor por un : 
				String key = headerParameters[0].trim(); 
				String value = headerParameters[1].trim();
				
				this.headerParameters.put(key, value);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new HTTPParseException(e.getLocalizedMessage());
		}	
		
		// Si resourceChain es diferente a la primera posicion de resource significa que contiene parámetros
		if (resource[0] != this.resourceChain) { 
			
			String[] resourceParameters = resource[1].split("&"); // Separa cada parámetro por una &
			for (int i = 0; i < resourceParameters.length; i++) {
				String[] parameter = resourceParameters[i].split("="); // Separa la clave del valor por un =
	
				String key = parameter[0];
				String value = parameter[1];
	
				this.resourceParameters.put(key, value);
			} 
		}
		
		// Si existe el parametro Content-Length en la cabecera es que existe contenido
		if (this.headerParameters.containsKey("Content-Length")) { 
			
			this.contentLength = Integer.parseInt(this.headerParameters.get("Content-Length"));
			
			char[] array_content = new char[this.contentLength]; // Un array de char de la misma longitud del contenido
			buffer.read(array_content);
			String content = new String (array_content);
			
			String decoded = URLDecoder.decode(content, "UTF-8"); // Decodifica el string usando UTF-8
			this.content = decoded;
			
			String[] resourceParameters = this.content.split("&");
			for (int i = 0; i < resourceParameters.length; i++) {
				String[] parameter = resourceParameters[i].split("=");
	
				String key = parameter[0];
				String value = parameter[1];
	
				this.resourceParameters.put(key, value);
			} 
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
