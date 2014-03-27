package com.aprohirdetes.server.apiv1;

import java.util.UUID;

import org.json.JSONObject;
import org.restlet.data.CookieSetting;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APISessionBelepesResource;

public class SessionBelepesServerResource extends ServerResource implements
		APISessionBelepesResource {

	private String felhasznaloNev;
	private String jelszo;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();

	}

	public Representation accept(JsonRepresentation entity) throws Exception {
		Representation rep = null;
		if (entity != null) {
			JSONObject requestJson = entity.getJsonObject();

			this.felhasznaloNev = requestJson.getString("felhasznaloNev");
			this.jelszo = requestJson.getString("jelszo");

			if ("birka".equals(felhasznaloNev)) {
				CookieSetting cookieSetting = new CookieSetting("AproSession",
						UUID.randomUUID().toString());
				cookieSetting.setVersion(0);
				cookieSetting.setAccessRestricted(true);
				cookieSetting.setPath(getRequest().getRootRef().toString());
				cookieSetting.setComment("Session Id");
				cookieSetting.setMaxAge(3600);
				getResponse().getCookieSettings().add(cookieSetting);

				rep = new JsonRepresentation(requestJson.toString());
			}
		} else {
			// POST request with no entity.
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		return rep;
	}
}
