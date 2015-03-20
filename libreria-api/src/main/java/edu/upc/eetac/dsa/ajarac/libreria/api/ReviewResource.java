package edu.upc.eetac.dsa.ajarac.libreria.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetad.dsa.ajarac.libreria.api.model.Review;
import edu.upc.eetad.dsa.ajarac.libreria.api.model.ReviewCollection;

@Path("/books/reviews")
public class ReviewResource {

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	private String GET_REVIEWS_QUERY = "select * from reviews where creation_timestamp < ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_REVIEWS_QUERY_FROM_LAST = "select * from reviews where creation_timestamp > ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_REVIEWS_BY_BOOKID = "select * from reviews where bookid = ? and creation_timestamp < ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_REVIEWS_BY_BOOKID_FROM_LAST = "select * from reviews where bookid = ? and creation_timestamp > ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_REVIEW_BY_REVIEWID = "select * from review where reviewid=?";
	private String INSERT_REVIEW_QUERY = "insert into reviews (username, name, bookid, content) values (?, ?, ?, ?)";
	private String UPDATE_REVIEW_QUERY = "update reviews set content = ifnull(?, content) where reviewid=?";
	private String DELETE_REVIEW_QUERY = "delete from reviews where reviewid = ?";

	@GET
	@Produces(MediaType.LIBRERIA_API_REVIEW_COLLECTION)
	public ReviewCollection getReviews(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after) {
		ReviewCollection reviews = new ReviewCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			if (before > 0) {
				stmt = conn.prepareStatement(GET_REVIEWS_QUERY_FROM_LAST);
				stmt.setTimestamp(1, new Timestamp(before));
			} else {
				stmt = conn.prepareStatement(GET_REVIEWS_QUERY);
				if (after > 0)
					stmt.setTimestamp(1, new Timestamp(after));
				else
					stmt.setTimestamp(1, null);
			}
			length = (length <= 0) ? 10 : length;
			stmt.setInt(2, length);
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Review review = new Review();
				review.setBook(rs.getInt("bookid"));
				review.setReviewid(rs.getInt("reviewid"));
				review.setContent(rs.getString("content"));
				review.setUsername(rs.getString("username"));
				review.setName(rs.getString("name"));
				review.setLast_modified(rs.getTimestamp("last_modified")
						.getTime());
				review.setCreation_timestamp(rs.getTimestamp(
						"creation_timestamp").getTime());
				oldestTimestamp = rs.getTimestamp("creation_timestamp")
						.getTime();
				review.setLast_modified(oldestTimestamp);
				if (first) {
					first = false;
					reviews.setNewestTimestamp(review.getCreation_timestamp());
					;
				}
				reviews.addReview(review);
			}
			reviews.setOldestTimestamp(oldestTimestamp);
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
		return reviews;
	}

	@GET
	@Path("/{bookid}")
	@Produces(MediaType.LIBRERIA_API_REVIEW_COLLECTION)
	public ReviewCollection getReview(@PathParam("bookid") String bookid,
			@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after) {
		ReviewCollection reviews = new ReviewCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			if (before > 0) {
				stmt = conn.prepareStatement(GET_REVIEWS_BY_BOOKID_FROM_LAST);
				stmt.setInt(1, Integer.valueOf(bookid));
				stmt.setTimestamp(2, new Timestamp(before));
			} else {
				stmt = conn.prepareStatement(GET_REVIEWS_BY_BOOKID);
				stmt.setInt(1, Integer.valueOf(bookid));
				if (after > 0)
					stmt.setTimestamp(2, new Timestamp(after));
				else
					stmt.setTimestamp(2, null);
			}
			length = (length <= 0) ? 5 : length;
			stmt.setInt(3, length);
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Review review = new Review();
				review.setBook(rs.getInt("bookid"));
				review.setReviewid(rs.getInt("reviewid"));
				review.setContent(rs.getString("content"));
				review.setUsername(rs.getString("username"));
				review.setName(rs.getString("name"));
				review.setLast_modified(rs.getTimestamp("last_modified")
						.getTime());
				review.setCreation_timestamp(rs.getTimestamp(
						"creation_timestamp").getTime());
				oldestTimestamp = rs.getTimestamp("creation_timestamp")
						.getTime();
				review.setLast_modified(oldestTimestamp);
				if (first) {
					first = false;
					reviews.setNewestTimestamp(review.getCreation_timestamp());
					;
				}
				reviews.addReview(review);
			}
			reviews.setOldestTimestamp(oldestTimestamp);

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
		return reviews;
	}

	@PUT
	@Path("/{reviewid}")
	@Consumes(MediaType.LIBRERIA_API_REVIEW)
	@Produces(MediaType.LIBRERIA_API_REVIEW)
	public Review updateReview(@PathParam("reviewid") String reviewid,
			Review review) {
		validateReview(review);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(UPDATE_REVIEW_QUERY);
			stmt.setString(1, review.getContent());
			stmt.setInt(2, review.getBook());
			int rows = stmt.executeUpdate();
			if (rows == 1) {
				review = getReviewFromDataBase(reviewid);
			} else {
				throw new NotFoundException("There's no book with bookid="
						+ reviewid);
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
		return review;
	}

	@POST
	@Consumes(MediaType.LIBRERIA_API_REVIEW)
	@Produces(MediaType.LIBRERIA_API_REVIEW)
	public Review createReview(Review review) {
		validateReview(review);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_REVIEW_QUERY,
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, review.getUsername());
			stmt.setString(2, review.getName());
			stmt.setInt(3, review.getBook());
			stmt.setString(4, review.getContent());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int reviewid = rs.getInt(1);
				review = getReviewFromDataBase(Integer.toString(reviewid));
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
		return review;
	}

	@DELETE
	@Path("/{reviewid}")
	public void deleteReview(@PathParam("reviewid") String reviewid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_REVIEW_QUERY);
			stmt.setInt(1, Integer.valueOf(reviewid));
			int rows = stmt.executeUpdate();
			if (rows == 0) {
				throw new NotFoundException("There's no book with reviewid="
						+ reviewid);
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

	private Review getReviewFromDataBase(String reviewid) {
		Review review = new Review();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_REVIEW_BY_REVIEWID);
			stmt.setInt(1, Integer.valueOf(reviewid));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				review.setReviewid(rs.getInt("reviewid"));
				review.setBook(rs.getInt("bookid"));
				review.setContent(rs.getString("content"));
				review.setCreation_timestamp(rs.getLong("creation_timestamp"));
				review.setLast_modified(rs.getLong("last_modified"));
				review.setName(rs.getString("name"));
				review.setUsername(rs.getString("username"));
			} else {
				throw new NotFoundException("There's no book with reviewid="
						+ reviewid);
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
		return review;
	}

	private void validateReview(Review review) {
		// TODO Auto-generated method stub

	}
}
