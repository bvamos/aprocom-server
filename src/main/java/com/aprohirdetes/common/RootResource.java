package com.aprohirdetes.common;

import java.io.IOException;
import java.net.UnknownHostException;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public interface RootResource {

	@Get("txt")
	public String representText() throws UnknownHostException;
	
	@Get("json")
	public Representation representJson() throws UnknownHostException;
	
	@Get("html")
	public Representation representHtml() throws IOException;
	
}
