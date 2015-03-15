package edu.upc.eetad.dsa.ajarac.libreria.api.model;

import java.util.ArrayList;
import java.util.List;

public class BookCollection {
	private long newestTimestamp;
	private long oldestTimestamp;
	private List<Book> books;

	public BookCollection() {
		super();
		books = new ArrayList<>();
	}

	public void addBook(Book book) {
		books.add(book);
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
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
