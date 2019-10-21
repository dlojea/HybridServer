package es.uvigo.esei.dai.hybridserver;

import java.util.List;

public interface PagesDAO {
	
	public String get (String uuid);
	public List<String> list();
	public boolean contains (String uuid);
	public void create (String uuid, String content);
	public void delete (String uuid);

}
