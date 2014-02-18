package com.aprohirdetes.common;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public interface KeresesResource {

	@Get("html")
	public Representation representHtml();
}
