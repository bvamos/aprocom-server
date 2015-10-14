package com.aprohirdetes.server.apiv1;

import java.util.HashMap;
import java.util.HashSet;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIRestResource;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.RestResponse;
import com.aprohirdetes.utils.MongoUtils;

/**
 * Ujrageneralja a kulcsszavakat minden hirdetesben.
 * @author bvamos
 *
 */
public class AdminRetokenizeServerResource extends ServerResource implements
		APIRestResource {

	@Override
	public RestResponse acceptJson(JsonRepresentation entity) {
		RestResponse response = new RestResponse();
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		
		HashMap<ObjectId, HashSet<String>> result = new HashMap<ObjectId, HashSet<String>>();
		
		for (Hirdetes hi : query) {
			hi.tokenize();
			result.put(hi.getId(), hi.getKulcsszavak());
			datastore.save(hi);
		}
		
		response.setSuccess(true);
		response.addData("kulcsszavak", result);
		
		return response;
	}

	@Override
	public RestResponse representJson() {
		getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}

	@Override
	public RestResponse representHtml() {
		getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}
}
