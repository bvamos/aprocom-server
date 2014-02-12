package com.aprohirdetes.server;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.RootResource;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaHelper;
import com.aprohirdetes.utils.MongoUtils;
import com.mongodb.DBObject;

public class RootServerResource extends ServerResource implements RootResource {

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
		
		System.out.println(dbObjectList);
		JSONArray json = new JSONArray(dbObjectList);
		System.out.println(json);
		
		rep = new JsonRepresentation(json);
		
		return rep;
	}

	public Representation representHtml() {
		ArrayList<DBObject> kategoriaList = KategoriaHelper.getKategoriaListByParentId(null);
		for(DBObject o : kategoriaList) {
			ArrayList<DBObject> alkategoriak = KategoriaHelper.getKategoriaListByParentId(o.get("_id").toString());
			o.put("alkategoriak", alkategoriak);
		}
		
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("content", getApplication().getName());
		dataModel.put("kategoriaMap", kategoriaList);
		
		Representation mailFtl = new ClientResource(LocalReference.createClapReference(getClass().getPackage())	+ "/templates/index.ftl.html").get();
		
		return new TemplateRepresentation(mailFtl, dataModel, MediaType.TEXT_HTML);
	}

}
