package edu.upc.eetac.dsa.ajarac.libreria.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetad.dsa.ajarac.libreria.api.model.Author;
import edu.upc.eetad.dsa.ajarac.libreria.api.model.Book;
import edu.upc.eetad.dsa.ajarac.libreria.api.model.BookCollection;

@Path("/books")
public class BookResource {

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	private String GET_AUTHORS_BY_BOOKID = "select authors.* from books, authors, authors_books where books.bookid = ? and authors.authorid = authors_books.authorid and books.bookid = authors_books.bookid";
	private String GET_BOOK_BY_ID_QUERY = "select * from books where bookid=?";
	private String GET_BOOKS_QUERY = "select * from books where creation_timestamp < ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_BOOKS_QUERY_FROM_LAST = "select * from books where creation_timestamp > ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_BOOKS_BY_TITLE_QUERY = "select * from books where title LIKE ? and creation_timestamp < ifnull(?, now()) order by creation_timestamp desc limit ?";
	private String GET_BOOKS_BY_TITLE_QUERY_FROM_LAST = "select * from books where title LIKE ? and creation_timestamp > ifnull(?, now()) order by creation_timestamp desc limit ?";
	private String GET_BOOKS_BY_AUTHOR_QUERY = "select books.* from books, authors, authors_books where authors.name like ? and authors.authorid = authors_books.authorid and books.bookid = authors_books.bookid and creation_timestamp < ifnull(?, now()) order by creation_timestamp desc limit ?";
	private String GET_BOOKS_BY_AUTHOR_QUERY_FROM_LAST = "select books.* from books, authors, authors_books where authors.name like ? and authors.authorid = authors_books.authorid and books.bookid = authors_books.bookid and creation_timestamp > ifnull(?, now()) order by creation_timestamp desc limit ?";
	private String GET_AUTHORS_QUERY = "select * from authors where name like ?";
	private String INSERT_BOOK_QUERY = "insert into books (title, language, edition, editionDate, printingDate, publisher) values (?,?,?,?,?,?) ";
	private String UPDATE_BOOK_QUERY = "update books set title=ifnull(?, title), language=ifnull(?, language), edition=ifnull(?,edition), editionDate=ifnull(?,editionDate), printingDate=ifnull(?,printingDate) where bookid =?";
	private String DELETE_BOOK_QUERY = "delete from books where bookid = ?";
	private String INSERT_AUTHOR_BOOK_QUERY = "insert into authors_books values (?, ?)";

