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
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.FormResource;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class UserBelepesServerResource extends ServerResource implements
		FormResource {

	private String contextPath = "";
	private Session session = null;
	private Hirdeto hirdeto = null;
	private String referrer = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		session = AproUtils.getSession(this);
		
		referrer = getQueryValue("referrer");
	}

	@Override
	public Representation representHtml() throws IOException {
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Belépés");
		appDataModel.put("description", "Hozzáférés a felhasználói oldalakhoz");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", session);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		if(session != null) {
			dataModel.put("hibaUzenet", "Már be vagy lépve. Ha mégsem Te vagy " + session.getFelhasznaloNev() + ", <a href=\"${app.contextRoot}/kilepes\">kattints ide és lépj ki</a> gyorsan!");
		}
		
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
		
		if ((this.hirdeto = SessionHelper.authenticate(felhasznaloNev, jelszo)) != null) {
			// Session betoltese
			Session session;
			session = SessionHelper.load(this.hirdeto.getId());
			if(session == null) {
				// Nincs session az adatbazisban, generalunk ujat, es elmentjuk
				session = new Session();
				session.setSessionId(UUID.randomUUID().toString());
				session.setHirdetoId(this.hirdeto.getId());
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
			datastore.update(this.hirdeto, datastore.createUpdateOperations(Hirdeto.class).set("utolsoBelepes", new Date()));

			// Atiranyitas a Hirdeto profiljara vagy a feladas oldalra
			// TODO: Relativ URL eseten kiegesziti, es h01.aprohirdtes.com lesz. Talan csak mas template-et kellene betolteni atiranyitas helyett
			if("feladas".equalsIgnoreCase(this.referrer)) {
				redirectPermanent(this.contextPath + "/feladas");
			} else {
				redirectPermanent(this.contextPath + "/felhasznalo/hirdetesek");
			}
		} else {
			errorMessage = "Hibás felhasználónév vagy jelszó";
			getLogger().severe("Sikertelen belepes: " + felhasznaloNev);
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Belépés");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("uzenet", message);
		dataModel.put("hibaUzenet", errorMessage);
		dataModel.put("email", felhasznaloNev);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
