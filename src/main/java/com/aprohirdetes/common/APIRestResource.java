package com.aprohirdetes.common;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public interface APIRestResource {

	@Post("json")
	public Representation acceptJson(JsonRepresentation entity) throws Exception;
	
}
