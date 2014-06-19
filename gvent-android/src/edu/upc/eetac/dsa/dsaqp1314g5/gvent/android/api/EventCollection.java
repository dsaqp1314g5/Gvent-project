package edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventCollection {
	
	private Map<String, Link> links = new HashMap<String,Link>();
	private List<Event> events = new ArrayList<Event>();;
	private long newestTimestamp;
	private long oldestTimestamp;

	public Map<String, Link> getLinks() {
		return links;
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
