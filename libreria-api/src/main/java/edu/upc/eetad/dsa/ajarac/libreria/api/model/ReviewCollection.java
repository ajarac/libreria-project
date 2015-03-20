package edu.upc.eetad.dsa.ajarac.libreria.api.model;

import java.util.ArrayList;
import java.util.List;

public class ReviewCollection {
	private long newestTimestamp;
	private long oldestTimestamp;
	private List<Review> reviews;
	
	public ReviewCollection(){
		super();
		reviews = new ArrayList<>();
	}
	
	public void addReview(Review review){
		reviews.add(review);
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

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
}
