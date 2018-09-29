package haifa.university.dmoz;

public class DmozLink {
	public DmozLink(long catid, String topic, String type, String resource, String title) {
		super();
		this.catid = catid;
		this.topic = topic;
		this.type = type;
		this.resource = resource;
		this.title = title;
	}
	public long getCatid() {
		return catid;
	}
	public void setCatid(long catid) {
		this.catid = catid;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	long catid;
	String topic;
	String type;
	String resource;
	String title;
}
