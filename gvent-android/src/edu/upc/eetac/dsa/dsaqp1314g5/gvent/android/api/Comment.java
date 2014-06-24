package edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Comment {
	private Map<String, Link> links = new HashMap<String, Link>();
	private String id;
	private String username;
	private String eventId;
	private String comment;
	private long lastModified;
	
	public Map<String, Link> getLinks() {
		return links;
	}
	public void setLinks(Map<String, Link> links) {
		this.links = links;
	}
	public String getId() {
		return id;
	}
	public void setId(String string) {
		this.id = string;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
}
