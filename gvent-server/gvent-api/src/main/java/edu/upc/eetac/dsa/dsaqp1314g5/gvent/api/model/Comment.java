package edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model;

import java.sql.Date;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.EventResource;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.MediaType;



public class Comment {
	@InjectLinks({
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "comments", title = "Latest comments", type = MediaType.GVENT_API_COMMENT_COLLECTION, method = "getComments"),
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "comment", type = MediaType.GVENT_API_COMMENT, method = "getComment", bindings = @Binding(name = "commentId", value = "${instance.id}")) })
	private List<Link> links;
	private int id;
	private String username;
	private int eventId;
	private String comment;
	private long lastModified;
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
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