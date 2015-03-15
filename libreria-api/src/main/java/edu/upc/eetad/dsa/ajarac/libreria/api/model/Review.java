package edu.upc.eetad.dsa.ajarac.libreria.api.model;

public class Review {
	private int reviewid;
	private String username;
	private String name;
	private int book;
	private String content;
	private long last_modified;
	private long creation_timestamp;

	public int getReviewid() {
		return reviewid;
	}

	public void setReviewid(int reviewid) {
		this.reviewid = reviewid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBook() {
		return book;
	}

	public void setBook(int book) {
		this.book = book;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getLast_modified() {
		return last_modified;
	}

	public void setLast_modified(long last_modified) {
		this.last_modified = last_modified;
	}

	public long getCreation_timestamp() {
		return creation_timestamp;
	}

	public void setCreation_timestamp(long creation_timestamp) {
		this.creation_timestamp = creation_timestamp;
	}

}
