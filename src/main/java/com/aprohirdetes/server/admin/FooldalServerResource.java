package com.aprohirdetes.server.admin;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticHtmlResource;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.KulcsszoCache;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.server.AproApplication;
import com.aprohirdetes.server.AproConfig;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class FooldalServerResource extends ServerResource implements
		StaticHtmlResource {

	private String contextPath = "";
	private Session session = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		//System.out.println(getRequest().getRootRef().toString());
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		this.session = SessionHelper.getSession(this);
	}
	
	@Override
	public Representation representHtml() throws IOException {
		Template indexFtl = AproApplication.TPL_CONFIG.getTemplate("admin_fooldal.ftl.html");
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Admin");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", this.session);
		
		if(!this.session.getHirdeto().isAdmin()) {
			indexFtl = AproApplication.TPL_CONFIG.getTemplate("forbidden.ftl.html");
		} else {
			dataModel.put("kulcsszavak", KulcsszoCache.getCacheByKulcsszo());
			dataModel.put("aktivHirdetesekSzama", getAktivHirdetesekSzama());
			dataModel.put("inaktivHirdetesekSzama", getInaktivHirdetesekSzama());
			dataModel.put("toroltHirdetesekSzama", getToroltHirdetesekSzama());
			dataModel.put("nemHitelesitettHirdetesekSzama", getNemHitelesitettHirdetesekSzama());
		}
		
		return new TemplateRepresentation(indexFtl, dataModel, MediaType.TEXT_HTML);
	}

	private long getAktivHirdetesekSzama() {
		long ret = 0;
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class).filter("statusz", Hirdetes.Statusz.AKTIV.value());
		
		ret = query.countAll();
		
		query = null;
		
		return ret;
	}
	
	private long getInaktivHirdetesekSzama() {
		long ret = 0;
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		query.criteria("statusz").in(Arrays.asList(Hirdetes.Statusz.INAKTIV_ELADVA.value(), Hirdetes.Statusz.INAKTIV.value(), Hirdetes.Statusz.INAKTIV_LEJART.value()));
		
		ret = query.countAll();
		
		query = null;
		
		return ret;
	}
	
	private long getToroltHirdetesekSzama() {
		long ret = 0;
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		query.criteria("statusz").equal(Hirdetes.Statusz.TOROLVE.value());
		
		ret = query.countAll();
		
		query = null;
		
		return ret;
	}
	
	private long getNemHitelesitettHirdetesekSzama() {
		long ret = 0;
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class).filter("statusz", Hirdetes.Statusz.UJ.value());
		
		ret = query.countAll();
		
		query = null;
		
		return ret;
	}
}
