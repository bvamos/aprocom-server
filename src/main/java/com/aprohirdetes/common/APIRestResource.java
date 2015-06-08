package com.aprohirdetes.common;

import java.io.IOException;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface APIRestResource {

	@Get("json")
	public Representation representJson() throws IOException;
	
	@Get("html")
	public Representation representHtml() throws IOException;
	
	@Post("json")
	public Representation acceptJson(JsonRepresentation entity) throws Exception;
	
}
