package com.aprohirdetes.common;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public interface StaticXmlResource {

	@Get("html")
	public Representation representHtml();
	
}