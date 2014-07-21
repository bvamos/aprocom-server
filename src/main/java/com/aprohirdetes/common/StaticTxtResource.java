package com.aprohirdetes.common;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public interface StaticTxtResource {

	@Get("txt")
	public Representation representTxt();
	
}
