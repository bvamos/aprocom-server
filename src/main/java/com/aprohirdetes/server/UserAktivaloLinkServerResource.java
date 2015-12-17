package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.FormResource;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.MailUtils;
import com.aprohirdetes.utils.MongoUtils;
import freemarker.template.Template;

public class UserAktivaloLinkServerResource extends ServerResource implements
		FormResource {

	private String contextPath = "";
	private Hirdeto hirdeto = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
	}

	@Override
	public Representation representHtml() throws IOException {
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Ajaj, nem jött meg a levél");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", SessionHelper.getSession(this));
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("ujaktivalolink.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

	@Override
	public Representation accept(Form form) throws IOException {
		String message = null;
		String errorMessage = null;
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("ujaktivalolink.ftl.html");
		
		String felhasznaloNev = form.getFirstValue("email");
		
		Datastore datastore = MongoUtils.getDatastore();
		
		Query<Hirdeto> query = datastore.createQuery(Hirdeto.class);
		query.criteria("email").equal(felhasznaloNev);
		hirdeto = query.get();
		
		if(hirdeto != null) {
			if(MailUtils.sendMailRegisztracio(hirdeto)) {
			
			} else {
				errorMessage = "Hiba történt a levél kiküldése közben.";
			}
			
		} else {
			errorMessage = "A megadott email címmel sajnos nincs regisztrált Hirdetőnk! Kattints a fenti Regisztráció fülre gyorsan!";
		}
		
		// Varjunk picit, hogy ne lehessen ezer ilyen kerest benyomni egy masodperc alatt
		try {
		    Thread.sleep(3000);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}

		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Ajaj, nem jött meg a levél");
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
