package edu.upc.eetad.dsa.ajarac.libreria.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.ajarac.libreria.api.BookResource;
import edu.upc.eetac.dsa.ajarac.libreria.api.LibreriaRootAPIResource;
import edu.upc.eetac.dsa.ajarac.libreria.api.MediaType;



public class LibreriaRootAPI {
	@InjectLinks({
        @InjectLink(resource = LibreriaRootAPIResource.class, style = Style.ABSOLUTE, rel = "self bookmark home", title = "Beeter Root API"),
        @InjectLink(resource = BookResource.class, style = Style.ABSOLUTE, rel = "collection", title = "Latest book", type = MediaType.LIBRERIA_API_BOOK_COLLECTION),
        @InjectLink(resource = BookResource.class, style = Style.ABSOLUTE, rel = "create-sting", title = "Create new book", type = MediaType.LIBRERIA_API_BOOK)})
	private List<Link> links;

public List<Link> getLinks() {
	return links;
}

public void setLinks(List<Link> links) {
	this.links = links;
}
}
