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
import org.mongodb.morphia.query.UpdateOperations;
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

public class UserKeresesServerResource extends ServerResource implements
		FormResource {

	private String contextPath = "";
	private Session session = null;
	private ObjectId keresesId;
	private String muvelet;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		this.session = SessionHelper.getSession(this);
		
		try {
			this.keresesId = new ObjectId((String) this.getRequestAttributes().get("keresesId"));
			this.muvelet = (String) this.getRequestAttributes().get("muvelet");
		} catch(Exception e) {
			getLogger().severe(e.getMessage());
		}
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
		dataModel.put("keresesAktiv", true);
		
		if(this.session == null) {
			ftl = AproApplication.TPL_CONFIG.getTemplate("forbidden.ftl.html");
			return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
		} else {
			dataModel.put("session", this.session);

			// Keresesek lekerdezese
			Datastore datastore = MongoUtils.getDatastore();
			Query<Kereses> query = datastore.createQuery(Kereses.class);
			
			query.criteria("hirdetoId").equal(this.session.getHirdetoId());
			query.criteria("id").equal(this.keresesId);
			
			UpdateOperations<Kereses> ops = null;
			switch(this.muvelet) {
			case "torol":
				ops = datastore.createUpdateOperations(Kereses.class).set("statusz", Kereses.Statusz.TOROLVE.value());
				datastore.update(query, ops);
				dataModel.put("uzenet", "A mentett keresést töröltük.");
				break;
			case "bekapcsol":
				ops = datastore.createUpdateOperations(Kereses.class).set("statusz", Kereses.Statusz.AKTIV.value());
				datastore.update(query, ops);
				dataModel.put("uzenet", "A mentett keresést kikapcsoltuk.");
				break;
			case "kikapcsol":
				ops = datastore.createUpdateOperations(Kereses.class).set("statusz", Kereses.Statusz.INAKTIV.value());
				datastore.update(query, ops);
				dataModel.put("uzenet", "A mentett keresést bekapcsoltuk.");
				break;
			default:
				getContext().getLogger().warning("Ismeretlen muvelet: " + this.muvelet);
			}
			
			redirectSeeOther(this.contextPath + "/felhasznalo/keresesek/aktiv");
		}
		
		//return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
		return null;
	}

	@Override
	public Representation accept(Form form) throws IOException {
		return null;
	}
}
