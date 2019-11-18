package es.uvigo.esei.dai.hybridserver.dao;

import java.util.List;

public interface HtmlDAO {
	
	public String get (String uuid);
	public List<String> list();
	public boolean contains (String uuid);
	public void create (String uuid, String content);
	public void delete (String uuid);

}
