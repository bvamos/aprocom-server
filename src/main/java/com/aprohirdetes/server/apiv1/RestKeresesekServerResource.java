package com.aprohirdetes.server.apiv1;

import java.util.Date;

import org.json.JSONObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.model.Kereses;
import com.aprohirdetes.model.RestResponse;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.MongoUtils;

public class RestKeresesekServerResource extends ServerResource  {
	
	private Session session;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		session = SessionHelper.getSession(this);
	}
	
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
		
		if (entity == null) {
			// POST request with no entity.
			restResponse.addError(1028, "A hirdetes objektum nem lehet ures");
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return restResponse;
		}
		
		if(this.session == null) {
			// Authentikacios hiba
			restResponse.addError(1028, "A hirdetes objektum nem lehet ures");
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return restResponse;
		}
		
		JSONObject requestJson = entity.getJsonObject();
		
		// Kereses objektum letrehozasa
		Kereses kereses = new Kereses();
		kereses.setHirdetoId(this.session.getHirdetoId());
		kereses.setEmail(session.getFelhasznaloEmail());
		kereses.setUtolsoKuldes(new Date());
		
		// Mentett Kereses megnevezese
		if(requestJson.has("nev")) {
			kereses.setNev(requestJson.getString("nev"));
		} else {
			restResponse.setSuccess(false);
			restResponse.addError(0, "A mentett kereses neve (nev) nem lehet üres");
			return restResponse;
		}
		
		// Mentett Kereses url
		if(requestJson.has("url")) {
			kereses.setUrl(requestJson.getString("url"));
		} else {
			restResponse.setSuccess(false);
			restResponse.addError(0, "A mentett kereses url-je (url) nem lehet üres");
			return restResponse;
		}
		
		kereses.setKuldesGyakorisaga(Kereses.KuldesGyakorisaga.getValueByString(requestJson.getString("kuldesGyakorisaga")));
		
		try {
			// Mentes
			Datastore datastore = MongoUtils.getDatastore();
			Key<Kereses> key = datastore.save(kereses);
			
			restResponse.setSuccess(true);
			restResponse.addData("id", key.getId().toString());
		} catch (Exception e) {
			restResponse.setSuccess(false);
			restResponse.addError(0, "Hiba a Kereses mentese közben: " + e.getMessage());
		}
		
		return restResponse;
	}

	
}
