package edu.upc.eetac.dsa.ajarac.libreria.api;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class LibreriaApplication extends ResourceConfig{
	public LibreriaApplication() {
		super();
		register(DeclarativeLinkingFeature.class);
	}
}
