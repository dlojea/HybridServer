package es.uvigo.esei.dai.hybridserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PagesMap {
	
	Map<String, String> pages;
	
	public PagesMap (Map<String, String> pages) {
		this.pages = pages;
	}
	
	public String getPage (String uuid) {
		return pages.get(uuid);
	}
	
	public List<String> getList(){
		return new ArrayList<>(pages.keySet());	
	}
	
	public boolean containsPage (String uuid) {
		return pages.containsKey(uuid);
	}
	
	public void putPage (String uuid, String content) {
		this.pages.put(uuid, content);
	}
	
	public void deletePage(String uuid) {
		this.pages.remove(uuid);
	}

}
