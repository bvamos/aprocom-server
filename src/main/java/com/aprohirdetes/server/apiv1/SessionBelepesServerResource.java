package com.aprohirdetes.server.apiv1;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.mongodb.morphia.Datastore;
import org.restlet.data.CookieSetting;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIRestResource;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.MongoUtils;

public class SessionBelepesServerResource extends ServerResource implements
		APIRestResource {

	private String sessionId;
	private String felhasznaloNev;
	private String jelszo;
	private String contextPath = "";

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();

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
				this.jelszo = requestJson.getString("jelszo");
			} catch (JSONException je) {
				setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Hianyzo felhasznalonev vagy jelszo");
				return rep;
			}

			Hirdeto hirdeto = null;
			if ((hirdeto = SessionHelper.authenticate(felhasznaloNev, jelszo)) != null) {
				// Session betoltese
				Session session;
				session = SessionHelper.load(hirdeto.getId());
				if(session == null) {
					// Nincs session az adatbazisban, generalunk ujat, es elmentjuk
					session = new Session();
					session.setSessionId(UUID.randomUUID().toString());
					session.setHirdetoId(hirdeto.getId());
				}
				getLogger().info("Sikeres belepes: " + felhasznaloNev + "; AproSession: " + session.getSessionId());
				
				// Session Cookie
				CookieSetting cookieSetting = new CookieSetting("AproSession", session.getSessionId());
				cookieSetting.setVersion(0);
				cookieSetting.setAccessRestricted(true);
				cookieSetting.setPath(contextPath + "/");
				cookieSetting.setComment("Session Id");
				cookieSetting.setMaxAge(3600*24*7);
				getResponse().getCookieSettings().add(cookieSetting);
				
				// Session mentese az adatbazisba
				Datastore datastore = MongoUtils.getDatastore();
				datastore.save(session);
				
				// Utolso belepes mentese
				datastore.update(hirdeto, datastore.createUpdateOperations(Hirdeto.class).set("utolsoBelepes", new Date()));

				// Valasz
				repData.put("felhasznaloNev", this.felhasznaloNev);
				repData.put("sessionId", sessionId);
				
				rep = new JsonRepresentation(repData);
			} else {
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

	@Override
	public Representation representJson() throws IOException {
		getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}
	
}
