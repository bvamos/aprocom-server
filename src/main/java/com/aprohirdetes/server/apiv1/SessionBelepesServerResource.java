package com.aprohirdetes.server.apiv1;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APISessionBelepesResource;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.server.AproApplication;
import com.aprohirdetes.utils.MongoUtils;

public class SessionBelepesServerResource extends ServerResource implements
		APISessionBelepesResource {

	private String sessionId;
	private String felhasznaloNev;
	private String jelszo;
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

	public Representation accept(JsonRepresentation entity) throws Exception {
		Representation rep = null;
		if (entity != null) {
			JSONObject requestJson = entity.getJsonObject();
			Map<String, Object> repData = new HashMap<String, Object>();

			try {
				this.felhasznaloNev = requestJson.getString("felhasznaloNev");
				this.jelszo = requestJson.getString("jelszo");
			} catch (JSONException je) {
				setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Hianyzo felhasznalonev vagy jelszo");
				return rep;
			}

			if (SessionHelper.authenticate(felhasznaloNev, jelszo)) {
				sessionId = UUID.randomUUID().toString();
				getLogger().info("Sikeres belepes. AproSession: " + sessionId);
				
				// Session Cookie
				CookieSetting cookieSetting = new CookieSetting("AproSession", sessionId);
				cookieSetting.setVersion(0);
				cookieSetting.setAccessRestricted(true);
				cookieSetting.setPath(contextPath + "/");
				cookieSetting.setComment("Session Id");
				cookieSetting.setMaxAge(3600*24*7);
				getResponse().getCookieSettings().add(cookieSetting);
				
				// Session mentese az adatbaziba
				Session session = new Session();
				session.setSessionId(sessionId);
				session.setFelhasznaloNev(felhasznaloNev);
				
				Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
				Key<Session> id = datastore.save(session);

				// Valasz
				repData.put("felhasznaloNev", this.felhasznaloNev);
				repData.put("sessionId", sessionId);
				
				rep = new JsonRepresentation(repData);
			} else {
				try {
					CookieSetting cookieSetting = new CookieSetting("AproSession", sessionId);
					cookieSetting.setVersion(0);
					cookieSetting.setAccessRestricted(true);
					cookieSetting.setPath(contextPath + "/");
					cookieSetting.setComment("Session Id");
					cookieSetting.setMaxAge(0);
					getResponse().getCookieSettings().add(cookieSetting);
					
					System.out.println("AproSession cookie torolve");
				} catch(NullPointerException npe) {
					
				}
				repData.put("errorMsg", "Hibas felhasznalonev vagy jelszo");
				rep = new JsonRepresentation(repData);
				setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Hibas felhasznalonev vagy jelszo");
			}
		} else {
			// POST request with no entity.
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		return rep;
	}
}
