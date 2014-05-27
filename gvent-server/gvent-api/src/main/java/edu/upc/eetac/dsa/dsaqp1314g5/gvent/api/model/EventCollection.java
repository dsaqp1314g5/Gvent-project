package edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.EventResource;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.MediaType;




public class EventCollection {
	@InjectLinks({
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "create-event", title = "Create event", type = MediaType.GVENT_API_EVENT),
		@InjectLink(value = "/events?before={before}", style = Style.ABSOLUTE, rel = "previous", title = "Previous events", type = MediaType.GVENT_API_EVENT_COLLECTION, bindings = { @Binding(name = "before", value = "${instance.oldestTimestamp}") }),
		@InjectLink(value = "/events?after={after}", style = Style.ABSOLUTE, rel = "current", title = "Newest events", type = MediaType.GVENT_API_EVENT_COLLECTION, bindings = { @Binding(name = "after", value = "${instance.newestTimestamp}") }) })

	private List<Link> links;
	private List<Event> events;
	private long newestTimestamp;
	private long oldestTimestamp;
	
	public EventCollection() {
		super();
		events = new ArrayList<>();
	}
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
	public void addEvent(Event event) {
		events.add(event);
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

}
