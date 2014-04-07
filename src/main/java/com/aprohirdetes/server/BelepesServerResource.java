package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.aprohirdetes.model.Helyseg;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MongoUtils;
import freemarker.template.Template;

public class BelepesServerResource extends ServerResource implements
		BelepesResource {

	private String contextPath = "";
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
	}

	@Override
	public Representation representHtml() throws IOException {
		
		// Legordulokhoz adatok feltoltese
		ArrayList<Kategoria> kategoriaList = KategoriaCache.getKategoriaListByParentId(null);
		for(Kategoria o : kategoriaList) {
			ArrayList<Kategoria> alkategoriak = KategoriaCache.getKategoriaListByParentId(o.getIdAsString());
			o.setAlkategoriaList(alkategoriak);
		}
		
		ArrayList<Helyseg> helysegList = HelysegCache.getHelysegListByParentId(null);
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Belépés");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", AproUtils.getSession(this));
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		
		
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
		
		if (SessionHelper.authenticate(felhasznaloNev, jelszo)) {
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
			
			// Session mentese az adatbaziba
			Session session = new Session();
			session.setSessionId(sessionId);
			session.setFelhasznaloNev(felhasznaloNev);
			
			Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
			datastore.save(session);

			redirectPermanent("");
		} else {
			errorMessage = "Hibás felhasználonév vagy jelszó";
		}
		
		// Adatmodell a Freemarker sablonhoz
		ArrayList<Kategoria> kategoriaList = KategoriaCache.getKategoriaListByParentId(null);
		for(Kategoria o : kategoriaList) {
			ArrayList<Kategoria> alkategoriak = KategoriaCache.getKategoriaListByParentId(o.getIdAsString());
			o.setAlkategoriaList(alkategoriak);
		}
		
		ArrayList<Helyseg> helysegList = HelysegCache.getHelysegListByParentId(null);

		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", getRequest().getRootRef().toString());
		appDataModel.put("htmlTitle", getApplication().getName() + " - Belépés");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		
		dataModel.put("app", appDataModel);
		dataModel.put("uzenet", message);
		dataModel.put("hibaUzenet", errorMessage);
		dataModel.put("email", felhasznaloNev);
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("hirdetesKategoria", "ingatlan");
		dataModel.put("hirdetesHelyseg", "magyarorszag");
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
