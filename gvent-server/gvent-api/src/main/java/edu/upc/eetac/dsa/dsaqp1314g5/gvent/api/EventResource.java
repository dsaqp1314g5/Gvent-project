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

import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model.Event;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model.EventCollection;

@Path("/events")
public class EventResource {

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.GVENT_API_EVENT_COLLECTION)
	public EventCollection getEvents(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after) {
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
			boolean updateFromLast = after > 0;
			stmt = conn.prepareStatement(buildGetEventsQuery(updateFromLast));
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
			if (rs.next()) {
				event.setId(rs.getInt("id"));
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
		return "INSERT INTO events(title, coord_x, coord_y, category, description, owner, state, public, event_date) value (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
				stmt.setString(2, "");
			}
			if(title != null){
				stmt.setString(1, "%" + title + "%");
			}else{
				stmt.setString(1, "%%");
			}
			length = (length <= 0) ? 10 : length;

			stmt.setInt(3, length);
			ResultSet rs = stmt.executeQuery();
			
			long oldestTimestamp = 0;
			while (rs.next()) {
				Event event = new Event();

				event.setId(rs.getInt("id"));
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
			stmt.setInt(10, Integer.valueOf(eventId));
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
		return "UPDATE events SET title=ifnull(?, title), coord_x=ifnull(?, coord_x), coord_y=ifnull(?, coord_y), category=ifnull(?, category), description=ifnull(?, description), owner=ifnull(?, owner), state=ifnull(?, state), public=ifnull(?, public), event_date=ifnull(?, event_date)  WHERE id=?";
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

}
