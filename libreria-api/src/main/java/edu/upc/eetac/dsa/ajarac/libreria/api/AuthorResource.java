package edu.upc.eetac.dsa.ajarac.libreria.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetad.dsa.ajarac.libreria.api.model.Author;

@Path("/authors")
public class AuthorResource {

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private String GET_AUTHOR_QUERY = "select * from authors where authorid= ?";
	private String INSERT_AUTHOR_QUERY = "insert into authors (name) values (?)";
	private String UPDATE_AUTHOR_QUERY = "update authors set name = ifnull(?, name) where authorid = ?";
	private String DELETE_AUTHOR_QUERY = "delete from authors where authorid = ?";

	@POST
	@Produces(MediaType.LIBRERIA_API_AUTHOR)
	@Consumes(MediaType.LIBRERIA_API_AUTHOR)
	public Author createAuthor(Author author) {
		validateAuthor(author);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_AUTHOR_QUERY,
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, author.getName());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int authorid = rs.getInt(1);

				author = getAuthorFromDataBase(Integer.toString(authorid));
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

		return author;
	}

	private Author getAuthorFromDataBase(String authorid) {
		Author author = new Author();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_AUTHOR_QUERY);
			stmt.setInt(1, Integer.parseInt(authorid));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				author.setAuthorid(rs.getInt("authorid"));
				author.setName(rs.getString("name"));
			} else {
				throw new NotFoundException("There's no author with stingid="
						+ authorid);
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
		return author;
	}

	private void validateAuthor(Author author) {
		if (author.getName() == null) {
			throw new BadRequestException("Name can't be null.");
		}
	}

	@PUT
	@Path("/{authorid}")
	@Consumes(MediaType.LIBRERIA_API_AUTHOR)
	@Produces(MediaType.LIBRERIA_API_AUTHOR)
	public Author updateAuthor(@PathParam("authorid") String authorid,
			Author author) {
		validateAuthor(author);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_AUTHOR_QUERY);
			stmt.setString(1, author.getName());
			int rows = stmt.executeUpdate();
			if (rows == 1)
				author = getAuthorFromDataBase(authorid);
			else
				throw new NotFoundException("There's no authorid with stingid="
						+ authorid);
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
		return author;
	}

	@DELETE
	@Path("/{authoid")
	public void deleteAuthor(@PathParam("authorid") String authorid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_AUTHOR_QUERY);
			stmt.setInt(1, Integer.parseInt(authorid));

			int rows = stmt.executeUpdate();
			if (rows == 0) {
				throw new NotFoundException("There's no authorid with stingid="
						+ authorid);
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
	}
}
