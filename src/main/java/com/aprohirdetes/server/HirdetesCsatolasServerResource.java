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
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticHtmlResource;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesHelper;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class HirdetesCsatolasServerResource extends ServerResource implements
		StaticHtmlResource {

	private String contextPath = "";
	private Session session = null;
	private ObjectId hirdetesId = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		this.session = SessionHelper.getSession(this);
		
		String hirdetesIdString = (String) this.getRequestAttributes().get("hirdetesId");
		try {
			hirdetesId = new ObjectId(hirdetesIdString);
		} catch(IllegalArgumentException iae) {
			getLogger().warning("Hibas hirdetesId: " + hirdetesIdString);
		}
	}

	@Override
	public Representation representHtml() throws IOException {
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("felhasznalo_eszkozok.ftl.html");
		String uzenet = null;
		String hibaUzenet = null;
		
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
		
		Session session = SessionHelper.getSession(this);
		if(session == null) {
			ftl = AproApplication.TPL_CONFIG.getTemplate("forbidden.ftl.html");
		} else {
			Hirdetes hirdetes = HirdetesHelper.load(hirdetesId);
			if (hirdetes != null) {
				if(hirdetes.getHirdeto().getEmail().equalsIgnoreCase(this.session.getFelhasznaloEmail())) {
					Datastore datastore = MongoUtils.getDatastore();
					Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
					query.criteria("id").equal(this.hirdetesId);
					UpdateOperations<Hirdetes> ops = datastore.createUpdateOperations(Hirdetes.class).set("hirdetoId", this.session.getHirdetoId());
					datastore.update(query, ops);
					
					getLogger().info("Hirdetes csatolva: " + this.hirdetesId.toString());
					uzenet = "Hirdetésedet sikeresen hozzácsatoltuk felhasználói fiókodhoz.";
				} else {
					// Az email cim nem egyezik
					hibaUzenet = "A megadott azonosítójú apróhirdetés email címe nem " + this.session.getFelhasznaloEmail();
				}
			} else {
				hibaUzenet = "A megadott azonosítójú apróhirdetés nem létezik.";
			}
			dataModel.put("logoHirdetesekList", HirdetesHelper.getLogoHirdetesek(this.session.getFelhasznaloEmail()));
		}
		
		dataModel.put("uzenet", uzenet);
		dataModel.put("hibaUzenet", hibaUzenet);
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
