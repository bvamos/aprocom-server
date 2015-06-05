package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.bson.types.ObjectId;
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
import com.aprohirdetes.model.Uzenet;
import com.aprohirdetes.model.UzenetHelper;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class UserUzenetServerResource extends ServerResource implements
		StaticHtmlResource {

	private String contextPath = "";
	private Session session = null;
	private String uzenetTipus = "beerkezett";
	private ObjectId uzenetId = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		this.session = AproUtils.getSession(this);
		
		try {
			this.uzenetTipus = (String) this.getRequestAttributes().get("tipus");
		} catch(Exception e) {
			getLogger().severe("Uzenet megjelenitese. Hibas a tipus: " + (String) this.getRequestAttributes().get("tipus"));
		}
		
		try {
			this.uzenetId = new ObjectId((String) this.getRequestAttributes().get("uzenetId"));
		} catch(Exception e) {
			getLogger().severe("Uzenet megjelenitese. Hibas az azonosito: " + (String) this.getRequestAttributes().get("uzenetId"));
		}
	}

	@Override
	public Representation representHtml() throws IOException {
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("felhasznalo_uzenet.ftl.html");
		
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

			if(uzenetId==null) {
				dataModel.put("hibaUzenet", "Hoppá, hibás az Üzenet azonosítója.");
			} else {
				// Uzenet lekerdezese
				Datastore datastore = MongoUtils.getDatastore();
				Query<Uzenet> query = datastore.createQuery(Uzenet.class);
				
				if("elkuldott".equalsIgnoreCase(uzenetTipus)) {
					query.criteria("feladoId").equal(this.session.getHirdetoId());
				} else {
					query.criteria("cimzettId").equal(this.session.getHirdetoId());
				}
				query.criteria("id").equal(uzenetId);
				query.criteria("torolve").equal(false);
				
				Uzenet uzenet = query.get();
				if(uzenet!=null) {
					dataModel.put("uzenet", uzenet);
					dataModel.put("uzenetTipus", this.uzenetTipus);
					UzenetHelper.setElolvasva(uzenetId);
				} else {
					dataModel.put("hibaUzenet", "Sajnos a megadott azonosítóval nincs Üzeneted.");
				}
			}
		}
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
