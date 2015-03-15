package edu.upc.eetac.dsa.ajarac.libreria.api;

import javax.sql.DataSource;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetac.dsa.ajarac.libreria.api.DataSourceSPA;

@Path("/books/reviews")
public class ReviewResource {
	@Context
	private SecurityContext security;
	
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	
	
}
