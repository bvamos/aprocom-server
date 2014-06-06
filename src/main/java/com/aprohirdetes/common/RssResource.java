package com.aprohirdetes.common;

import java.io.IOException;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public interface RssResource {

	@Get("xml")
	public Representation representXml() throws IOException;
}
