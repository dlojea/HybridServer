package es.uvigo.esei.dai.hybridserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HtmlMapDAO implements HtmlDAO{
	
	private Map<String, String> pages;
	
	public HtmlMapDAO (Map<String, String> pages) {
		this.pages = pages;
	}
	
	@Override
	public String get (String uuid) {
		return pages.get(uuid);
	}
	
	@Override
	public List<String> list() {	
		return new ArrayList<String>(this.pages.keySet());
	}
	
	@Override
	public boolean contains (String uuid) {
		return pages.containsKey(uuid);
	}
	
	@Override
	public void create (String uuid, String content) {
		this.pages.put(uuid, content);
	}
	
	@Override
	public void delete (String uuid) {
		this.pages.remove(uuid);
	}

}
