package com.aprohirdetes.common;

import java.io.IOException;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface ProfilResource {

	@Get("html")
	public Representation representHtml() throws IOException;
	
	@Post
	public Representation accept(Form form) throws IOException;
}
