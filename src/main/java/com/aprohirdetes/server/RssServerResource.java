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

import com.aprohirdetes.common.StaticXmlResource;
import com.aprohirdetes.model.Helyseg;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class RssServerResource extends ServerResource implements
		StaticXmlResource {

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
		for(Kategoria kat : selectedKategoriaList) {
			selectedKategoriaIdList.add(kat.getId());
		}
		
		/**
		 * Kivalasztott helysegek Id-jait tartalmazo lista. A kereseshez kell.
		 */
		ArrayList<ObjectId> selectedHelysegIdList = new ArrayList<ObjectId>();
		for(Helyseg helyseg : selectedHelysegList) {
			selectedHelysegIdList.add(helyseg.getId());
		}

		// Kereses
		Datastore datastore = MongoUtils.getDatastore();
		List<Hirdetes> hirdetesList = new ArrayList<Hirdetes>();
		
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
		query.order("-id");
		
		// Kereses eredmenyeben levo Hirdetes objektumok feltoltese kepekkel, egyeb adatokkal a megjeleniteshez
		for(Hirdetes h : query) {
			Kategoria kat = KategoriaCache.getCacheById().get(h.getKategoriaId());
			h.getEgyebMezok().put("kategoriaNev", (kat!=null) ? KategoriaCache.getKategoriaNevChain(kat.getId()) : "");
			h.getEgyebMezok().put("kategoriaUrlNev", (kat!=null) ? kat.getUrlNev() : "");
			
			h.getEgyebMezok().put("feladvaDatum", h.getFeladvaAsDate().toString());
			
			// Kepek
			/*Query<HirdetesKep> kepekQuery = datastore.createQuery(HirdetesKep.class);
			kepekQuery.criteria("hirdetesId").equal(h.getId());
			
			for(HirdetesKep kep : kepekQuery) {
				h.getKepek().add(kep);
			}*/
			
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
