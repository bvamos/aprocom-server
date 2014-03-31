package com.aprohirdetes.server;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.RootResource;
import com.aprohirdetes.model.Helyseg;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MongoUtils;
import com.mongodb.DBObject;

import freemarker.template.Template;

public class RootServerResource extends ServerResource implements RootResource {

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		//System.out.println(getRequest().getRootRef().toString());
	}

	public String representText() throws UnknownHostException {
		Datastore ds = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
		
		//Query<Kategoria> query = ds.createQuery(Kategoria.class).filter("sorrend", 9);
		Query<Kategoria> query = ds.createQuery(Kategoria.class).filter("szuloId", null);
		//Query<Kategoria> query = ds.find(Kategoria.class).field("szuloId").doesNotExist();
		//Query<Kategoria> query = ds.find(Kategoria.class).field("sorrend").equal(9);
		
		return query.asList().toString();
	}
	
	public Representation representJson() throws UnknownHostException {
		Representation rep = null;
		
		Datastore ds = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
		
		Query<Kategoria> query = ds.createQuery(Kategoria.class).filter("szuloId != ", null).order("sorrend");
		
		Mapper mapper = new Morphia().getMapper();
		List<DBObject> dbObjectList = new ArrayList<DBObject>();
		for(Object obj : query.asList()) {
			DBObject dbObj = mapper.toDBObject(obj);
			dbObjectList.add(dbObj);
		}
		
		JSONArray json = new JSONArray(dbObjectList);
		
		rep = new JsonRepresentation(json);
		
		return rep;
	}

	public Representation representHtml() throws IOException {
		ArrayList<Kategoria> kategoriaList = KategoriaCache.getKategoriaListByParentId(null);
		for(Kategoria o : kategoriaList) {
			ArrayList<Kategoria> alkategoriak = KategoriaCache.getKategoriaListByParentId(o.getIdAsString());
			o.setAlkategoriaList(alkategoriak);
		}
		
		ArrayList<Helyseg> helysegList = HelysegCache.getHelysegListByParentId(null);
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", getRequest().getRootRef().toString());
		appDataModel.put("htmlTitle", getApplication().getName());
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		
		dataModel.put("app", appDataModel);
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("hirdetesKategoria", "ingatlan");
		dataModel.put("hirdetesHelyseg", "magyarorszag");
		dataModel.put("session", AproUtils.getSession(this));
		
		// Without global configuration object
		//Representation indexFtl = new ClientResource(LocalReference.createClapReference(getClass().getPackage())	+ "/templates/index.ftl.html").get();
		Template indexFtl = AproApplication.TPL_CONFIG.getTemplate("index.ftl.html");
		
		return new TemplateRepresentation(indexFtl, dataModel, MediaType.TEXT_HTML);
	}

}
