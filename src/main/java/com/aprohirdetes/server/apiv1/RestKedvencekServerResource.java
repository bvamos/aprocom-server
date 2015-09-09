package com.aprohirdetes.server.apiv1;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.model.HirdetesHelper;
import com.aprohirdetes.model.HirdetesKedvenc;
import com.aprohirdetes.model.RestResponse;
import com.aprohirdetes.utils.MongoUtils;

public class RestKedvencekServerResource extends ServerResource  {
	
	@Get
	public RestResponse representJson() throws Exception {
		RestResponse restResponse = new RestResponse();
		
		// TODO: API: Kedvencek listaja (GET)
		getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		
		return restResponse;
	}

	@Post("json")
	public RestResponse acceptJson(JsonRepresentation entity) throws Exception {
		RestResponse restResponse = new RestResponse();
		
		if (entity != null) {
			JSONObject requestJson = entity.getJsonObject();
			
			// Kedvenc objektum letrehozasa
			HirdetesKedvenc kedvenc = new HirdetesKedvenc();
			
			// Felado azonositoja. AproApplication.apiAuthFilterben kerul beallitasra
			// Nem feltetlenul azonos a Hirdeto.id-val
			ObjectId hirdetoId = (ObjectId) getRequest().getAttributes().get("feladoId");
			kedvenc.setHirdetoId(hirdetoId);
			
			if(requestJson.has("hirdetesId")) {
				try {
					ObjectId hirdetesId = new ObjectId(requestJson.getString("hirdetesId"));
					kedvenc.setHirdetesId(hirdetesId);
					kedvenc.setHirdetes(HirdetesHelper.load(hirdetesId));
					
					// Mentes
					Datastore datastore = MongoUtils.getDatastore();
					Key<HirdetesKedvenc> key = datastore.save(kedvenc);
					
					restResponse.setSuccess(true);
					restResponse.addData("id", key.getId().toString());
					restResponse.addData("hirdetoId", hirdetoId.toString());
					restResponse.addData("hirdetesId", hirdetesId.toString());
				} catch (Exception e) {
					restResponse.setSuccess(false);
					restResponse.addError(0, "A hirdetes azonositoja hibas: " + requestJson.getString("hirdetesId"));
				}
			} else {
				restResponse.setSuccess(false);
				restResponse.addError(0, "A hirdetes azonositoja (hirdetesId) nem lehet üres");
			}
			
			
		} else {
			// POST request with no entity.
			restResponse.addError(1028, "A hirdetes objektum nem lehet ures");
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return restResponse;
	}

	
}
