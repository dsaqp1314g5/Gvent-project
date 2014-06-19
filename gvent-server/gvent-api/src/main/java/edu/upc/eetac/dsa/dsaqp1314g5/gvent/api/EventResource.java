package edu.upc.eetac.dsa.dsaqp1314g5.gvent.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model.Comment;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model.CommentCollection;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model.Event;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model.EventCollection;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model.User;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model.UserCollection;

@Path("/events")
public class EventResource {

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.GVENT_API_EVENT_COLLECTION)
	public EventCollection getEvents(@QueryParam("sort") String sort, @QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after) {
		EventCollection events = new EventCollection();
		if(sort==null) sort="last";
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			boolean updateFromLast = after > 0;
			//stmt = conn.prepareStatement(buildGetEventsQuery(updateFromLast)); //////////// FALTA ORDERNAR POR SORT
			if(sort.equals("last")){
				stmt = conn.prepareStatement(buildGetEventsQuery(updateFromLast));
			}else if(sort.equals("popular")){
				stmt = conn.prepareStatement(buildGetEventsQueryPopular(updateFromLast));
			}
			if (updateFromLast) {
				stmt.setTimestamp(1, new Timestamp(after));
			} else {
				if (before > 0)
					stmt.setTimestamp(1, new Timestamp(before));
				else
					stmt.setTimestamp(1, null);
				length = (length <= 0) ? 20 : length;
				stmt.setInt(2, length);
			}
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Event event = new Event();
				event.setId(rs.getInt("id"));
				event.setTitle(rs.getString("title"));
				event.setCoordX(rs.getString("coord_x"));
				event.setCoordY(rs.getString("coord_y"));
				event.setCategory(rs.getString("category"));
				event.setDescription(rs.getString("description"));
				event.setOwner(rs.getString("owner"));
				event.setState(rs.getString("state"));
				event.setPublicEvent(rs.getBoolean(9));
				event.setCreationDate(rs.getTimestamp("creation_date")
						.getTime());
				event.setEventDate(rs.getDate(11));
				event.setPopularity(rs.getInt("popularity"));
				oldestTimestamp = rs.getTimestamp("creation_date").getTime();
				if (first) {
					first = false;
					events.setNewestTimestamp(event.getCreationDate());
				}
				events.addEvent(event);
			}
			events.setOldestTimestamp(oldestTimestamp);
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return events;
	}

	private String buildGetEventsQuery(boolean updateFromLast) {
		if (updateFromLast)
			return "SELECT * FROM events WHERE creation_date > ? ORDER BY creation_date DESC";
		else
			return "SELECT * FROM events WHERE creation_date < ifnull(?, now()) ORDER BY creation_date DESC LIMIT ?";
	}
	
	private String buildGetEventsQueryPopular(boolean updateFromLast) {
		if (updateFromLast)
			return "SELECT * FROM events WHERE creation_date > ? ORDER BY popularity DESC";
		else
			return "SELECT * FROM events WHERE creation_date < ifnull(?, now()) ORDER BY popularity DESC LIMIT ?";
	}

	@GET
	@Path("/{eventId}")
	@Produces(MediaType.GVENT_API_EVENT)
	public Response getEvent(@PathParam("eventId") String eventId,
			@Context Request request) {
		// Create CacheControl
		CacheControl cc = new CacheControl();

		Event event = getEventFromDatabase(eventId);

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Long.toString(event.getCreationDate()));

		// Verify if it matched with etag available in http request
		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);

		// If ETag matches the rb will be non-null;
		// Use the rb to return the response without any further processing
		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}

		// If rb is null then either it is first time request; or resource is
		// modified
		// Get the updated representation and return with Etag attached to it
		rb = Response.ok(event).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	private Event getEventFromDatabase(String eventId) {
		Event event = new Event();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(buildGetEventByIdQuery());
			stmt.setInt(1, Integer.valueOf(eventId));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {event.setId(rs.getInt("id"));
			event.setTitle(rs.getString("title"));
			event.setCoordX(rs.getString("coord_x"));
			event.setCoordY(rs.getString("coord_y"));
			event.setCategory(rs.getString("category"));
			event.setDescription(rs.getString("description"));
			event.setOwner(rs.getString("owner"));
			event.setState(rs.getString("state"));
			event.setPublicEvent(rs.getBoolean(9));
			event.setCreationDate(rs.getTimestamp("creation_date")
					.getTime());
			event.setEventDate(rs.getDate(11));
			event.setPopularity(rs.getInt("popularity"));
			} else {
				throw new NotFoundException("There's no event with id="
						+ eventId);
			}

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return event;
	}

	private String buildGetEventByIdQuery() {
		return "SELECT * FROM events WHERE id = ?";
	}

	@POST
	@Consumes(MediaType.GVENT_API_EVENT)
	@Produces(MediaType.GVENT_API_EVENT)
	public Event createEvent(Event event) {
		// validateSting(Event); VALIDARRRRRRR
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			String sql = buildInsertEvent();
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			// stmt.setString(1, security.getUserPrincipal().getName()); QUE
			// OBTENGA EL USERNAME DEL LOGEADO
			stmt.setString(1, event.getTitle());
			stmt.setString(2, event.getCoordX());
			stmt.setString(3, event.getCoordY());
			stmt.setString(4, event.getCategory());
			stmt.setString(5, event.getDescription());
			stmt.setString(6, event.getOwner());// stmt.setString(1,
												// security.getUserPrincipal().getName());
			stmt.setString(7, event.getState());
			stmt.setBoolean(8, event.isPublicEvent());
			stmt.setDate(9, event.getEventDate());
			stmt.setInt(10, event.getPopularity());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int eventId = rs.getInt(1);

				event = getEventFromDatabase(Integer.toString(eventId));
			} else {
				// Something has failed...
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return event;
	}

	private String buildInsertEvent() {
		return "INSERT INTO events(title, coord_x, coord_y, category, description, owner, state, public, event_date, popularity) value (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}

	@GET //// SI SE BUSCA UN TTITULO CON ESPACIOS DEVUELTE TODOS LOS QUE TENGAN ESPACIOS :S:S:S
	@Path("/search")
	@Produces(MediaType.GVENT_API_EVENT_COLLECTION)
	public EventCollection searchEvent(@QueryParam("category") String category,
			@QueryParam("title") String title, @QueryParam("length") int length) {

		EventCollection events = new EventCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;

		try {

			stmt = conn.prepareStatement(searchGetEventByIdQuery());
			if(category != null){
				stmt.setString(2, category);
			}else{
				stmt.setString(2, "%''%");
			}
			if(title != null){
				stmt.setString(1, "%" + title + "%");
			}else{
				stmt.setString(1, "%''%");
			}
			length = (length <= 0) ? 10 : length;

			stmt.setInt(3, length);
			ResultSet rs = stmt.executeQuery();
			
			long oldestTimestamp = 0;
			while (rs.next()) {
				Event event = new Event();
				event.setId(rs.getInt("id"));
				event.setTitle(rs.getString("title"));
				event.setCoordX(rs.getString("coord_x"));
				event.setCoordY(rs.getString("coord_y"));
				event.setCategory(rs.getString("category"));
				event.setDescription(rs.getString("description"));
				event.setOwner(rs.getString("owner"));
				event.setState(rs.getString("state"));
				event.setPublicEvent(rs.getBoolean(9));
				event.setPopularity(rs.getInt("popularity"));
				event.setCreationDate(rs.getTimestamp("creation_date")
						.getTime());
				event.setEventDate(rs.getDate(11));
				oldestTimestamp = rs.getTimestamp("creation_date").getTime();
				events.addEvent(event);
			}
			events.setOldestTimestamp(oldestTimestamp);
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return events;

	}
	
	private String searchGetEventByIdQuery() {
		
		return "SELECT * FROM events WHERE title like ? or category = ? LIMIT ?";
	}

	
	@PUT
	@Path("/{eventId}")
	@Consumes(MediaType.GVENT_API_EVENT)
	@Produces(MediaType.GVENT_API_EVENT)
	public Event updateEvent(@PathParam("eventId") String eventId, Event event) {
		// VALIDACIONES
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(buildUpdateEvent());
			stmt.setString(1, event.getTitle());
			stmt.setString(2, event.getCoordX());
			stmt.setString(3, event.getCoordY());
			stmt.setString(4, event.getCategory());
			stmt.setString(5, event.getDescription());
			stmt.setString(6, event.getOwner());
			stmt.setString(7, event.getState());
			stmt.setBoolean(8, event.isPublicEvent());
			stmt.setDate(9, event.getEventDate());
			stmt.setInt(10, event.getPopularity());
			stmt.setInt(11, Integer.valueOf(eventId));
			int rows = stmt.executeUpdate();
			if (rows == 1)
				event = getEventFromDatabase(eventId);
			else {
				throw new NotFoundException("There is no event with ID = "
						+ eventId);
			}

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return event;
	}
	
	private String buildUpdateEvent() {
		return "UPDATE events SET title=ifnull(?, title), coord_x=ifnull(?, coord_x), coord_y=ifnull(?, coord_y), category=ifnull(?, category), description=ifnull(?, description), owner=ifnull(?, owner), state=ifnull(?, state), public=ifnull(?, public), event_date=ifnull(?, event_date), popularity=ifnull(?, popularity) WHERE id=?";
	}
	
	@DELETE
	@Path("/{eventId}")
	public void deleteEvent(@PathParam("eventId") String eventId) {
		//VALIDAR
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(buildDeleteEvent());
			stmt.setInt(1, Integer.valueOf(eventId));

			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There is no event with ID = "
						+ eventId);
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	private String buildDeleteEvent() {
		return "DELETE FROM events WHERE id=?";
	}
	
	//////////////////COMMENTS//////////////////////////
	
	@GET
	@Path("/{eventId}/comments")
	@Produces(MediaType.GVENT_API_COMMENT_COLLECTION)  ////FALTAN INJECT LINKS PARA PAGINAR EN COMMENTCOLLECTION
	public CommentCollection getComments(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after, @PathParam("eventId") int eventId) {
		CommentCollection comments = new CommentCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			boolean updateFromLast = after > 0;
			stmt = conn.prepareStatement(buildGetCommentsQuery(updateFromLast));
			if (updateFromLast) {
				stmt.setTimestamp(1, new Timestamp(after));
			} else {
				if (before > 0)
					stmt.setTimestamp(1, new Timestamp(before));
				else
					stmt.setTimestamp(1, null);
				length = (length <= 0) ? 20 : length;
				stmt.setInt(3, length);
			}
			
			stmt.setInt(2, eventId);
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Comment comment = new Comment();
				comment.setId(rs.getInt("id"));
				comment.setUsername(rs.getString("username"));
				comment.setEventId(rs.getInt("event_id"));
				comment.setComment(rs.getString("comment"));
				comment.setLastModified(rs.getTimestamp("last_modified").getTime());
				oldestTimestamp = rs.getTimestamp("last_modified").getTime();
				if (first) {
					first = false;
					comments.setNewestTimestamp(comment.getLastModified());
				}
				comments.addComment(comment);
			}
			comments.setOldestTimestamp(oldestTimestamp);
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return comments;
	}

	private String buildGetCommentsQuery(boolean updateFromLast) {
		if (updateFromLast)
			return "SELECT * FROM comments WHERE last_modified > ? AND event_id = ? ORDER BY last_modified DESC";
		else
			return "SELECT * FROM comments WHERE last_modified < ifnull(?, now()) AND event_id = ? ORDER BY last_modified DESC LIMIT ?";
	}
	
	@GET
	@Path("/{eventId}/comments/{commentId}")
	@Produces(MediaType.GVENT_API_EVENT)
	public Response getComment(@PathParam("eventId") String eventId, @PathParam("commentId") int commentId,
			@Context Request request) {
		// Create CacheControl
		CacheControl cc = new CacheControl();

		Comment comment = getCommentFromDatabase(eventId, commentId);

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Long.toString(comment.getLastModified()));

		// Verify if it matched with etag available in http request
		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);

		// If ETag matches the rb will be non-null;
		// Use the rb to return the response without any further processing
		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}

		// If rb is null then either it is first time request; or resource is
		// modified
		// Get the updated representation and return with Etag attached to it
		rb = Response.ok(comment).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	private Comment getCommentFromDatabase(String eventId, int commentId) {
		Comment comment = new Comment();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(buildGetCommentByIdQuery());
			stmt.setInt(1, commentId);
			stmt.setInt(2, Integer.valueOf(eventId));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				comment.setId(rs.getInt("id"));
				comment.setUsername(rs.getString("username"));
				comment.setEventId(rs.getInt("event_id"));
				comment.setComment(rs.getString("comment"));
				comment.setLastModified(rs.getTimestamp("last_modified").getTime());
			} else {
				throw new NotFoundException("There's no comment with id="
						+ commentId);
			}

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return comment;
	}

	private String buildGetCommentByIdQuery() {
		return "SELECT * FROM comments WHERE id = ? AND event_id = ?";
	}
	
	@POST
	@Path("/{eventId}/comments")
	@Consumes(MediaType.GVENT_API_COMMENT)
	@Produces(MediaType.GVENT_API_COMMENT)
	public Comment createComment(Comment comment, @PathParam("eventId") String eventId) {
		// validateSting(Event); VALIDARRRRRRR
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			String sql = buildInsertComment();
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			// stmt.setString(1, security.getUserPrincipal().getName()); QUE
			// OBTENGA EL USERNAME DEL LOGEADO
			stmt.setString(1, comment.getUsername());
			stmt.setInt(2, Integer.valueOf(eventId));
			stmt.setString(3, comment.getComment());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int commentId = rs.getInt(1);

				comment = getCommentFromDatabase(eventId, commentId);
			} else {
				// Something has failed...
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return comment;
	}

	private String buildInsertComment() {
		return "INSERT INTO comments(username, event_id, comment) value (?, ?, ?)";
	}
	

	@DELETE
	@Path("/{eventId}/comments/{commentId}")
	public void deleteComment(@PathParam("eventId") String eventId, @PathParam("commentId") int commentId) {
		//VALIDAR
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(buildDeleteComment());
			stmt.setInt(1, Integer.valueOf(eventId));
			stmt.setInt(2, commentId);

			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There is no comment with ID = "
						+ commentId);
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	private String buildDeleteComment() {
		return "DELETE FROM comments WHERE event_id=? AND id = ?";
	}
	
	////////////////////USERS///////////////
	@GET
	@Path("/{eventId}/users")
	@Produces(MediaType.GVENT_API_USER_COLLECTION)  ////FALTAN INJECT LINKS PARA PAGINAR EN COMMENTCOLLECTION
	public UserCollection getUsers(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after, @PathParam("eventId") int eventId) {
		UserCollection users = new UserCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			boolean updateFromLast = after > 0;
			stmt = conn.prepareStatement(buildGetUsersQuery(updateFromLast));
			if (updateFromLast) {
				stmt.setTimestamp(1, new Timestamp(after));
			} else {
				if (before > 0)
					stmt.setTimestamp(1, new Timestamp(before));
				else
					stmt.setTimestamp(1, null);
				length = (length <= 0) ? 20 : length;
				stmt.setInt(3, length);
			}
			stmt.setInt(2, eventId);
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				User user = new User();
				user.setUsername(rs.getString("username"));
				user.setUserpass(rs.getString("userpass"));
				user.setName(rs.getString("name"));
				user.setEmail(rs.getString("email"));
				user.setRegisterDate(rs.getTimestamp("register_date").getTime());
				oldestTimestamp = rs.getTimestamp("register_date").getTime();
				if (first) {
					first = false;
					users.setNewestTimestamp(user.getRegisterDate());
				}
				users.addUser(user);
			}
			users.setOldestTimestamp(oldestTimestamp);
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return users;
	}

	private String buildGetUsersQuery(boolean updateFromLast) {
		if (updateFromLast)
			return "SELECT u.*, r.username FROM event_users r, users u WHERE u.register_date > ? AND r.event_id= ? AND u.username=r.username ORDER BY register_date DESC";
		else
			return "SELECT u.*, r.username FROM event_users r, users u  WHERE u.register_date < ifnull(?, now()) AND r.event_id= ? AND u.username=r.username ORDER BY register_date DESC LIMIT ?";
	}
	
	@POST
	@Path("/{eventId}/users")
	@Consumes(MediaType.GVENT_API_USER)
	@Produces(MediaType.GVENT_API_USER)
	public void addUser(User user, @PathParam("eventId") String eventId) {
		// validateSting(Event); VALIDARRRRRRR
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			String sql = buildInsertUser();
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			// stmt.setString(1, security.getUserPrincipal().getName()); QUE
			// OBTENGA EL USERNAME DEL LOGEADO
			stmt.setString(1, user.getUsername());
			stmt.setInt(2, Integer.valueOf(eventId));
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

	}

	private String buildInsertUser() {
		return "INSERT INTO event_users(username, event_id) VALUES(?,?)";
	}
	

	@DELETE
	@Consumes(MediaType.GVENT_API_USER)
	@Path("/{eventId}/users")
	public void deleteUser(@PathParam("eventId") String eventId, User user) {
		//VALIDAR
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(buildDeleteUser());
			stmt.setInt(1, Integer.valueOf(eventId));
			stmt.setString(2, user.getUsername());

			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There is no user with username = "
						+ user.getUsername());
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	private String buildDeleteUser() {
		return "DELETE FROM event_users WHERE event_id=? AND username = ?";
	}
	
}
