package edu.upc.eetac.dsa.dsaqp1314g5.gvent.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model.GventRootAPI;

@Path("/")
public class GventRootAPIResource {
	@GET
	public GventRootAPI getRootAPI() {
		GventRootAPI api = new GventRootAPI();
		return api;
	}
}