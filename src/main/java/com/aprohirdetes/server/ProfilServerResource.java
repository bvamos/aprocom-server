package com.aprohirdetes.server;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.RegisztracioResource;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MongoUtils;
import com.aprohirdetes.utils.PasswordHash;
import com.mongodb.MongoException;

import freemarker.template.Template;

public class ProfilServerResource extends ServerResource implements
		RegisztracioResource {

	private String contextPath = "";
	private Session session;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		this.session = AproUtils.getSession(this);
	}

	@Override
	public Representation representHtml() throws IOException {
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Profil");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", this.session);
		dataModel.put("hirdeto", session.getHirdeto());
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("profil.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

	@Override
	public Representation accept(Form form) throws IOException {
		String message = null;
		String errorMessage = null;
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("profil.ftl.html");
		
		// TODO: Email cim ellenorzese, vagy hagyjuk a unique indexre?
		
		Hirdeto ho = new Hirdeto();
		ho.setNev(form.getFirstValue("hirdetoNev"));
		ho.setEmail(form.getFirstValue("hirdetoEmail"));
		ho.setTelefon(form.getFirstValue("hirdetoTelefon"));
		ho.setOrszag(form.getFirstValue("hirdetoOrszag"));
		ho.setIranyitoSzam(form.getFirstValue("hirdetoIranyitoSzam"));
		ho.setTelepules(form.getFirstValue("hirdetoTelepules"));
		ho.setCim(form.getFirstValue("hirdetoCim"));
		
		try {
			ho.setJelszo(PasswordHash.createHash(form.getFirstValue("hirdetoJelszo")));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO: Validacio
		
		// Mentes
		try {
			Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
			datastore.save(ho);
			
			message = "Az adatokat módosítottuk";
		} catch (MongoException me) {
			if(me.getCode()==11000) {
				errorMessage = "A megadott email cím már létezik!";
			} else {
				errorMessage = "Hiba történt az adatok mentése közben.";
			}
			ftl = AproApplication.TPL_CONFIG.getTemplate("regisztracio.ftl.html");
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", getRequest().getRootRef().toString());
		appDataModel.put("htmlTitle", getApplication().getName() + " - Profil");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		
		dataModel.put("app", appDataModel);
		dataModel.put("uzenet", message);
		dataModel.put("hibaUzenet", errorMessage);
		dataModel.put("session", this.session);
		dataModel.put("hirdeto", ho);
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
