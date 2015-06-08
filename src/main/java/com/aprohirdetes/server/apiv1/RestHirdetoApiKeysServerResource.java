package com.aprohirdetes.server.apiv1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIRestResource;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.utils.MongoUtils;

public class RestHirdetoApiKeysServerResource extends ServerResource implements APIRestResource {

	private ObjectId hirdetoId = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		String hirdetoIdString = (String) this.getRequestAttributes().get("hirdetoId");
		hirdetoId = new ObjectId(hirdetoIdString);
	}
	
	@Override
	public Representation acceptJson(JsonRepresentation entity) throws Exception {
		Representation rep = null;
		Map<String, Object> repData = new HashMap<String, Object>();

		// Uj API Key generalasa es hozzaadasa a Hirdetohoz
		String apiKey = UUID.randomUUID().toString();
		repData.put("apiKey", apiKey);
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdeto> query = datastore.createQuery(Hirdeto.class);
		query.criteria("id").equal(this.hirdetoId);
		UpdateOperations<Hirdeto> ops = datastore.createUpdateOperations(Hirdeto.class).set("apiKey", apiKey);
		datastore.update(query, ops);
		
		rep = new JsonRepresentation(repData);	
		return rep;
	}

	@Override
	public Representation representJson() throws IOException {
		setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}

	@Override
	public Representation representHtml() throws IOException {
		getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}
}
