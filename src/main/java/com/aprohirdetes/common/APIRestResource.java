package com.aprohirdetes.common;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.aprohirdetes.model.RestResponse;

/**
 * AproAPI Server Resource interface
 * @author bvamos
 *
 */
public interface APIRestResource {

	@Get("json")
	public RestResponse representJson();
	
	@Get("html")
	public RestResponse representHtml();
	
	@Post("json")
	public RestResponse acceptJson(JsonRepresentation entity);
	
}
