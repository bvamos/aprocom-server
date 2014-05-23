package com.aprohirdetes.server.apiv1;

import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIJsonResource;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.utils.MongoUtils;

public class AdminRetokenizeServerResource extends ServerResource implements
		APIJsonResource {

	@Override
	public Representation accept(JsonRepresentation entity) throws Exception {
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		
		HashMap<ObjectId, List<String>> result = new HashMap<ObjectId, List<String>>();
		
		for (Hirdetes hi : query) {
			hi.tokenize();
			result.put(hi.getId(), hi.getKulcsszavak());
			datastore.save(hi);
		}
		
		return new JsonRepresentation(new JSONObject(result));
	}

}
