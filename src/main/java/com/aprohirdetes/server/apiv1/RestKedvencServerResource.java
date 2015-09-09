package com.aprohirdetes.server.apiv1;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.model.HirdetesKedvenc;
import com.aprohirdetes.model.RestResponse;
import com.aprohirdetes.utils.MongoUtils;

public class RestKedvencServerResource extends ServerResource  {
	
	private ObjectId hirdetesId;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		hirdetesId = new ObjectId((String) this.getRequestAttributes().get("hirdetesId"));
	}
	
	@Get
	public RestResponse representJson() throws Exception {
		RestResponse restResponse = new RestResponse();
		
		// TODO: API: Kedvenc visszaadasa hirdetesId alapjan
		getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		
		return restResponse;
	}

	@Delete("json")
	public RestResponse removeJson() throws Exception {
		RestResponse restResponse = new RestResponse();
		
		Datastore datastore = MongoUtils.getDatastore();
		datastore.delete(datastore.createQuery(HirdetesKedvenc.class).filter("hirdetesId", this.hirdetesId));
		
		restResponse.setSuccess(true);
		
		return restResponse;
	}

	
}
