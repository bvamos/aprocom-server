package com.aprohirdetes.server;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticTxtResource;

public class StaticRobotsServerResource extends ServerResource implements StaticTxtResource {

	public Representation representTxt() {
		String sitemap = "User-agent: *\n"
				+ "Disallow:\n";
		
		return new StringRepresentation(sitemap, MediaType.TEXT_PLAIN);
	}

}
