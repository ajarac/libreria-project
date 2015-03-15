package edu.upc.eetac.dsa.ajarac.libreria.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import edu.upc.eetad.dsa.ajarac.libreria.api.model.LibreriaRootAPI;

@Path("/")
public class LibreriaRootAPIResource {
	@GET
	public LibreriaRootAPI getRootAPI() {
		LibreriaRootAPI api = new LibreriaRootAPI();
		return api;
	}
}