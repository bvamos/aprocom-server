package com.aprohirdetes.server.apiv1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIRestResource;

public class SessionKilepesServerResource extends ServerResource implements
		APIRestResource {

	private String sessionId;
	private String felhasznaloNev;
	private String contextPath = "";

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();

		// Cookie-ban taroljuk a session azonositojat
		Cookie sessionCookie = getRequest().getCookies().getFirst("AproSession");
		if(sessionCookie != null) {
			sessionId = sessionCookie.getValue();
			System.out.println("AproSession cookie letezik: " + sessionCookie.getValue());
		}
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
	}

	@Override
	public Representation acceptJson(JsonRepresentation entity) throws Exception {
		Representation rep = null;
		if (entity != null) {
			JSONObject requestJson = entity.getJsonObject();
			Map<String, Object> repData = new HashMap<String, Object>();

			try {
				this.felhasznaloNev = requestJson.getString("felhasznaloNev");
			} catch (JSONException je) {
			}

			if (this.sessionId != null) {
				getLogger().info("Session cookie torlese: " + this.sessionId);
				//AproUtils.removeSessionCookie(this, this.sessionId);
				try {
					CookieSetting cookieSetting = new CookieSetting("AproSession", sessionId);
					cookieSetting.setVersion(0);
					cookieSetting.setAccessRestricted(true);
					cookieSetting.setPath(contextPath + "/");
					cookieSetting.setComment("Session Id");
					cookieSetting.setMaxAge(0);
					getResponse().getCookieSettings().add(cookieSetting);
					
					System.out.println("AproSession cookie torolve: " + felhasznaloNev);
				} catch(NullPointerException npe) {
					
				}
				
				// TODO: Session torlese az adatbazisbol
				
				// Response
				repData.put("msg", "Session lezarva");
				rep = new JsonRepresentation(repData);
			}
		} else {
			// POST request with no entity.
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		return rep;
	}

	@Override
	public Representation representJson() throws IOException {
		getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}
}
