package com.aprohirdetes.common;

import java.io.IOException;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public interface KilepesResource {

	@Get("html")
	public Representation representHtml() throws IOException;
	
}
