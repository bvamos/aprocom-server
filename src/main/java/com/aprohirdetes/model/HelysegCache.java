package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.aprohirdetes.server.AproApplication;
import com.aprohirdetes.utils.MongoUtils;

public class HelysegCache {

	private static Map<String, Helyseg> CACHE_BY_ID = new LinkedHashMap<String, Helyseg>();
	private static Map<String, Helyseg> CACHE_BY_URLNEV = new LinkedHashMap<String, Helyseg>();
	
	public static Map<String, Helyseg> getCacheById() {
		return CACHE_BY_ID;
	}
	
	public static Map<String, Helyseg> getCacheByUrlNev() {
		return CACHE_BY_URLNEV;
	}
	
	public static void loadCache() {
		Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
		Query<Helyseg> query = datastore.createQuery(Helyseg.class).order("sorrend");
		
		for(Helyseg level1 : query.asList()) {
			System.out.println(level1.getNev());
			
			Query<Helyseg> query1 = datastore.createQuery(Helyseg.class).filter("szuloId", level1.getId()).order("sorrend");
			level1.setAlhelysegList(query1.asList());
			
			for(Helyseg level2 : query1.asList()) {
				System.out.print(level2.getNev() + " ");
				
				Query<Helyseg> query2 = datastore.createQuery(Helyseg.class).filter("szuloId", level2.getId()).order("sorrend");
				level2.setAlhelysegList(query2.asList());
			}
			System.out.println();
			CACHE_BY_ID.put(level1.getIdAsString(), level1);
			CACHE_BY_URLNEV.put(level1.getUrlNev(), level1);
			
		}
	}
	
	public static ArrayList<Helyseg> getHelysegListByParentId(String parentId) {
		ArrayList<Helyseg> ret = new ArrayList<Helyseg>();
		
		for(Helyseg obj : CACHE_BY_ID.values()) {
			if(parentId == null) {
				if(obj.getSzuloId() == null) {
					ret.add(obj);
				}
			} else {
				if(obj.getSzuloId() != null && parentId.equals(obj.getSzuloId().toString())) {
					ret.add(obj);
				}
			}
		}
		
		return ret;
	}
}
