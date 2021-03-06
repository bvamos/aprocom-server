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
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticHtmlResource;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.model.Uzenet;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class UserUzenetekServerResource extends ServerResource implements
		StaticHtmlResource {

	private String contextPath = "";
	private Session session = null;
	private String uzenetTipus = "beerkezett";
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		this.session = SessionHelper.getSession(this);
		
		try {
			this.uzenetTipus = (String) this.getRequestAttributes().get("tipus");
		} catch(Exception e) {
			getLogger().severe("Uzenet megjelenitese. Hibas a tipus: " + (String) this.getRequestAttributes().get("tipus"));
		}
	}

	@Override
	public Representation representHtml() throws IOException {
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("felhasznalo_uzenetek.ftl.html");
		
		int countBeerkezett = 0;
		int countElkuldott = 0;
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", this.contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Hirdetéseim");
		appDataModel.put("description", "Regisztrált felhasználó hirdetéseinek szerkesztése");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		if(this.session == null) {
			ftl = AproApplication.TPL_CONFIG.getTemplate("forbidden.ftl.html");
		} else {
			dataModel.put("session", this.session);
			dataModel.put("uzenetTipus", this.uzenetTipus);

			// Uzenetek lekerdezese
			final Datastore datastore = MongoUtils.getDatastore();
			Query<Uzenet> query = datastore.createQuery(Uzenet.class);
			
			query.criteria("torolve").equal(false);
			query.or(query.criteria("cimzettId").equal(this.session.getHirdetoId()), query.criteria("feladoId").equal(this.session.getHirdetoId()));
			
			query.order("-id");
			
			ArrayList<Uzenet> uzenetList = new ArrayList<Uzenet>();
			for(Uzenet h : query) {
				if(h.getFeladoId() != null && this.session.getHirdetoId().equals(h.getFeladoId())) {
					countElkuldott++;
					if("elkuldott".equalsIgnoreCase(this.uzenetTipus)) {
						// Uzenet mentese a listaba
						uzenetList.add(h);
					}
				}
				if(h.getCimzettId() != null && this.session.getHirdetoId().equals(h.getCimzettId())) {
					countBeerkezett++;
					if("beerkezett".equalsIgnoreCase(this.uzenetTipus)) {
						// Uzenet mentese a listaba
						uzenetList.add(h);
					}
				}
			}
			
			dataModel.put("uzenetList", uzenetList);
			dataModel.put("countBeerkezett", countBeerkezett);
			dataModel.put("countElkuldott", countElkuldott);
		}
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
