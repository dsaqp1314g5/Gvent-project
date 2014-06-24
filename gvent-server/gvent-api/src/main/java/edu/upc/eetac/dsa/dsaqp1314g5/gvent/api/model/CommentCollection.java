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




public class CommentCollection {
	@InjectLinks({
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "create-comment", title = "Create comment", type = MediaType.GVENT_API_COMMENT) })
	private List<Link> links;
	private List<Comment> comments;
	private long newestTimestamp;
	private long oldestTimestamp;
	
	public CommentCollection() {
		super();
		comments = new ArrayList<>();
	}

	public List<Link> getLinks() {
		return links;
	}


	public void setLinks(List<Link> links) {
		this.links = links;
	}


	public void addComment(Comment comment) {
		comments.add(comment);
	}
	
	public List<Comment> getComments() {
		return comments;
	}


	public void setComments(List<Comment> comments) {
		this.comments = comments;
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