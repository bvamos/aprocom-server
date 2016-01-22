package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.mongodb.morphia.Datastore;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.FormResource;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.HirdetoHelper;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.MongoUtils;
import com.aprohirdetes.utils.PasswordHash;
import com.mongodb.MongoException;

import freemarker.template.Template;

public class UserJelszoCsereServerResource extends ServerResource implements
		FormResource {

	private String contextPath = "";
	private Session session = null;
	private Hirdeto hirdeto = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		this.session = SessionHelper.getSession(this);
		if(this.session != null) {
			this.hirdeto = HirdetoHelper.load(this.session.getHirdetoId());
		}
	}

	@Override
	public Representation representHtml() throws IOException {
		redirectPermanent("beallitasok");
		return null;
	}

	@Override
	public Representation accept(Form form) throws IOException {
		String message = null;
		String errorMessage = null;
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("felhasznalo_beallitasok.ftl.html");
		
		String jelszo1 = form.getFirstValue("hirdetoJelszo");
		String jelszo2 = form.getFirstValue("hirdetoJelszo2");
		
		// TODO: Validacio
		
		try {
			
			if(jelszo1==null || jelszo1.isEmpty() || jelszo2==null || jelszo2.isEmpty()) {
				throw new Exception("Mindkét jelszó mezőt ki kell tölteni!");
			}
			
			if(!jelszo1.equals(jelszo2)) {
				throw new Exception("A két jelszó mezőnek azonosnak kell lennie!");
			}
			
			hirdeto.setJelszo(PasswordHash.createHash(form.getFirstValue("hirdetoJelszo")));
			
			Datastore datastore = MongoUtils.getDatastore();
			datastore.save(hirdeto);
			
			message = "Az adatokat módosítottuk";
		} catch (MongoException me) {
			if(me.getCode()==11000) {
				errorMessage = "A megadott email cím már létezik!";
			} else {
				errorMessage = "Hiba történt az adatok mentése közben.";
			}
		} catch(Exception e) {
			errorMessage = e.getMessage();
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Beállítások");
		appDataModel.put("description", "Regisztrált felhasználó beállításainak szerkesztése. API beállítások. Profil törlése.");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("uzenet", message);
		dataModel.put("hibaUzenet", errorMessage);
		dataModel.put("session", this.session);
		dataModel.put("hirdeto", hirdeto);
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
