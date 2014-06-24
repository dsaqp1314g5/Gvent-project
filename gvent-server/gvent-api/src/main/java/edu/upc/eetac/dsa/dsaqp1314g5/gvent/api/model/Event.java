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




public class Event {
	@InjectLinks({
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "events", title = "Latest events", type = MediaType.GVENT_API_EVENT_COLLECTION),
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "Event", type = MediaType.GVENT_API_EVENT, method = "getEvent", bindings = @Binding(name = "eventId", value = "${instance.id}")),
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "comments", title = "Comments", type = MediaType.GVENT_API_COMMENT_COLLECTION, method = "getComments", bindings = @Binding(name = "eventId", value = "${instance.id}")),
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "create-comment", title = "Comment", type = MediaType.GVENT_API_COMMENT, method = "createComment", bindings = @Binding(name = "eventId", value = "${instance.id}"))})
	private List<Link> links;
	private int id;
	private String title;
	private String coordX;
	private String coordY;
	private String category;
	private String description;
	private String owner;
	private String state;
	private boolean publicEvent;
	private long creationDate;
	private Date eventDate;
	private int popularity;
	
	
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCoordX() {
		return coordX;
	}
	public void setCoordX(String coordX) {
		this.coordX = coordX;
	}
	public String getCoordY() {
		return coordY;
	}
	public void setCoordY(String coordY) {
		this.coordY = coordY;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public boolean isPublicEvent() {
		return publicEvent;
	}
	public void setPublicEvent(boolean publicEvent) {
		this.publicEvent = publicEvent;
	}
	public long getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
	public Date getEventDate() {
		return eventDate;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public int getPopularity() {
		return popularity;
	}
	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}
	

}
