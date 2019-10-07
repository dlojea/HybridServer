package es.uvigo.esei.dai.hybridserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PagesMap {
	
	Map<String, String> pages;
	
	public PagesMap(Map<String, String> pages) {
		this.pages = pages;
	}
	
	public String getPage(String uuid) {
		return pages.get(uuid);
	}
	
	public List<String> getList(){
		return new ArrayList<>(pages.keySet());	
	}
	
	

}
