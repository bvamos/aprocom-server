package com.aprohirdetes.common;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public interface APISessionKilepesResource {

	@Post("json")
	public Representation accept(JsonRepresentation entity) throws Exception;
}
