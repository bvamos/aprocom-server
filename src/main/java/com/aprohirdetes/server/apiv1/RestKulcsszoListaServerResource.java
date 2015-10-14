package com.aprohirdetes.server.apiv1;

import java.util.HashMap;
import java.util.LinkedList;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIRestResource;
import com.aprohirdetes.model.KulcsszoCache;
import com.aprohirdetes.model.RestResponse;

public class RestKulcsszoListaServerResource extends ServerResource implements APIRestResource {

	private String prefix = "";
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		prefix = getQueryValue("p")==null ? "" : getQueryValue("p");
	}
	
	@Override
	public RestResponse acceptJson(JsonRepresentation entity) {
		setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}

	@Override
	public RestResponse representJson() {
		RestResponse response = new RestResponse();
		
		LinkedList<HashMap<String, Object>> repData = KulcsszoCache.getCacheByPrefix(prefix, 10);
		
		response.setSuccess(true);
		response.addData("keywords", repData);
		return response;
	}

	@Override
	public RestResponse representHtml() {
		getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}
}
