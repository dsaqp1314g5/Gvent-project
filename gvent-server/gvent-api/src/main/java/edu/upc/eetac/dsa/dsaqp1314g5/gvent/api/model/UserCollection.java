package edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.MediaType;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.UserResource;




public class UserCollection {
	@InjectLinks({
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "create-user", title = "Create user", type = MediaType.GVENT_API_USER),
		@InjectLink(value = "/users?before={before}", style = Style.ABSOLUTE, rel = "previous", title = "Previous users", type = MediaType.GVENT_API_USER_COLLECTION, bindings = { @Binding(name = "before", value = "${instance.oldestTimestamp}") }),
		@InjectLink(value = "/users?after={after}", style = Style.ABSOLUTE, rel = "current", title = "Newest users", type = MediaType.GVENT_API_USER_COLLECTION, bindings = { @Binding(name = "after", value = "${instance.newestTimestamp}") }) })

	private List<Link> links;
	private List<User> users;
	private long newestTimestamp;
	private long oldestTimestamp;

	public UserCollection() {
		super();
		users = new ArrayList<>();
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public long getNewestTimestamp() {
		return newestTimestamp;
	}

	public void setNewestTimestamp(long newestTimestamp) {
		this.newestTimestamp = newestTimestamp;
	}

	public long getOldestTimestamp() {
		return oldestTimestamp;
	}

	public void setOldestTimestamp(long oldestTimestamp) {
		this.oldestTimestamp = oldestTimestamp;
	}
	
	public void addUser(User user) {
		users.add(user);
	}


}
