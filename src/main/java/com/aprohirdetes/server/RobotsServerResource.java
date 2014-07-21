package com.aprohirdetes.server;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticHtmlResource;

public class RobotsServerResource extends ServerResource implements StaticHtmlResource {

	public Representation representHtml() {
		String sitemap = "User-agent: *\n"
				+ "Disallow:\n";
		
		return new StringRepresentation(sitemap, MediaType.TEXT_PLAIN);
	}

}
