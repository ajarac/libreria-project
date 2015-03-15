package edu.upc.eetad.dsa.ajarac.libreria.api.model;

import java.util.ArrayList;
import java.util.List;

public class Book {
	private int bookid;
	private String title;
	private String language;
	private String edition;
	private String editionDate;
	private String printingDate;
	private String publisher;
	private long lastModified;
	private long creationTimestamp;
	
	private List<Author> authors;
	
	public Book(){
		super();
		authors = new ArrayList<>();
	}
	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public int getBookid() {
		return bookid;
	}
	
	public void addAuthors(Author author){
		authors.add(author);
	}

	public void setBookid(int bookid) {
		this.bookid = bookid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getEditionDate() {
		return editionDate;
	}

	public void setEditionDate(String editionDate) {
		this.editionDate = editionDate;
	}

	public String getPrintingDate() {
		return printingDate;
	}

	public void setPrintingDate(String printingDate) {
		this.printingDate = printingDate;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public long getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
}
