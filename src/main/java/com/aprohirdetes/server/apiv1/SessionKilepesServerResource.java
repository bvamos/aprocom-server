package com.aprohirdetes.server.apiv1;

import javax.servlet.ServletContext;

import org.restlet.Context;
import org.restlet.data.CookieSetting;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.APIRestResource;
import com.aprohirdetes.model.RestResponse;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;

public class SessionKilepesServerResource extends ServerResource implements
		APIRestResource {

	private Session session;
	private String contextPath = "";

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();

		session = SessionHelper.getSession(this);
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
	}

	@Override
	public RestResponse acceptJson(JsonRepresentation entity) {
		RestResponse response = new RestResponse();
		
		if (this.session != null) {
			Context.getCurrentLogger().info("Kilepes: " + this.session.getSessionId());
			
			//SessionHelper.removeSessionCookie(this);
			try {
				CookieSetting cookieSetting = new CookieSetting("AproSession", session.getSessionId());
				cookieSetting.setVersion(0);
				cookieSetting.setAccessRestricted(true);
				cookieSetting.setPath(contextPath + "/");
				cookieSetting.setComment("Session Id");
				cookieSetting.setMaxAge(0);
				getResponse().getCookieSettings().add(cookieSetting);
				
				Context.getCurrentLogger().info("Kilepes. AproSession cookie torolve: " + session.getSessionId());
				response.setSuccess(true);
			} catch(NullPointerException npe) {
				Context.getCurrentLogger().severe("Hiba a kilepes kozben: " + npe.getMessage());
				response.setSuccess(false);
				response.addError(0, "Hiba a kilepes kozben: " + npe.getMessage());
			}
			
			// TODO: Session torlese az adatbazisbol
		} else {
			// Nem is volt session
			response.addError(0, "Nem letezik a session");
		}

		return response;
	}

	@Override
	public RestResponse representJson() {
		return acceptJson(null);
	}
	
	@Override
	public RestResponse representHtml() {
		getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		return null;
	}
}
