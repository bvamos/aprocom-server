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

import com.aprohirdetes.common.RssResource;
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

public class RssServerResource extends ServerResource implements
		RssResource {

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
	private int sorrend;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		this.selectedKategoriaUrlNevListString = (String) this.getRequestAttributes().get("kategoriaList");
		this.selectedKategoriaList = KategoriaCache.getKategoriaListByUrlNevList(this.selectedKategoriaUrlNevListString);
		
		this.selectedHelysegUrlNevListString = (String) this.getRequestAttributes().get("helysegList");
		if(this.selectedHelysegUrlNevListString == null || this.selectedHelysegUrlNevListString.isEmpty()) {
			this.selectedHelysegUrlNevListString = "osszes-helyseg";
		}
		this.selectedHelysegList = HelysegCache.getHelysegListByUrlNevList(this.selectedHelysegUrlNevListString);
		
		this.hirdetesTipusString = (String) this.getRequestAttributes().get("hirdetesTipus");
		this.hirdetesTipus = ("keres".equals(this.hirdetesTipusString)) ? HirdetesTipus.KERES : HirdetesTipus.KINAL;
		
		this.kulcsszo = getQueryValue("q")==null ? "" : getQueryValue("q");
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
	}

	public Representation representXml() throws IOException {
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
		Datastore datastore = MongoUtils.getDatastore();
		List<Hirdetes> hirdetesList = new ArrayList<Hirdetes>();
		//long hirdetesekSzama = 0;
		
		// Kereses Morphiaval
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
		
		query.limit(50);
		
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
		
		//hirdetesekSzama = query.countAll();
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - " + KategoriaCache.getKategoriaNevekByUrlNevList(this.selectedKategoriaUrlNevListString) + " hirdetések " + HelysegCache.getHelysegNevekByUrlNevList(selectedHelysegUrlNevListString));
		appDataModel.put("description", "50 legfrissebb apróhirdetés " + KategoriaCache.getKategoriaNevekByUrlNevList(this.selectedKategoriaUrlNevListString) + " kategóriában. Helység, város: " + HelysegCache.getHelysegNevekByUrlNevList(selectedHelysegUrlNevListString));
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesList", hirdetesList);
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("rss.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.APPLICATION_RSS);
	}

}
