package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.aprohirdetes.model.Kereses;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class UserKeresesekServerResource extends ServerResource implements
		FormResource {

	private String contextPath = "";
	private Session session = null;
	private boolean keresesAktiv = true;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		this.session = SessionHelper.getSession(this);
		
		this.keresesAktiv = "aktiv".equalsIgnoreCase((String) this.getRequestAttributes().get("tipus"));
	}

	@Override
	public Representation representHtml() throws IOException {
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("felhasznalo_keresesek.ftl");
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", this.contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Mentett kereséseim");
		appDataModel.put("description", "Regisztrált felhasználó mentett kereséseinek szerkesztése");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		if(this.session == null) {
			ftl = AproApplication.TPL_CONFIG.getTemplate("forbidden.ftl.html");
		} else {
			dataModel.put("session", this.session);

			// Keresesek lekerdezese
			Datastore datastore = MongoUtils.getDatastore();
			Query<Kereses> query = datastore.createQuery(Kereses.class);
			
			query.criteria("hirdetoId").equal(this.session.getHirdetoId());
			// Megjelenitjuk az inaktiv kereseseket is, hogy vissza lehessen allitani
			if(this.keresesAktiv) {
				query.criteria("statusz").equal(Kereses.Statusz.AKTIV.value());
			} else {
				query.criteria("statusz").equal(Kereses.Statusz.INAKTIV.value());
			}
			
			ArrayList<Kereses> keresesList = new ArrayList<Kereses>();
			for(Kereses k : query) {
				// Kereses mentese a listaba
				keresesList.add(k);
			}
			
			dataModel.put("keresesList", keresesList);
			dataModel.put("keresesAktiv", this.keresesAktiv);
		}
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

	@Override
	public Representation accept(Form form) throws IOException {
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("felhasznalo_keresesek.ftl");
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", this.contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Mentett kereséseim");
		appDataModel.put("description", "Regisztrált felhasználó mentett kereséseinek szerkesztése");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		if(this.session == null) {
			ftl = AproApplication.TPL_CONFIG.getTemplate("forbidden.ftl.html");
		} else {
			Kereses kereses = new Kereses();
			kereses.setNev(form.getFirstValue("keresesNev"));
			kereses.setKuldesGyakorisaga(Kereses.KuldesGyakorisaga.getValueByString(form.getFirstValue("kuldesGyakorisaga")));
			kereses.setEmail(this.session.getFelhasznaloEmail());
			
			Datastore datastore = MongoUtils.getDatastore();
			datastore.save(kereses);
			
			redirectPermanent(this.contextPath + "felhasznalo/keresesek/aktiv");
		}
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}
}
