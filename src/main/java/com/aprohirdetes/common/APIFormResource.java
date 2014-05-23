package com.aprohirdetes.common;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public interface APIFormResource {

	@Post
	public Representation accept(Representation entity) throws Exception;
}
