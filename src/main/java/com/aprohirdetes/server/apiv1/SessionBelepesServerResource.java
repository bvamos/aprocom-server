package com.aprohirdetes.server.apiv1;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIRestResource;
import com.aprohirdetes.exception.AproException;
import com.aprohirdetes.model.RestResponse;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;

public class SessionBelepesServerResource extends ServerResource implements
		APIRestResource {

	private String felhasznaloNev;
	private String jelszo;

	@Override
	public RestResponse acceptJson(JsonRepresentation entity) {
		RestResponse response = new RestResponse();
		
		if (entity != null) {
			JSONObject requestJson = null;

			try {
				requestJson = entity.getJsonObject();
				this.felhasznaloNev = requestJson.getString("felhasznaloNev");
				this.jelszo = requestJson.getString("jelszo");
			} catch (JSONException je) {
				setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Hianyzo felhasznalonev vagy jelszo");
				response.setSuccess(false);
				response.addError(2004, "Hianyzo felhasznalonev vagy jelszo");
				return response;
			}

			Session session = null;
			try {
				session = SessionHelper.login(this.felhasznaloNev, this.jelszo, getRequest().getClientInfo().getAddress());

				// Session Cookie
				SessionHelper.setSessionCookie(this, session.getSessionId());
				
				// Valasz
				response.setSuccess(true);
				response.addData("felhasznaloNev", this.felhasznaloNev);
				response.addData("sessionId", session.getSessionId());
			} catch(AproException ae) {
				setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Hibas felhasznalonev vagy jelszo");
				response.setSuccess(false);
				response.addError(2002, "Hibas felhasznalonev vagy jelszo");
			}
		} else {
			// POST request with no entity.
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}

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
