package es.uvigo.esei.dai.hybridserver;

public interface PagesDAO {
	
	public String getPage (String uuid);
	public String getList();
	public boolean containsPage (String uuid);
	public void putPage (String uuid, String content);
	public void deletePage(String uuid);

}
