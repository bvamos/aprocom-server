package com.aprohirdetes.server.apiv1;

import java.io.IOException;
import org.json.JSONArray;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIRestResource;
import com.aprohirdetes.model.KulcsszoCache;

public class RestKulcsszoListaServerResource extends ServerResource implements APIRestResource {

	private String prefix = "";
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		prefix = getQueryValue("p")==null ? "" : getQueryValue("p");
	}
	
	@Override
	public Representation acceptJson(JsonRepresentation entity) throws Exception {
		setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}

	@Override
	public Representation representJson() throws IOException {
		Representation rep = null;
		JSONArray repData = KulcsszoCache.getCacheByPrefix(prefix, 10);
		
		rep = new JsonRepresentation(repData);
		return rep;
	}

	@Override
	public Representation representHtml() throws IOException {
		getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}
}
