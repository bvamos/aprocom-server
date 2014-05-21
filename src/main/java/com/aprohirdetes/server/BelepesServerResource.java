package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.BelepesResource;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class BelepesServerResource extends ServerResource implements
		BelepesResource {

	private String contextPath = "";
	private Hirdeto hirdeto = null;
	private String referrer = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		referrer = getQueryValue("referrer");
	}

	@Override
	public Representation representHtml() throws IOException {
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Belépés");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproApplication.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", AproUtils.getSession(this));
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("belepes.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

	@Override
	public Representation accept(Form form) throws IOException {
		String message = null;
		String errorMessage = null;
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("belepes.ftl.html");
		
		String felhasznaloNev = form.getFirstValue("signinEmail");
		String jelszo = form.getFirstValue("signinPassword");
		String sessionId;
		
		if ((this.hirdeto = SessionHelper.authenticate(felhasznaloNev, jelszo)) != null) {
			// Session ID generalasa
			sessionId = UUID.randomUUID().toString();
			getLogger().info("Sikeres belepes: " + felhasznaloNev + "; AproSession: " + sessionId);
			
			// Session Cookie
			CookieSetting cookieSetting = new CookieSetting("AproSession", sessionId);
			cookieSetting.setVersion(0);
			cookieSetting.setAccessRestricted(true);
			cookieSetting.setPath(contextPath + "/");
			cookieSetting.setComment("Session Id");
			cookieSetting.setMaxAge(3600*24*7);
			getResponse().getCookieSettings().add(cookieSetting);
			
			// Session mentese az adatbazisba
			Session session = new Session();
			session.setSessionId(sessionId);
			session.setFelhasznaloNev(felhasznaloNev);
			session.setHirdetoId(this.hirdeto.getId());
			
			Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
			datastore.save(session);
			
			// Utolso belepes mentese
			datastore.update(this.hirdeto, datastore.createUpdateOperations(Hirdeto.class).set("utolsoBelepes", new Date()));

			// Atiranyitas a Hirdeto profiljara vagy a feladas oldalra
			if("feladas".equalsIgnoreCase(this.referrer)) {
				redirectPermanent(contextPath + "/feladas");
			} else {
				redirectPermanent(contextPath + "/felhasznalo/hirdetesek");
			}
		} else {
			errorMessage = "Hibás felhasználónév vagy jelszó";
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Belépés");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproApplication.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("uzenet", message);
		dataModel.put("hibaUzenet", errorMessage);
		dataModel.put("email", felhasznaloNev);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
