package com.aprohirdetes.server;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.RootResource;

public class RootServerResource extends ServerResource implements RootResource {

	public String representText() {
		return getApplication().getName();
	}

	public Representation representHtml() {
		// TODO Auto-generated method stub
		return new StringRepresentation(getApplication().getName());
	}

}
