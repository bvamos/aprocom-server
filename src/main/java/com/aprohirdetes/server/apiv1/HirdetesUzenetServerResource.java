package com.aprohirdetes.server.apiv1;

import java.io.IOException;
import java.util.HashMap;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
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
					String recaptcha = requestJson.getString("grec");
					
					final Context context = new Context();
				    context.getParameters().set("maxConnectionsPerHost", "20");

				    final ClientResource requestResource = new ClientResource(context, Method.POST, "https://www.google.com/recaptcha/api/siteverify");
				    requestResource.getClientInfo().setAgent("Example-Client/1.0");
				    
				    Form payload = new Form();
				    payload.add("secret", "6LeH8QcTAAAAAHWVTw0huUZjwQAKR6Nz23aF-gDD");
				    payload.add("response", recaptcha);
				    Representation r = requestResource.post(payload, MediaType.MULTIPART_FORM_DATA);
				    if(requestResource.getStatus().isSuccess()) {
				    	JSONObject recaptchaResponse = new JSONObject(r.getText());
				    	Boolean recaptchaResponseSuccess = (Boolean) recaptchaResponse.get("success");
				    	if(recaptchaResponseSuccess) {
				    		// Recaptcha valasz true, nem robot
				    		
				    		// Uzenet szerkesztese
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
							if(requestJson.getString("feladoId")!=null && !requestJson.getString("feladoId").isEmpty())
								uzenet.setFeladoId(new ObjectId(requestJson.getString("feladoId")));
							uzenet.setTargy("Üzenet: " + hirdetes.getCim());
							uzenet.setSzoveg(body);
							uzenet.setKezbesitve(true);
							UzenetHelper.add(uzenet);
							
							// Level kikuldese
							if(!MailUtils.sendMailHirdeto(hirdetes, requestJson.getString("feladoNev"), requestJson.getString("feladoEmail"), requestJson.getString("uzenet"))) {
								//System.out.println("Nem sikerült a levél elküldése.");
								result.put("hibaUzenet", "Nem sikerült a levél elküldése.");
								setStatus(Status.SERVER_ERROR_INTERNAL, "Nem sikerült a levél elküldése.");
							}
				    	} else {
				    		// Racaptcha valasz false, robot
				    		result.put("hibaUzenet", "Robotok kíméljenek!");
							setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Robotok kíméljenek!");
				    	}
				    } else {
				    	result.put("hibaUzenet", "Hiba a robot-ellenőrzés közben");
						setStatus(Status.SERVER_ERROR_INTERNAL, "Hiba a robot-ellenőrzés közben");
				    }
				    
				} else {
					result.put("hibaUzenet", "Nincs ilyen Hirdetés.");
					setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Nincs ilyen Hirdetés.");
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

	@Override
	public Representation representHtml() throws IOException {
		getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}
}
