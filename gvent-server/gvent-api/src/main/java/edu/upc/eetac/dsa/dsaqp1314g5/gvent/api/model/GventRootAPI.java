package edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.GventRootAPIResource;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.MediaType;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.EventResource;

public class GventRootAPI {
	@InjectLinks({
		@InjectLink(resource = GventRootAPIResource.class, style = Style.ABSOLUTE, rel = "self bookmark home", title = "Gvent Root API", method = "getRootAPI"),
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "events", title = "Latest events", type = MediaType.GVENT_API_EVENT_COLLECTION),
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "create-event", title = "Latest events", type = MediaType.GVENT_API_EVENT) })
	private List<Link> links;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
}