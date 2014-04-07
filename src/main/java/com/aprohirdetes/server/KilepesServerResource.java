package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.mongodb.morphia.Datastore;
import org.restlet.data.CookieSetting;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.KilepesResource;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class KilepesServerResource extends ServerResource implements
		KilepesResource {

	private String contextPath = "";
	private Session session = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		this.session = AproUtils.getSession(this);
	}

	@Override
	public Representation representHtml() throws IOException {
		
		if (this.session != null) {
			getLogger().info("Session cookie torlese: " + session.getSessionId());
			//AproUtils.removeSessionCookie(this, this.sessionId);
			try {
				CookieSetting cookieSetting = new CookieSetting("AproSession", session.getSessionId());
				cookieSetting.setVersion(0);
				cookieSetting.setAccessRestricted(true);
				cookieSetting.setPath(contextPath + "/");
				cookieSetting.setComment("Session Id");
				cookieSetting.setMaxAge(0);
				getResponse().getCookieSettings().add(cookieSetting);
				
				System.out.println("AproSession cookie torolve: " + session.getSessionId());
			} catch(NullPointerException npe) {
				
			}
			
			// Session torlese az adatbazisbol
			Datastore datastore = MongoUtils.getDatastore();
			datastore.delete(this.session);
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", this.contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Kilépés");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("kilepes.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
