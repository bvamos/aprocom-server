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
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.HirdetesResource;
import com.aprohirdetes.model.Helyseg;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesKep;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class HirdetesServerResource extends ServerResource implements
		HirdetesResource {

	private String contextPath = "";
	/**
	 * Hirdetes azonositoja
	 */
	private ObjectId hirdetesId;
	
	private Hirdetes hirdetes = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		try {
			this.hirdetesId = new ObjectId((String) this.getRequestAttributes().get("hirdetesId"));
			
			Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
			Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);

			query.criteria("id").equal(this.hirdetesId);
			
			hirdetes = query.get();
		} catch(IllegalArgumentException iae) {
			getLogger().severe("Hirdetes megjelenitese: nem jo az id formatuma: " + (String) this.getRequestAttributes().get("hirdetesId"));
		}
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
	}

	public Representation representHtml() throws IOException {
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - " + hirdetes.getCim());
		appDataModel.put("description", hirdetes.getCim());
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", AproUtils.getSession(this));
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		// Hirdetes adatai
		if(hirdetes != null) {
			// Kereses eredmenyeben levo Hirdetes objektum feltoltese kepekkel, egyeb adatokkal a megjeleniteshez
			hirdetes.getEgyebMezok().put("tipusNev", (hirdetes.getTipus()==HirdetesTipus.KINAL) ? "Kínál" : "Keres");
			
			Kategoria kat = KategoriaCache.getCacheById().get(hirdetes.getKategoriaId());
			hirdetes.getEgyebMezok().put("kategoriaNev", (kat!=null) ? KategoriaCache.getKategoriaNevChain(hirdetes.getKategoriaId()) : "");
			hirdetes.getEgyebMezok().put("kategoriaUrlNev", (kat!=null) ? kat.getUrlNev() : "");
			
			Helyseg hely = HelysegCache.getCacheById().get(hirdetes.getHelysegId());
			hirdetes.getEgyebMezok().put("helysegNev", (hely!=null) ? HelysegCache.getHelysegNevChain(hirdetes.getHelysegId()) : "");
			hirdetes.getEgyebMezok().put("helysegUrlNev", (hely!=null) ? hely.getUrlNev() : "");
			
			hirdetes.getEgyebMezok().put("feladvaSzoveg", AproUtils.getHirdetesFeladvaSzoveg(hirdetes.getFeladasDatuma()));
			
			// Kepek
			Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
			Query<HirdetesKep> kepekQuery = datastore.createQuery(HirdetesKep.class);
			kepekQuery.criteria("hirdetesId").equal(hirdetes.getId());
			
			for(HirdetesKep kep : kepekQuery) {
				hirdetes.getKepek().add(kep);
			}
			
			dataModel.put("hirdetes", hirdetes);
		} else {
			// Nincs ilyen hirdetes
			getLogger().severe("Nincs meg a megadott hirdetes: " + (String) this.getRequestAttributes().get("hirdetesId"));
			
			dataModel.put("hibaUzenet", "A megadott hirdetés nem található.");
		}
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("hirdetes.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
