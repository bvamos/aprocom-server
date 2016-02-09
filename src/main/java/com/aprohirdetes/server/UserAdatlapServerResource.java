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

public class UserAdatlapServerResource extends ServerResource implements
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
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("felhasznalo_adatlap.ftl.html");
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Profil");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		if(this.session == null) {
			ftl = AproApplication.TPL_CONFIG.getTemplate("forbidden.ftl.html");
		} else {
			dataModel.put("session", this.session);
			dataModel.put("hirdeto", this.hirdeto);
			dataModel.put("hirdetoTipusMsz", (hirdeto.getTipus()==2) ? "" : "checked");
			dataModel.put("hirdetoTipusCeg", (hirdeto.getTipus()==2) ? "checked" : "");
		}
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

	@Override
	public Representation accept(Form form) throws IOException {
		String message = null;
		String errorMessage = null;
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("felhasznalo_adatlap.ftl.html");
		
		// TODO: Email cim ellenorzese, vagy hagyjuk a unique indexre?
		
		hirdeto.setTipus(HirdetoHelper.getHirdetoTipus(form.getFirstValue("hirdetoTipus")));
		hirdeto.setNev(form.getFirstValue("hirdetoNev"));
		if(hirdeto.getTipus()==2) {
			// Ceg
			hirdeto.setCegNev(form.getFirstValue("hirdetoCegNev"));
		} else {
			hirdeto.setCegNev(null);
		}
		hirdeto.setEmail(form.getFirstValue("hirdetoEmail"));
		hirdeto.setTelefon(form.getFirstValue("hirdetoTelefon"));
		hirdeto.setOrszag(form.getFirstValue("hirdetoOrszag"));
		hirdeto.setIranyitoSzam(form.getFirstValue("hirdetoIranyitoSzam"));
		hirdeto.setTelepules(form.getFirstValue("hirdetoTelepules"));
		hirdeto.setCim(form.getFirstValue("hirdetoCim"));
		
		if(form.getFirstValue("hirdetoJelszo") != null && !form.getFirstValue("hirdetoJelszo").isEmpty()) {
			try {
				hirdeto.setJelszo(PasswordHash.createHash(form.getFirstValue("hirdetoJelszo")));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// TODO: Validacio
		
		// Mentes
		try {
			Datastore datastore = MongoUtils.getDatastore();
			datastore.save(hirdeto);
			
			message = "Az adatokat módosítottuk";
		} catch (MongoException me) {
			if(me.getCode()==11000) {
				errorMessage = "A megadott email cím már létezik!";
			} else {
				errorMessage = "Hiba történt az adatok mentése közben.";
			}
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Adatlap");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("uzenet", message);
		dataModel.put("hibaUzenet", errorMessage);
		dataModel.put("session", this.session);
		dataModel.put("hirdeto", hirdeto);
		dataModel.put("hirdetoTipusMsz", (hirdeto.getTipus()==2) ? "" : "checked");
		dataModel.put("hirdetoTipusCeg", (hirdeto.getTipus()==2) ? "checked" : "");
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
