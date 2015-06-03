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
import com.aprohirdetes.model.Uzenet;
import com.aprohirdetes.model.UzenetHelper;
import com.aprohirdetes.utils.MailUtils;

public class HirdetesUzenetServerResource extends ServerResource implements
		APIRestResource {

	@Override
	public Representation acceptJson(JsonRepresentation entity) throws Exception {
		HashMap<String, String> result = new HashMap<String, String>();
		
		if (entity != null) {
			//System.out.println(entity.toString());
			JSONObject requestJson = entity.getJsonObject();
			String hirdetesId = requestJson.getString("hirdetesId");
			Hirdetes hirdetes = HirdetesHelper.load(hirdetesId);
			
			try {
				//System.out.println(entity.toString());
				
				if(hirdetes!=null) {
					String feladoNev = requestJson.getString("feladoNev");
					String feladoEmail = requestJson.getString("feladoEmail");
					String szoveg = requestJson.getString("uzenet");
					String body = "Kedves " + hirdetes.getHirdeto().getNev() + "!\n\n"
							+ szoveg + "\n\n"
							+ "Kapcsolódó hirdetés: \n"
							+ "  " + hirdetes.getCim() + "\n"
							+ "  https://www.aprohirdetes.com/hirdetes/" + hirdetes.getId() + "\n\n"
							+ "Üdvözlettel,\n"
							+ feladoNev + " (" + feladoEmail + ")\n";
					
					Uzenet uzenet = new Uzenet();
					uzenet.setCimzettEmail(hirdetes.getHirdeto().getEmail());
					uzenet.setCimzettId(hirdetes.getHirdetoId());
					uzenet.setFeladoEmail(requestJson.getString("feladoEmail"));
					uzenet.setTargy("Üzenet a weboldalról");
					uzenet.setSzoveg(body);
					uzenet.setKezbesitve(true);
					UzenetHelper.add(uzenet);
					
					if(!MailUtils.sendMailHirdeto(hirdetes, requestJson.getString("feladoNev"), requestJson.getString("feladoEmail"), requestJson.getString("uzenet"))) {
						//System.out.println("Nem sikerült a levél elküldése.");
						result.put("hibaUzenet", "Nem sikerült a levél elküldése.");
						setStatus(Status.SERVER_ERROR_INTERNAL);
					}
				} else {
					result.put("hibaUzenet", "Nincs ilyen Hirdetés.");
					setStatus(Status.SERVER_ERROR_INTERNAL);
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
