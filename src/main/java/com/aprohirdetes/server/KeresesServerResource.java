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
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class KeresesServerResource extends ServerResource implements
		KeresesResource {

	private int tipus = 1;
	private String kategoriaUrlNevList = null;
	private List<Kategoria> kategoriaList = new LinkedList<Kategoria>();
	private String helysegUrlNevList = null;
	private List<Helyseg> helysegList = new LinkedList<Helyseg>();
	
	private String query;
	private int page;
	private int pageSize = Integer.parseInt(AproApplication.APP_CONFIG.getProperty("SEARCH_DEFAULT_PAGESIZE", "10"));
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		this.kategoriaUrlNevList = (String) this.getRequestAttributes().get("kategoriaList");
		this.kategoriaList = KategoriaCache.getKategoriaListByUrlNevList(this.kategoriaUrlNevList);
		
		this.helysegUrlNevList = (String) this.getRequestAttributes().get("helysegList");
		if(this.helysegUrlNevList == null || this.helysegUrlNevList.isEmpty()) {
			this.helysegUrlNevList = "magyarorszag";
		}
		this.helysegList = HelysegCache.getHelysegListByUrlNevList(this.helysegUrlNevList);
		
		this.tipus = ("keres".equals((String) this.getRequestAttributes().get("hirdetesTipus"))) ? HirdetesTipus.KERES : HirdetesTipus.KINAL;
		
		this.query = getQueryValue("q")==null ? "" : getQueryValue("q");
		
		// Set current page
		try {
			this.page = Math.max(0, Integer.parseInt(
					(getQueryValue("p") == null || getQueryValue("p").isEmpty()) ? "1"  : getQueryValue("p")
				));
		} catch(NumberFormatException nfe) {
			this.page = 1;
		}
		
	}

	public Representation representHtml() throws IOException {
		
		ArrayList<ObjectId> kategoriaIdList = new ArrayList<ObjectId>();
		for(Kategoria kat : kategoriaList) {
			System.out.println(kat.toString());
			kategoriaIdList.add(kat.getId());
		}
		
		ArrayList<ObjectId> helysegIdList = new ArrayList<ObjectId>();
		for(Helyseg kat : helysegList) {
			System.out.println(kat.toString());
			helysegIdList.add(kat.getId());
		}
		
		Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		
		query.and(
				query.criteria("tipus").equal(this.tipus),
				query.criteria("helysegId").in(helysegIdList),
				query.criteria("kategoriaId").in(kategoriaIdList)
				);
		query.offset(this.page * this.pageSize - this.pageSize);
		query.limit(Integer.parseInt(AproApplication.APP_CONFIG.getProperty("SEARCH_DEFAULT_PAGESIZE", "10")));
		query.order("-id");
		
		List<Hirdetes> hirdetesList = new ArrayList<Hirdetes>();
		for(Hirdetes h : query) {
			System.out.println(h.getKategoriaId());
			
			h.getEgyebMezok().put("tipusNev", (h.getTipus()==HirdetesTipus.KINAL) ? "Kínál" : "Keres");
			
			Kategoria kat = KategoriaCache.getCacheById().get(h.getKategoriaId());
			h.getEgyebMezok().put("kategoriaNev", (kat!=null) ? kat.getNev() : "");
			h.getEgyebMezok().put("kategoriaUrlNev", (kat!=null) ? kat.getUrlNev() : "");
			
			Helyseg hely = HelysegCache.getCacheById().get(h.getHelysegId());
			h.getEgyebMezok().put("helysegNev", (hely!=null) ? hely.getNev() : "");
			h.getEgyebMezok().put("helysegUrlNev", (hely!=null) ? hely.getUrlNev() : "");
			
			h.getEgyebMezok().put("feladvaSzoveg", AproUtils.getHirdetesFeladvaSzoveg(h.getFeladasDatuma()));
			
			hirdetesList.add(h);
		}
		
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
		appDataModel.put("contextRoot", "/aprocom-server");
		appDataModel.put("htmlTitle", getApplication().getName());
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		
		dataModel.put("app", appDataModel);
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		dataModel.put("hirdetesList", hirdetesList);
		dataModel.put("hirdetesek_szama", query.countAll());
		dataModel.put("q", this.query);
		dataModel.put("page", this.page);
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("kereses.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
