package com.aprohirdetes.server;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.aprohirdetes.model.Helyseg;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.HirdetoHelper;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.MailUtils;
import com.aprohirdetes.utils.MongoUtils;
import com.aprohirdetes.utils.PasswordHash;
import com.mongodb.MongoException;

import freemarker.template.Template;

public class UserRegisztracioServerResource extends ServerResource implements
		FormResource {

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
		appDataModel.put("htmlTitle", getApplication().getName() + " - Regisztráció");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", SessionHelper.getSession(this));
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("regisztracio.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

	@Override
	public Representation accept(Form form) throws IOException {
		String message = null;
		String errorMessage = null;
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("regisztracio_eredmeny.ftl.html");
		
		// TODO: Email cim ellenorzese, vagy hagyjuk a unique indexre?
		
		Hirdeto ho = new Hirdeto();
		ho.setTipus(HirdetoHelper.getHirdetoTipus(form.getFirstValue("hirdetoTipus")));
		ho.setNev(form.getFirstValue("hirdetoNev"));
		if(ho.getTipus()==2) {
			// Ceg
			ho.setCegNev(form.getFirstValue("hirdetoCegNev"));
		}
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
			Datastore datastore = MongoUtils.getDatastore();
			datastore.save(ho);
			
			message = "Köszönjük, hogy regisztráltál nálunk!";
			
			// Level kikuldese az aktivalo linkkel
			MailUtils.sendMailRegisztracio(ho);
		} catch (MongoException me) {
			if(me.getCode()==11000) {
				errorMessage = "A megadott email cím már létezik!";
			} else {
				errorMessage = "Hiba törtent a regisztracio közben.";
			}
			ftl = AproApplication.TPL_CONFIG.getTemplate("regisztracio.ftl.html");
		}
		
		// Cookie torlese, kileptetes, ha van ervenyes session
		SessionHelper.removeSessionCookie(this);
		
		// Adatmodell a Freemarker sablonhoz
		ArrayList<Kategoria> kategoriaList = KategoriaCache.getKategoriaListByParentId(null);
		for(Kategoria o : kategoriaList) {
			ArrayList<Kategoria> alkategoriak = KategoriaCache.getKategoriaListByParentId(o.getIdAsString());
			o.setAlkategoriaList(alkategoriak);
		}
		
		ArrayList<Helyseg> helysegList = HelysegCache.getHelysegListByParentId(null);

		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Regisztráció");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("uzenet", message);
		dataModel.put("hibaUzenet", errorMessage);
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("hirdetesKategoria", "ingatlan");
		dataModel.put("hirdetesHelyseg", "magyarorszag");
		dataModel.put("hirdeto", ho);
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}
	
}