	@GET
	@Produces(MediaType.LIBRERIA_API_BOOK_COLLECTION)
	public BookCollection getBooks(@QueryParam("length") int length,
			@QueryParam("title") String title,
			@QueryParam("author") String author,
			@QueryParam("before") long before, @QueryParam("after") long after) {
		BookCollection books = new BookCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			if (title != null) {
				if (before > 0) {
					stmt = conn
							.prepareStatement(GET_BOOKS_BY_TITLE_QUERY_FROM_LAST);
					stmt.setString(1, "%" + title + "%");
					stmt.setTimestamp(2, new Timestamp(before));
				} else {
					stmt = conn.prepareStatement(GET_BOOKS_BY_TITLE_QUERY);
					stmt.setString(1, "%" + title + "%");
					if (after > 0)
						stmt.setTimestamp(2, new Timestamp(after));
					else
						stmt.setTimestamp(2, null);
				}
				length = (length <= 0) ? 5 : length;
				stmt.setInt(3, length);
			} else if (author != null) {
				if (before > 0) {
					stmt = conn
							.prepareStatement(GET_BOOKS_BY_AUTHOR_QUERY_FROM_LAST);
					stmt.setString(1, "%" + author + "%");
					stmt.setTimestamp(2, new Timestamp(before));
				} else {
					stmt = conn.prepareStatement(GET_BOOKS_BY_AUTHOR_QUERY);
					stmt.setString(1, "%" + author + "%");
					if (after > 0)
						stmt.setTimestamp(2, new Timestamp(after));
					else
						stmt.setTimestamp(2, null);
				}
				length = (length <= 0) ? 5 : length;
				stmt.setInt(3, length);
			} else {
				if (before > 0) {
					stmt = conn.prepareStatement(GET_BOOKS_QUERY_FROM_LAST);
					stmt.setTimestamp(1, new Timestamp(before));
				} else {
					stmt = conn.prepareStatement(GET_BOOKS_QUERY);
					if (after > 0)
						stmt.setTimestamp(1, new Timestamp(after));
					else
						stmt.setTimestamp(1, null);
				}
				length = (length <= 0) ? 5 : length;
				stmt.setInt(2, length);
			}
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Book book = new Book();
				book.setBookid(rs.getInt("bookid"));
				book.setTitle(rs.getString("title"));
				book.setLanguage(rs.getString("language"));
				book.setEdition(rs.getString("edition"));
				book.setEditionDate(rs.getString("editionDate"));
				book.setPrintingDate(rs.getString("printingDate"));
				book.setPublisher(rs.getString("publisher"));
				book.setLastModified(rs.getTimestamp("last_modified").getTime());
				book.setCreationTimestamp(rs.getTimestamp("creation_timestamp")
						.getTime());
				Book b = new Book();
				b = getAuthors(book.getBookid());
				book.setAuthors(b.getAuthors());
				oldestTimestamp = rs.getTimestamp("creation_timestamp")
						.getTime();
				book.setLastModified(oldestTimestamp);
				if (first) {
					first = false;
					books.setNewestTimestamp(book.getCreationTimestamp());
				}
				books.addBook(book);
			}
			books.setOldestTimestamp(oldestTimestamp);
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
		return books;
	}

	private Book getAuthors(int bookid) {
		Book book = new Book();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_AUTHORS_BY_BOOKID);
			stmt.setInt(1, Integer.valueOf(bookid));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Author author = new Author();
				author.setAuthorid(rs.getInt("authorid"));
				author.setName(rs.getString("name"));
				book.addAuthors(author);
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
		return book;

	}

	@GET
	@Path("/{bookid}")
	@Produces(MediaType.LIBRERIA_API_BOOK)
	public Response getBook(@PathParam("bookid") String bookid,
			@Context Request request) {
		CacheControl cc = new CacheControl();

		Book book = getBookFromDataBase(bookid);

		EntityTag eTag = new EntityTag(Long.toString(book.getLastModified()));

		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);

		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}
		rb = Response.ok(book).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	private Book getBookFromDataBase(String bookid) {
		Book book = new Book();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_BOOK_BY_ID_QUERY);
			stmt.setInt(1, Integer.valueOf(bookid));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				book.setBookid(rs.getInt("bookid"));
				book.setTitle(rs.getString("title"));
				book.setLanguage(rs.getString("language"));
				book.setEdition(rs.getString("edition"));
				book.setEditionDate(rs.getString("editionDate"));
				book.setPrintingDate(rs.getString("printingDate"));
				book.setPublisher(rs.getString("publisher"));
				book.setLastModified(rs.getTimestamp("last_modified").getTime());
				book.setCreationTimestamp(rs.getTimestamp("creation_timestamp")
						.getTime());
				Book b = new Book();
				b = getAuthors(book.getBookid());
				book.setAuthors(b.getAuthors());
			} else {
				throw new NotFoundException("There's no book with bookid="
						+ bookid);
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

		return book;
	}

	@POST
	@Consumes(MediaType.LIBRERIA_API_BOOK)
	@Produces(MediaType.LIBRERIA_API_BOOK)
	public Book createBook(Book book) {
		validateBook(book);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_BOOK_QUERY,
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, book.getTitle());
			stmt.setString(2, book.getLanguage());
			stmt.setString(3, book.getEdition());
			stmt.setString(4, book.getEditionDate());
			stmt.setString(5, book.getPrintingDate());
			stmt.setString(6, book.getPublisher());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int authorid = rs.getInt(1);

				book = getBookFromDataBase(Integer.toString(authorid));
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
		return book;
	}

	@PUT
	@Path("/{bookid}")
	@Consumes(MediaType.LIBRERIA_API_BOOK)
	@Produces(MediaType.LIBRERIA_API_BOOK)
	public Book updateBook(@PathParam("bookid") String bookid, Book book) {
		validateBook(book);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			updateAuthorBook(book);
			stmt = null;
			stmt = conn.prepareStatement(UPDATE_BOOK_QUERY);
			stmt.setString(1, book.getTitle());
			stmt.setString(2, book.getLanguage());
			stmt.setString(3, book.getEdition());
			stmt.setString(4, book.getEditionDate());
			stmt.setString(5, book.getPrintingDate());
			stmt.setString(6, book.getPublisher());
			stmt.setInt(7, Integer.valueOf(bookid));
			int rows = stmt.executeUpdate();
			if (rows == 1) {
				book = getBookFromDataBase(bookid);
			} else {
				throw new NotFoundException("There's no book with bookid="
						+ bookid);
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
		return book;
	}

	private void updateAuthorBook(Book book) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			int j = 0;
			int i = book.getAuthors().size();
			while (j < i) {
				stmt = null;
				stmt = conn.prepareStatement(INSERT_AUTHOR_BOOK_QUERY);
				stmt.setInt(1, book.getAuthors().get(j).getAuthorid());
				stmt.setInt(2, book.getBookid());
				int rows = stmt.executeUpdate();
				if (rows == 1) {
					throw new NotFoundException("There's no author with authorid="
							+ book.getAuthors().get(j).getAuthorid());
				}
				j++;
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

	private void validateBook(Book book) {
		List<Author> authors = new ArrayList<>();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			boolean vacio = false;
			int j = 0;
			int i = authors.size();
			while (j < i) {
				stmt = null;
				stmt = conn.prepareStatement(GET_AUTHORS_QUERY);
				stmt.setInt(1, authors.get(j).getAuthorid());
				int rows = stmt.executeUpdate();
				if (rows == 0)
					vacio = true;
				j++;
			}
			if (vacio == true) {
				throw new NotFoundException("There's no author");
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

	@DELETE
	@Path("/{bookid}")
	public void deleteBook(@PathParam("bookid") String bookid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_BOOK_QUERY);
			stmt.setInt(1, Integer.valueOf(bookid));
			int rows = stmt.executeUpdate();
			if (rows == 0) {
				throw new NotFoundException("There's no book with bookid="
						+ bookid);
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
