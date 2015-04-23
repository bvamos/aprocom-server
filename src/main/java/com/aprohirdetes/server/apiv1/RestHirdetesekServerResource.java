package com.aprohirdetes.server.apiv1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIRestResource;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.RestHiba;

public class RestHirdetesekServerResource extends ServerResource implements APIRestResource {

	@Override
	public Representation acceptJson(JsonRepresentation entity) throws Exception {
		Representation rep = null;
		Map<String, Object> repData = new HashMap<String, Object>();
		RestHiba hiba = null;
		
		if (entity != null) {
			JSONObject requestJson = entity.getJsonObject();
			
			if(!requestJson.has("cim")) {
				hiba = new RestHiba(1, "A hirdetes cim mezoje nem lehet ures");
				rep = new JsonRepresentation(hiba);
				return rep;
			}
			
			Hirdetes hirdetes = new Hirdetes();
			hirdetes.setId(new ObjectId());
			hirdetes.setCim(requestJson.getString("cim"));
			
			repData.put("id_str", hirdetes.getId().toString());
			repData.put("cim", hirdetes.getCim());
			rep = new JsonRepresentation(repData);
			
		} else {
			// POST request with no entity.
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return rep;
	}

	@Override
	public Representation representJson() throws IOException {
		setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}

}
