package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.aprohirdetes.server.AproApplication;
import com.aprohirdetes.utils.MongoUtils;

public class KategoriaCache {

	private static Map<ObjectId, Kategoria> CACHE_BY_ID = new LinkedHashMap<ObjectId, Kategoria>();
	private static Map<String, Kategoria> CACHE_BY_URLNEV = new LinkedHashMap<String, Kategoria>();
	
	public static Map<ObjectId, Kategoria> getCacheById() {
		return CACHE_BY_ID;
	}
	
	public static Map<String, Kategoria> getCacheByUrlNev() {
		return CACHE_BY_URLNEV;
	}
	
	/**
	 * 
	 */
	public static void loadCache() {
		CACHE_BY_ID.clear();
		CACHE_BY_URLNEV.clear();
		
		Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
		Query<Kategoria> query = datastore.createQuery(Kategoria.class).order("sorrend");
		
		for(Kategoria level1 : query.asList()) {
			Query<Kategoria> query1 = datastore.createQuery(Kategoria.class).filter("szuloId", level1.getId()).order("sorrend");
			level1.setAlkategoriaList(query1.asList());
			
			for(Kategoria level2 : query1.asList()) {
				Query<Kategoria> query2 = datastore.createQuery(Kategoria.class).filter("szuloId", level2.getId()).order("sorrend");
				level2.setAlkategoriaList(query2.asList());
			}
			CACHE_BY_ID.put(level1.getId(), level1);
			CACHE_BY_URLNEV.put(level1.getUrlNev(), level1);
			
		}
	}
	
	public static ArrayList<Kategoria> getKategoriaListByParentId(String parentId) {
		ArrayList<Kategoria> ret = new ArrayList<Kategoria>();
		
		for(Kategoria kat : CACHE_BY_ID.values()) {
			if(parentId == null) {
				if(kat.getSzuloId() == null) {
					ret.add(kat);
				}
			} else {
				if(kat.getSzuloId() != null && parentId.equals(kat.getSzuloId().toString())) {
					ret.add(kat);
				}
			}
		}
		
		return ret;
	}
	
	public static ArrayList<Kategoria> getKategoriaListByUrlNevList(String urlNevList) {
		ArrayList<Kategoria> ret = new ArrayList<Kategoria>();
		
		if(urlNevList != null) {
			for(String urlNev : urlNevList.split("\\+")) {
				Kategoria k = CACHE_BY_URLNEV.get(urlNev);
				if(k != null) {
					ret.add(k);
					
					if(!k.getAlkategoriaList().isEmpty()) {
						for(Kategoria k1 : k.getAlkategoriaList()) {
							ret.addAll(getKategoriaListByUrlNevList(k1.getUrlNev()));
						}
					}
				}
			}
		}
		
		return ret;
	}
}
