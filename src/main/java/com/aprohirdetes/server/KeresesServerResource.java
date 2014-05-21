package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

import com.aprohirdetes.common.KeresesResource;
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

public class KeresesServerResource extends ServerResource implements
		KeresesResource {

	private String contextPath = "";
	/**
	 * Hirdetes tipusa. 1=Keres, 2=Kinal
	 */
	private int hirdetesTipus = HirdetesTipus.KINAL;
	/**
	 * A kivalasztott kategoriak URL neveit tartalmazo, + jellel elvalasztott lista. Az URL-bol jon.
	 */
	private String selectedKategoriaUrlNevListString = null;
	/**
	 * A kivalasztott Kategoria objektumokat tartalmazo lista
	 */
	private List<Kategoria> selectedKategoriaList = new LinkedList<Kategoria>();
	/**
	 * Kivalasztott kategoriak URL neveit tartalmazo, + jellel elvalasztott lista. Az URL-bol jon.
	 */
	private String selectedHelysegUrlNevListString = null;
	/**
	 * A kivalasztott Helyseg objektumokat tartalmazo lista
	 */
	private List<Helyseg> selectedHelysegList = new LinkedList<Helyseg>();
	
	private String kulcsszo;
	private int page;
	private int pageSize = Integer.parseInt(AproApplication.APP_CONFIG.getProperty("SEARCH.DEFAULT_PAGESIZE", "20"));
	private int sorrend;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		this.selectedKategoriaUrlNevListString = (String) this.getRequestAttributes().get("kategoriaList");
		this.selectedKategoriaList = KategoriaCache.getKategoriaListByUrlNevList(this.selectedKategoriaUrlNevListString);
		
		this.selectedHelysegUrlNevListString = (String) this.getRequestAttributes().get("helysegList");
		if(this.selectedHelysegUrlNevListString == null || this.selectedHelysegUrlNevListString.isEmpty()) {
			this.selectedHelysegUrlNevListString = "magyarorszag";
		}
		this.selectedHelysegList = HelysegCache.getHelysegListByUrlNevList(this.selectedHelysegUrlNevListString);
		
		this.hirdetesTipus = ("keres".equals((String) this.getRequestAttributes().get("hirdetesTipus"))) ? HirdetesTipus.KERES : HirdetesTipus.KINAL;
		
		this.kulcsszo = getQueryValue("q")==null ? "" : getQueryValue("q");
		
		// Set current page
		try {
			this.page = Math.max(0, Integer.parseInt(
					(getQueryValue("p") == null || getQueryValue("p").isEmpty()) ? "1"  : getQueryValue("p")
				));
		} catch(NumberFormatException nfe) {
			this.page = 1;
		}
		
		try {
			this.sorrend = Integer.parseInt(
				(getQueryValue("s") == null || getQueryValue("s").isEmpty()) ? "0"  : getQueryValue("s")
			);
		} catch(NumberFormatException nfe) {
			this.sorrend = 0;
		}
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
	}

	public Representation representHtml() throws IOException {
		/**
		 * Kivalasztott kategoriak Id-jait tartalmazo lista. A kereseshez kell.
		 */
		ArrayList<ObjectId> selectedKategoriaIdList = new ArrayList<ObjectId>();
		/**
		 * Kivalasztott kategoriak UrlNeveit tartalmazo lista. A legordulo menuhoz kell, hogy be tudjuk allitani a kivalasztott elemeket.
		 */
		ArrayList<String> selectedKategoriaUrlNevList = new ArrayList<String>();
		for(Kategoria kat : selectedKategoriaList) {
			selectedKategoriaIdList.add(kat.getId());
			selectedKategoriaUrlNevList.add(kat.getUrlNev());
		}
		
		/**
		 * Kivalasztott helysegek Id-jait tartalmazo lista. A kereseshez kell.
		 */
		ArrayList<ObjectId> selectedHelysegIdList = new ArrayList<ObjectId>();
		/**
		 * Kivalasztott helysegek UrlNeveit tartalmazo lista. A legordulo menuhoz kell, hogy be tudjuk allitani a kivalasztott elemeket.
		 */
		ArrayList<String> selectedHelysegUrlNevList = new ArrayList<String>();
		for(Helyseg helyseg : selectedHelysegList) {
			selectedHelysegIdList.add(helyseg.getId());
			selectedHelysegUrlNevList.add(helyseg.getUrlNev());
		}

		// Kereses
		Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
		List<Hirdetes> hirdetesList = new ArrayList<Hirdetes>();
		long hirdetesekSzama = 0;
		
		// Kereses Morphiaval
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		
		query.criteria("tipus").equal(this.hirdetesTipus);
		query.criteria("helysegId").in(selectedHelysegIdList);
		query.criteria("kategoriaId").in(selectedKategoriaIdList);
		if(!this.kulcsszo.isEmpty()) {
			query.criteria("kulcsszavak").equal(kulcsszo);
		}
		
		query.offset(this.page * this.pageSize - this.pageSize);
		query.limit(Integer.parseInt(AproApplication.APP_CONFIG.getProperty("SEARCH.DEFAULT_PAGESIZE", "20")));
		
		// Rendezes
		if(this.sorrend==2) {
			// Feladas ideje szerint novekvo: Legregebbi elol
			query.order("id");
		} else if(this.sorrend==3) {
			// Ar szerint novekvo: Legolcsobb elol
			query.order("ar");
		} else if(this.sorrend==4) {
			// Ar szerint csokkeno: Legdragabb elol
			query.order("-ar");
		} else {
			// Feladas ideje szerint csokkeno: Legujabb elol
			query.order("-id");
		}
		
		//System.out.println(query);
		
		// Kereses eredmenyeben levo Hirdetes objektumok feltoltese kepekkel, egyeb adatokkal a megjeleniteshez
		for(Hirdetes h : query) {
			h.getEgyebMezok().put("tipusNev", (h.getTipus()==HirdetesTipus.KINAL) ? "Kínál" : "Keres");
			
			Kategoria kat = KategoriaCache.getCacheById().get(h.getKategoriaId());
			h.getEgyebMezok().put("kategoriaNev", (kat!=null) ? KategoriaCache.getKategoriaNevChain(kat.getId()) : "");
			h.getEgyebMezok().put("kategoriaUrlNev", (kat!=null) ? kat.getUrlNev() : "");
			
			Helyseg hely = HelysegCache.getCacheById().get(h.getHelysegId());
			h.getEgyebMezok().put("helysegNev", (hely!=null) ? HelysegCache.getHelysegNevChain(hely.getId()) : "");
			h.getEgyebMezok().put("helysegUrlNev", (hely!=null) ? hely.getUrlNev() : "");
			
			h.getEgyebMezok().put("feladvaSzoveg", AproUtils.getHirdetesFeladvaSzoveg(h.getFeladasDatuma()));
			
			// Kepek
			Query<HirdetesKep> kepekQuery = datastore.createQuery(HirdetesKep.class);
			kepekQuery.criteria("hirdetesId").equal(h.getId());
			
			for(HirdetesKep kep : kepekQuery) {
				h.getKepek().add(kep);
			}
			
			hirdetesList.add(h);
		}
		
		hirdetesekSzama = query.countAll();
		
		// Legordulokhoz adatok feltoltese
		ArrayList<Kategoria> kategoriaList = KategoriaCache.getKategoriaListByParentId(null);
		for(Kategoria o : kategoriaList) {
			ArrayList<Kategoria> alkategoriak = KategoriaCache.getKategoriaListByParentId(o.getIdAsString());
			o.setAlkategoriaList(alkategoriak);
		}
		
		ArrayList<Helyseg> helysegList = HelysegCache.getHelysegListByParentId(null);
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " " + KategoriaCache.getKategoriaNevekByUrlNevList(this.selectedKategoriaUrlNevListString));
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", AproUtils.getSession(this));
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		dataModel.put("hirdetesList", hirdetesList);
		dataModel.put("hirdetesTipus", this.hirdetesTipus);
		dataModel.put("hirdetesKategoria", selectedKategoriaUrlNevList);
		dataModel.put("hirdetesHelyseg", selectedHelysegUrlNevList);
		dataModel.put("hirdetesek_szama", hirdetesekSzama);
		dataModel.put("q", this.kulcsszo);
		dataModel.put("aktualisOldal", this.page);
		dataModel.put("osszesOldal", (hirdetesekSzama/this.pageSize)+1);
		dataModel.put("sorrend", this.sorrend);
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("kereses.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
