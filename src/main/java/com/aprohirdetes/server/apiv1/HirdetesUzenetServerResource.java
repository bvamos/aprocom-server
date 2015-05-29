package com.aprohirdetes.server.apiv1;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIRestResource;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesHelper;
import com.aprohirdetes.utils.MailUtils;

public class HirdetesUzenetServerResource extends ServerResource implements
		APIRestResource {

	@Override
	public Representation acceptJson(JsonRepresentation entity) throws Exception {
		HashMap<String, String> result = new HashMap<String, String>();
		
		if (entity != null) {
			System.out.println(entity.toString());
			JSONObject requestJson = entity.getJsonObject();
			String hirdetesId = requestJson.getString("hirdetesId");
			System.out.println(hirdetesId);
			Hirdetes hirdetes = HirdetesHelper.load(hirdetesId);
			
			try {
				System.out.println(entity.toString());
				
				if(hirdetes!=null) {
					if(!MailUtils.sendMailHirdeto(hirdetes, requestJson.getString("feladoNev"), requestJson.getString("feladoEmail"), requestJson.getString("uzenet"))) {
						result.put("hibaUzenet", "Nem sikerült a levél elküldése.");
					}
				} else {
					result.put("hibaUzenet", "Nincs ilyen Hirdetés.");
				}
			} catch (JSONException je) {
				getLogger().severe(je.getMessage());
			}
		} else {
			// POST request with no entity.
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return new JsonRepresentation(new JSONObject(result));
	}

	@Override
	public Representation representJson() throws IOException {
		getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}

}
