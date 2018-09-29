package haifa.university.dmoz;

public class Category {
	private int index;
	private int catid;
	private String name;
	private String path;
	private String title;
	private int hierarchy_level;
	private String description;
	
	public Category() {
		super();
	}
	public Category(int index, int catid, String name, String title, int hierarchy_level, String description) {
		super();
		this.index = index;
		this.catid = catid;
		this.name = name;
		this.title = title;
		this.hierarchy_level = hierarchy_level;
		this.description = description;
	}
	public Category(int index, int catid, String name, String title, int hierarchy_level) {
		this(index, catid, name, title, hierarchy_level, null);
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getCatid() {
		return catid;
	}
	public void setCatid(int catid) {
		this.catid = catid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getHierarchy_level() {
		return hierarchy_level;
	}
	public void setHierarchy_level(int hierarchy_level) {
		this.hierarchy_level = hierarchy_level;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
