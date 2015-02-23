package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.HirdetesResource;
import com.aprohirdetes.model.Attributum;
import com.aprohirdetes.model.AttributumCache;
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

	@SuppressWarnings("unchecked")
	public Representation representHtml() throws IOException {
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName());
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproApplication.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", AproUtils.getSession(this));
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		// Hirdetes adatai
		if(hirdetes != null) {
			if(!hirdetes.isTorolve()) {
				if(hirdetes.isHitelesitve()) {
					// HTML Title modositasa
					((HashMap<String, Object>) dataModel.get("app")).put("htmlTitle", getApplication().getName() + " - " + hirdetes.getCim());
					((HashMap<String, Object>) dataModel.get("app")).put("description", "Apróhirdetés: " + hirdetes.getCim());
					
					// Hirdetes szovegenek modositasa: ujsorok
					hirdetes.setSzoveg(hirdetes.getSzoveg().replaceAll("\\r\\n", "<br>"));
					
					// Mongo Datastore
					Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
					
					// Megjelenes szamanak novelese
					Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
					query.criteria("id").equal(this.hirdetesId);
					UpdateOperations<Hirdetes> ops = datastore.createUpdateOperations(Hirdetes.class).set("megjelenes", hirdetes.getMegjelenes()+1);
					datastore.update(query, ops);
				    hirdetes.increaseMegjelenesByOne();
				    
					
					// Kereses eredmenyeben levo Hirdetes objektum feltoltese kepekkel, egyeb adatokkal a megjeleniteshez
					hirdetes.getEgyebMezok().put("tipusNev", (hirdetes.getTipus()==HirdetesTipus.KINAL) ? "Kínál" : "Keres");
					
					Kategoria kat = KategoriaCache.getCacheById().get(hirdetes.getKategoriaId());
					hirdetes.getEgyebMezok().put("kategoriaNev", (kat!=null) ? KategoriaCache.getKategoriaNevChain(hirdetes.getKategoriaId()) : "");
					hirdetes.getEgyebMezok().put("kategoriaUrlNev", (kat!=null) ? kat.getUrlNev() : "");
					
					Helyseg hely = HelysegCache.getCacheById().get(hirdetes.getHelysegId());
					hirdetes.getEgyebMezok().put("helysegNev", (hely!=null) ? HelysegCache.getHelysegNevChain(hirdetes.getHelysegId()) : "");
					hirdetes.getEgyebMezok().put("helysegUrlNev", (hely!=null) ? hely.getUrlNev() : "");
					
					hirdetes.getEgyebMezok().put("feladvaSzoveg", AproUtils.getHirdetesFeladvaSzoveg(hirdetes.getFeladasDatuma()));
					hirdetes.getEgyebMezok().put("lejaratSzoveg", AproUtils.getHirdetesLejaratSzoveg(hirdetes.getLejaratDatuma()));
					
					// Attributumok cimenek, ertekenek (ha kell) atirasa
					LinkedList<Attributum> attributumList = AttributumCache.getKATEGORIA_ATTRIBUTUM().get(kat.getUrlNev());
					HashMap<String, Object> attributumok = new HashMap<String, Object>();
					for(String key : hirdetes.getAttributumok().keySet()) {
						for(Attributum attr : attributumList) {
							if(attr.getNev().equalsIgnoreCase(key)) {
								Object o = hirdetes.getAttributumok().get(key);
								// Ha van mertekegyseg, az ertek moge irjuk 
								if(attr.getMertekEgyseg() != null) {
									o = new String(o.toString() + " " + attr.getMertekEgyseg());
								}
								// Ha az ertek boolean, leforditjuk
								if(o instanceof Boolean) {
									o = ((Boolean) o) ? new String("igen") : new String("nem");
								}
								// Ha van ertekMap, kiszedjuk az ertekhez tartozo nevet
								if(attr.getErtekMap() != null) {
									o = attr.getErtekMap().get(o.toString());
								}
								attributumok.put(attr.getCim(), o);
								break;
							}
						}
					}
					hirdetes.setAttributumok(attributumok);
					
					// Kepek
					Query<HirdetesKep> kepekQuery = datastore.createQuery(HirdetesKep.class);
					kepekQuery.criteria("hirdetesId").equal(hirdetes.getId());
					
					for(HirdetesKep kep : kepekQuery) {
						hirdetes.getKepek().add(kep);
					}
					
					dataModel.put("hirdetes", hirdetes);
				} else {
					// Letezik a hirdetes, de nincs hitelesitve a felado altal
					dataModel.put("hibaUzenet", "A megadott hirdetés ugyan létezik, de a feladó még nem hitelesítette az emailben kiküldött linkre kattintva.");
				}
			} else {
				// Letezik a hirdetes, de mar lejart es torolve lett
				dataModel.put("hibaUzenet", "A megadott hirdetés már lejárt, és feladója nem hosszabbította meg.");
			}
		} else {
			// Nincs ilyen hirdetes
			getLogger().severe("Nincs meg a megadott hirdetes: " + (String) this.getRequestAttributes().get("hirdetesId"));
			
			dataModel.put("hibaUzenet", "A megadott hirdetés nem található. Valószinűleg oldalunk segítségével már vevőre lelt a meghirdetett termék vagy szolgáltatás.");
		}
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("hirdetes.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
