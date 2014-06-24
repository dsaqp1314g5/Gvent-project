package edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api;

import java.util.HashMap;
import java.util.Map;


public class GventRootAPI {

	private Map<String, Link> links;
	 
	public GventRootAPI() {
		links = new HashMap<String, Link>();
	}
 
	public Map<String, Link> getLinks() {
		return links;
	}
 
}