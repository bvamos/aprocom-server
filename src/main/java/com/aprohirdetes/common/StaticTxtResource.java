package com.aprohirdetes.common;

import java.io.IOException;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public interface StaticTxtResource {

	@Get("txt")
	public Representation representTxt() throws IOException;
	
}
