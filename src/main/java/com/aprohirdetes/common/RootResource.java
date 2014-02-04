package com.aprohirdetes.common;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public interface RootResource {

	@Get("txt")
	public String representText();
	
	@Get("html")
	public Representation representHtml();
	
}
