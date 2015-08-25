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
import org.mongodb.morphia.query.UpdateResults;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticHtmlResource;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class UserAktivalasServerResource extends ServerResource implements
		StaticHtmlResource {

	private String contextPath = "";
	private ObjectId hirdetoId = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		String hirdetoIdString = (String) this.getRequestAttributes().get("hirdetoId");
		try {
			hirdetoId = new ObjectId(hirdetoIdString);
		} catch(IllegalArgumentException iae) {
			getLogger().warning("Hibas hirdetoId: " + hirdetoIdString);
		}
	}

	@Override
	public Representation representHtml() throws IOException {
		String uzenet = null;
		String hibaUzenet = null;
		
		if (this.hirdetoId != null) {
			Datastore datastore = MongoUtils.getDatastore();
			Query<Hirdeto> query = datastore.createQuery(Hirdeto.class);
			query.criteria("id").equal(this.hirdetoId);
			UpdateOperations<Hirdeto> ops = datastore.createUpdateOperations(Hirdeto.class).set("hitelesitve", true);
			UpdateResults<Hirdeto> updateResults = datastore.update(query, ops);
			
			if(updateResults.getUpdatedCount()>0) {
				getLogger().info("Felhasznalo aktivalva: " + this.hirdetoId.toString());
				uzenet = "Hozzáférésedet sikeresen aktiváltuk, most már be tudsz lépni az email címeddel és a megadott jelszavaddal. "
						+ "Köszönjük, hogy minket választottál!";
			} else {
				getLogger().warning("Nincs ilyen aktivalando fiok: " + this.hirdetoId.toString());
				hibaUzenet = "A megadott Felhasználó nem létezik, vagy már sikeresen aktiváltad magad.";
			}
			
		} else {
			hibaUzenet = "A megadott Felhasználó nem létezik, vagy már sikeresen aktiváltad magad.";
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", this.contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Hirdető aktiválása");
		appDataModel.put("description", "Új felhasználó aktiválása. A hirdetéseket ingyen feladhatod, de csak regisztrált felhasználóknt tudsz igazán könnyen hirdetni oldalunkon.");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("uzenet", uzenet);
		dataModel.put("hibaUzenet", hibaUzenet);
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("felhasznalo_aktivalas.ftl");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
