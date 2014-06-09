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
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model.User;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model.UserCollection;

@Path("/users")
public class UserResource {

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.GVENT_API_USER_COLLECTION)
	public UserCollection getUsers(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after) {
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
				stmt.setInt(2, length);
			}
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
			return "SELECT * FROM users WHERE register_date > ? ORDER BY register_date DESC";
		else
			return "SELECT * FROM users WHERE register_date < ifnull(?, now()) ORDER BY register_date DESC LIMIT ?";
	}

	@GET
	@Path("/{username}")
	@Produces(MediaType.GVENT_API_EVENT)
	public Response getUser(@PathParam("username") String username,
			@Context Request request) {
		// Create CacheControl
		CacheControl cc = new CacheControl();

		User user = getUserFromDatabase(username);

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Long.toString(user.getRegisterDate()));

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
		rb = Response.ok(user).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	private User getUserFromDatabase(String username) {
		User user = new User();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(buildGetUserByIdQuery());
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user.setUsername(rs.getString("username"));
				user.setUserpass(rs.getString("userpass"));
				user.setName(rs.getString("name"));
				user.setEmail(rs.getString("email"));
				user.setRegisterDate(rs.getTimestamp("register_date").getTime());
			} else {
				throw new NotFoundException("There's no user with username="
						+ username);
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

		return user;
	}

	private String buildGetUserByIdQuery() {
		return "SELECT * FROM users WHERE username = ?";
	}

	@POST
	@Consumes(MediaType.GVENT_API_USER)
	@Produces(MediaType.GVENT_API_USER)
	public User createUser(User user) {
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

			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getUserpass());
			stmt.setString(3, user.getName());
			stmt.setString(4, user.getEmail());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				String username = rs.getString(1);

				user = getUserFromDatabase(username);
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

		return user;
	}

	private String buildInsertUser() {
		return "INSERT INTO users(username, userpass, name, email) value (?, ?, ?, ?)";
	}
	
	@GET
	@Path("/search")
	@Produces(MediaType.GVENT_API_USER_COLLECTION)
	public UserCollection searchUser(@QueryParam("username") String username,
			@QueryParam("title") String title, @QueryParam("length") int length) {

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
			stmt = conn.prepareStatement(searchGetUserByIdQuery());
			if(!username.isEmpty()){
				stmt.setString(1, "%" + username + "%");
			}
			length = (length <= 0) ? 10 : length;
			stmt.setInt(2, length);
			ResultSet rs = stmt.executeQuery();
			long oldestTimestamp = 0;
			while (rs.next()) {
				User user = new User();
				user.setUsername(rs.getString("username"));
				user.setUserpass(rs.getString("userpass"));
				user.setName(rs.getString("name"));
				user.setEmail(rs.getString("email"));
				user.setRegisterDate(rs.getTimestamp("register_date").getTime());
				oldestTimestamp = rs.getTimestamp("register_date").getTime();
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
	
	private String searchGetUserByIdQuery() {
		return "SELECT * FROM users WHERE username like ? LIMIT ?";
	}

	@PUT
	@Path("/{username}")
	@Consumes(MediaType.GVENT_API_USER)
	@Produces(MediaType.GVENT_API_USER)
	public User updateUser(@PathParam("username") String username, User user) {
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
			stmt = conn.prepareStatement(buildUpdateUser());

			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getUserpass());
			stmt.setString(3, user.getName());
			stmt.setString(4, user.getEmail());
			stmt.setString(5, username);
			int rows = stmt.executeUpdate();
			if (rows == 1)
				user = getUserFromDatabase(user.getUsername());
			else {
				throw new NotFoundException("There is no user with username = "
						+ username);
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

		return user;
	}
	
	private String buildUpdateUser() {
		return "UPDATE users SET username=ifnull(?, username), userpass=ifnull(?, userpass), name=ifnull(?, name), email=ifnull(?, email)  WHERE username=?";
	}
	
	@DELETE
	@Path("/{username}")
	public void deleteUser(@PathParam("username") String username) {
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
			stmt.setString(1, username);

			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There is no user with username = "
						+ username);
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
		return "DELETE FROM users WHERE username=?";
	}
	
	@POST
	@Path("/{username}")
	@Consumes(MediaType.GVENT_API_USER)
	@Produces(MediaType.GVENT_API_USER)
	public void addFriend(@PathParam("username") String username, @QueryParam("friend") String friend) {
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
			String sql = buildInsertFriend();
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, username);
			stmt.setString(2, friend);
			stmt.executeUpdate();
			/*stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getName());
			stmt.setString(3, user.getEmail());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				String username = rs.getString(1);

				user = getUserFromDatabase(username);
			} else {
				// Something has failed...
			}*/
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

		//return user;
	}

	private String buildInsertFriend(){
		return "INSERT INTO friends(username_a, username_b) value (?, ?)";
	}
	
	@GET
	@Path("/{username}/friends")
	@Produces(MediaType.GVENT_API_USER_COLLECTION)
	public UserCollection getFriends(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after, @PathParam("username") String username) {
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
			stmt = conn.prepareStatement(buildGetFriendsQuery(updateFromLast));
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
			//String username = username; //security.getUserPrincipal().getName();
			stmt.setString(2, username);
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

	private String buildGetFriendsQuery(boolean updateFromLast) {
		if (updateFromLast)
			return "SELECT u.*, f.* FROM users u, friends f WHERE u.register_date > ?  AND f.username_a = ? AND f.username_b = u.username ORDER BY register_date DESC";
		else
			return "SELECT u.*, f.* FROM users u, friends f WHERE u.register_date < ifnull(?, now()) AND f.username_a = ? AND f.username_b = u.username ORDER BY register_date DESC LIMIT ?";
	}
	
	
	@DELETE
	@Path("/{username}/friends")
	public void deleteFriend(@PathParam("username") String username,  @QueryParam("friend") String friend) {
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
			stmt = conn.prepareStatement(buildDeleteFriend());
			stmt.setString(1, username);
			stmt.setString(2, friend);
			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There is no user with username = "
						+ username);
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

	private String buildDeleteFriend() {
		return "DELETE FROM friends WHERE username_a = ? AND username_b = ?";
	}
	
	//// EVENTS
	
	
	@GET
	@Path("/{username}/events")
	@Produces(MediaType.GVENT_API_EVENT_COLLECTION)
	public EventCollection getEvents(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after, @PathParam("username") String username) {
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
				stmt.setInt(3, length);
			}
			//String username = username; //security.getUserPrincipal().getName();
			stmt.setString(2, username);
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
				event.setPuntuation(rs.getDouble("puntuation"));
				event.setVotes(rs.getInt("votes"));
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
			return "SELECT e.* FROM events e WHERE e.creation_date > ? AND e.owner= ? ORDER BY creation_date DESC";
		else
			return "SELECT e.* FROM events e  WHERE e.creation_date < ifnull(?, now()) AND e.owner= ? ORDER BY creation_date DESC LIMIT ?";
	}
	
	@GET
	@Path("/{username}/events/followed")
	@Produces(MediaType.GVENT_API_EVENT_COLLECTION)
	public EventCollection getEventsFollowed(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after, @PathParam("username") String username) {
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
			stmt = conn.prepareStatement(buildGetEventsFollowedQuery(updateFromLast));
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
			//String username = username; //security.getUserPrincipal().getName();
			stmt.setString(2, username);
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
				event.setPuntuation(rs.getDouble("puntuation"));
				event.setVotes(rs.getInt("votes"));
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

	private String buildGetEventsFollowedQuery(boolean updateFromLast) {
		if (updateFromLast)
			return "SELECT e.*, r.username FROM event_users r, events e WHERE e.creation_date > ? AND r.username= ? AND e.id = r.event_id ORDER BY creation_date DESC";
		else
			return "SELECT e.*, r.username FROM event_users r, events e  WHERE e.creation_date < ifnull(?, now()) AND r.username= ? AND  e.id = r.event_id ORDER BY creation_date DESC LIMIT ?";
	}

}
