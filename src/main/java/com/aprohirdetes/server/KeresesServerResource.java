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
import org.mongodb.morphia.query.Query;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticHtmlResource;
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
		StaticHtmlResource {

	private String contextPath = "";
	private String hirdetesTipusString = "kinal";
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
	private Integer arMin;
	private Integer arMax;
	private int page;
	private int pageSize = Integer.parseInt(AproConfig.APP_CONFIG.getProperty("SEARCH.DEFAULT_PAGESIZE", "20"));
	private int sorrend;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		this.selectedKategoriaUrlNevListString = (String) this.getRequestAttributes().get("kategoriaList");
		this.selectedKategoriaList = KategoriaCache.getKategoriaListByUrlNevList(this.selectedKategoriaUrlNevListString);
		
		this.selectedHelysegUrlNevListString = (String) this.getRequestAttributes().get("helysegList");
		if(this.selectedHelysegUrlNevListString == null || this.selectedHelysegUrlNevListString.isEmpty()) {
			// Alapertelmezett helyseg: Osszes helyseg
			this.selectedHelysegUrlNevListString = "osszes-helyseg";
		}
		this.selectedHelysegList = HelysegCache.getHelysegListByUrlNevList(this.selectedHelysegUrlNevListString);
		
		this.hirdetesTipusString = (String) this.getRequestAttributes().get("hirdetesTipus");
		this.hirdetesTipus = ("keres".equals(this.hirdetesTipusString)) ? HirdetesTipus.KERES : HirdetesTipus.KINAL;
		
		this.kulcsszo = getQueryValue("q")==null ? "" : getQueryValue("q");
		
		try {
			this.arMin = getQueryValue("arMin")==null ? null : Integer.parseInt(getQueryValue("arMin"));
		} catch(NumberFormatException nfe) {
			getLogger().warning("Ervenytelen minimum ar: " + getQueryValue("arMin"));
			this.arMin = null;
		}
		
		try {
			this.arMax = getQueryValue("arMax")==null ? null : Integer.parseInt(getQueryValue("arMax"));
		} catch(NumberFormatException nfe) {
			getLogger().warning("Ervenytelen maximum ar: " + getQueryValue("arMax"));
			this.arMax = null;
		}
		
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
		String hibaUzenet = null;
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
		// !!!!!! BARMI MODOSUL ITT, AZ RssServerResource-BAN IS MODOSITANI KELL !!!!!
		long hirdetesekSzama = 0;
		List<Hirdetes> hirdetesList = new ArrayList<Hirdetes>();
		
		try {
			// Kereses Morphiaval
			Datastore datastore = MongoUtils.getDatastore();
			Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
			
			query.criteria("hitelesitve").equal(true);
			query.criteria("torolve").equal(false);
			query.criteria("tipus").equal(this.hirdetesTipus);
			if(selectedHelysegIdList.size()>0) {
				query.criteria("helysegId").in(selectedHelysegIdList);
			}
			if(selectedKategoriaIdList.size()>0) {
				query.criteria("kategoriaId").in(selectedKategoriaIdList);
			}
			if(!this.kulcsszo.isEmpty()) {
				query.criteria("kulcsszavak").equal(kulcsszo);
			}
			if(this.arMin != null) {
				query.criteria("ar").greaterThanOrEq(arMin);
			}
			if(this.arMax != null) {
				query.criteria("ar").lessThanOrEq(arMax);
			}
			
			query.offset(this.page * this.pageSize - this.pageSize);
			query.limit(Integer.parseInt(AproConfig.APP_CONFIG.getProperty("SEARCH.DEFAULT_PAGESIZE", "20")));
			
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
				
				// Tagek
				h.getEgyebMezok().put("tag", "");
				
				if(h.getAr()==0) {
					String tag = h.getEgyebMezok().get("tag");
					if(!tag.isEmpty()) {
						tag += ";";
					}
					tag += "Ingyenes";
					h.getEgyebMezok().put("tag", tag);
				}
				// Friss tag: 3 napig
				if(h.getId().getTime()+3*24*3600*1000 > new Date().getTime()) {
					String tag = h.getEgyebMezok().get("tag");
					if(!tag.isEmpty()) {
						tag += ";";
					}
					tag += "Friss";
					h.getEgyebMezok().put("tag", tag);
				}
	
				
				// Kepek
				Query<HirdetesKep> kepekQuery = datastore.createQuery(HirdetesKep.class);
				kepekQuery.criteria("hirdetesId").equal(h.getId());
				
				for(HirdetesKep kep : kepekQuery) {
					h.getKepek().add(kep);
				}
				
				hirdetesList.add(h);
			}
			
			hirdetesekSzama = query.countAll();
		} catch(Exception e) {
			// Ha eddig nincs konkret hibauzenet, akkor kiirok egy altalanosat
			if(hibaUzenet==null) {
				hibaUzenet = "Sajnos hiba történt a keresés közben. Kérlek látogass vissza később, addigra javítani fogjuk!";
			}
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - " + KategoriaCache.getKategoriaNevekByUrlNevList(this.selectedKategoriaUrlNevListString) + " hirdetések " + HelysegCache.getHelysegNevekByUrlNevList(selectedHelysegUrlNevListString));
		appDataModel.put("description", "Sok-sok apróhirdetés " + KategoriaCache.getKategoriaNevekByUrlNevList(this.selectedKategoriaUrlNevListString) + " kategóriában. Helység, város: " + HelysegCache.getHelysegNevekByUrlNevList(selectedHelysegUrlNevListString));
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		String rssUrl = this.contextPath + "/rss/" + this.hirdetesTipusString + "/";
		rssUrl += (selectedHelysegUrlNevListString == null || selectedHelysegUrlNevListString.isEmpty()) ? "" : selectedHelysegUrlNevListString + "/";
		rssUrl += (selectedKategoriaUrlNevListString == null || selectedHelysegUrlNevListString.isEmpty()) ? "" : selectedKategoriaUrlNevListString + "/";
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", AproUtils.getSession(this));
		dataModel.put("kategoriaList", KategoriaCache.getKategoriaListByParentId(null));
		dataModel.put("helysegList", HelysegCache.getHelysegListByParentId(null));
		dataModel.put("hirdetesList", hirdetesList);
		dataModel.put("hirdetesTipus", this.hirdetesTipus);
		dataModel.put("hirdetesKategoria", selectedKategoriaUrlNevList);
		dataModel.put("hirdetesHelyseg", selectedHelysegUrlNevList);
		dataModel.put("hirdetesKategoriaUrl", selectedKategoriaUrlNevListString);
		dataModel.put("hirdetesHelysegUrl", selectedHelysegUrlNevListString);
		dataModel.put("rssUrl", rssUrl);
		dataModel.put("hirdetesek_szama", hirdetesekSzama);
		dataModel.put("q", this.kulcsszo);
		dataModel.put("arMin", this.arMin==null ? null : this.arMin.toString());
		dataModel.put("arMax", this.arMax==null ? null : this.arMax.toString());
		dataModel.put("aktualisOldal", this.page);
		dataModel.put("osszesOldal", (hirdetesekSzama/this.pageSize)+1);
		dataModel.put("sorrend", this.sorrend);
		dataModel.put("hibaUzenet", hibaUzenet);
		
		// TODO: Osszes kategoriat megoldani
		if(selectedKategoriaUrlNevList.contains("ingatlan") || selectedKategoriaUrlNevList.contains("lakas") || selectedKategoriaUrlNevList.contains("haz") || selectedKategoriaUrlNevList.contains("alberlet")) {
			dataModel.put("ingatlanTabClass", "ingatlan-tab");
			dataModel.put("headerBgClass", "ingatlan-bg");
		} else if(selectedKategoriaUrlNevList.contains("jarmu") || selectedKategoriaUrlNevList.contains("szemelyauto") || selectedKategoriaUrlNevList.contains("kishaszon") || selectedKategoriaUrlNevList.contains("haszonjarmu")) {
			dataModel.put("jarmuTabClass", "jarmu-tab");
			dataModel.put("headerBgClass", "jarmu-bg");
		} else if(selectedKategoriaUrlNevList.contains("allas")) {
			dataModel.put("allasTabClass", "allas-tab");
			dataModel.put("headerBgClass", "allas-bg");
		} else {
			dataModel.put("aproTabClass", "apro-tab");
			dataModel.put("headerBgClass", "apro-bg");
		}
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("kereses.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
