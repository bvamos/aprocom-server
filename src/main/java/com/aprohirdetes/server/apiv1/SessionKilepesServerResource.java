package com.aprohirdetes.server.apiv1;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Cookie;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APISessionKilepesResource;
import com.aprohirdetes.utils.AproUtils;

public class SessionKilepesServerResource extends ServerResource implements
		APISessionKilepesResource {

	private String sessionId;
	private String felhasznaloNev;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();

		// Cookie-ban taroljuk a session azonositojat
		Cookie sessionCookie = getRequest().getCookies().getFirst("AproSession");
		if(sessionCookie != null) {
			sessionId = sessionCookie.getValue();
			System.out.println("AproSession cookie letezik: " + sessionCookie.getValue());
		}
	}

	public Representation accept(JsonRepresentation entity) throws Exception {
		Representation rep = null;
		if (entity != null) {
			JSONObject requestJson = entity.getJsonObject();
			Map<String, Object> repData = new HashMap<String, Object>();

			try {
				this.felhasznaloNev = requestJson.getString("felhasznaloNev");
			} catch (JSONException je) {
			}

			if (this.sessionId != null) {
				AproUtils.removeSessionCookie(this, this.sessionId);
				repData.put("msg", "Session lezarva");
				rep = new JsonRepresentation(repData);
			}
		} else {
			// POST request with no entity.
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		return rep;
	}
}
