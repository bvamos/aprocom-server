package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticHtmlResource;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class HirdetesHosszabbitasServerResource extends ServerResource implements
		StaticHtmlResource {

	private String contextPath = "";
	private ObjectId hirdetesId = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		String hirdetesIdString = (String) this.getRequestAttributes().get("hirdetesId");
		try {
			hirdetesId = new ObjectId(hirdetesIdString.replace("23afc87dd765476ad66c", ""));
		} catch(IllegalArgumentException iae) {
			getLogger().warning("Hibas hirdetesId: " + hirdetesIdString);
		}
	}

	@Override
	public Representation representHtml() throws IOException {
		String uzenet = null;
		String hibaUzenet = null;
		
		if (this.hirdetesId != null) {
			Calendar c = Calendar.getInstance(); 
			c.setTime(new Date()); 
			c.add(Calendar.DATE, 30);
			
			Datastore datastore = MongoUtils.getDatastore();
			Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
			query.criteria("id").equal(this.hirdetesId);
			UpdateOperations<Hirdetes> ops = datastore.createUpdateOperations(Hirdetes.class).set("lejar", c.getTime()).set("modositva", new Date());
			datastore.update(query, ops);
			
			getLogger().info("Hirdetes meghosszabbitva: " + this.hirdetesId.toString());
			uzenet = "Hirdetésedet sikeresen meghosszabbítottuk. Köszönjük a bizalmadat!";
		} else {
			hibaUzenet = "A megadott azonosítójú apróhirdetés nem létezik.";
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", this.contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Hirdetés meghosszabbítása");
		appDataModel.put("description", "Hamarosan lejáró apróhirdetés meghosszabbítása 30 nappal. A hirdetéseket bármikor meghosszabbíthatod, hogy ne törlődjön az Apróhirdetés.com-ról.");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", SessionHelper.getSession(this));
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("hirdetesId", hirdetesId!=null ? hirdetesId.toString() : null);
		dataModel.put("uzenet", uzenet);
		dataModel.put("hibaUzenet", hibaUzenet);
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("hosszabbitas.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
