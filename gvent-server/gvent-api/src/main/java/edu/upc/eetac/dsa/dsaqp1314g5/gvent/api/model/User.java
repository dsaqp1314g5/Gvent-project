package edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model;

import java.sql.Date;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.MediaType;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.UserResource;



public class User {
	@InjectLinks({
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "users", title = "Latest users", type = MediaType.GVENT_API_USER_COLLECTION),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "user", type = MediaType.GVENT_API_USER, method = "getUser", bindings = @Binding(name = "username", value = "${instance.username}")),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "events", title = "events", type = MediaType.GVENT_API_EVENT_COLLECTION, method = "getEvents", bindings = @Binding(name = "username", value = "${instance.username}")),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "followed", title = "events-followed", type = MediaType.GVENT_API_EVENT_COLLECTION, method = "getEventsFollowed", bindings = @Binding(name = "username", value = "${instance.username}")),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "comments", title = "comments", type = MediaType.GVENT_API_COMMENT_COLLECTION, method = "getComments", bindings = @Binding(name = "username", value = "${instance.username}")),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "friends", title = "my-friends", type = MediaType.GVENT_API_USER_COLLECTION, method = "getFriends", bindings = @Binding(name = "username", value = "${instance.username}"))})
	private List<Link> links;
	private String username;
	private String userpass; 
	private String name;
	private String email;
	private long registerDate;
	
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}	
	public String getUserpass() {
		return userpass;
	}
	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(long registerDate) {
		this.registerDate = registerDate;
	}
	
}
