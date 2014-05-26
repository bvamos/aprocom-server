package com.aprohirdetes.server;

import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticXmlResource;

public class SitemapServerResource extends ServerResource implements StaticXmlResource {

	public Representation representHtml() {
		return new FileRepresentation("war:///WEB-INF/classes/sitemap.xml", MediaType.TEXT_XML, 3600*24);
	}

}
