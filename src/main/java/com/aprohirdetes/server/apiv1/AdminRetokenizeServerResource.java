package com.aprohirdetes.server.apiv1;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIRestResource;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.utils.MongoUtils;

public class AdminRetokenizeServerResource extends ServerResource implements
		APIRestResource {

	@Override
	public Representation acceptJson(JsonRepresentation entity) throws Exception {
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		
		HashMap<ObjectId, HashSet<String>> result = new HashMap<ObjectId, HashSet<String>>();
		
		for (Hirdetes hi : query) {
			hi.tokenize();
			result.put(hi.getId(), hi.getKulcsszavak());
			datastore.save(hi);
		}
		
		return new JsonRepresentation(new JSONObject(result));
	}

	@Override
	public Representation representJson() throws IOException {
		getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}

}
