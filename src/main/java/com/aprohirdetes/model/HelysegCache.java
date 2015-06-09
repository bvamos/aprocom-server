package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.aprohirdetes.utils.MongoUtils;

public class HelysegCache {

	private static Map<ObjectId, Helyseg> CACHE_BY_ID = new LinkedHashMap<ObjectId, Helyseg>();
	private static Map<String, Helyseg> CACHE_BY_URLNEV = new LinkedHashMap<String, Helyseg>();
	
	public static Map<ObjectId, Helyseg> getCacheById() {
		return CACHE_BY_ID;
	}
	
	public static Map<String, Helyseg> getCacheByUrlNev() {
		return CACHE_BY_URLNEV;
	}
	
	public static void loadCache() {
		Datastore datastore = MongoUtils.getDatastore();
		Query<Helyseg> query = datastore.createQuery(Helyseg.class).order("sorrend");
		
		for(Helyseg level1 : query.asList()) {
			Query<Helyseg> query1 = datastore.createQuery(Helyseg.class).filter("szuloId", level1.getId()).order("sorrend");
			level1.setAlhelysegList(query1.asList());
			
			for(Helyseg level2 : query1.asList()) {
				Query<Helyseg> query2 = datastore.createQuery(Helyseg.class).filter("szuloId", level2.getId()).order("sorrend");
				level2.setAlhelysegList(query2.asList());
			}
			CACHE_BY_ID.put(level1.getId(), level1);
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
	
	public static ArrayList<Helyseg> getHelysegListByUrlNevList(String urlNevList) {
		ArrayList<Helyseg> ret = new ArrayList<Helyseg>();
		
		if(urlNevList != null) {
			for(String urlNev : urlNevList.split("\\+")) {
				Helyseg h = CACHE_BY_URLNEV.get(urlNev); 
				if(h != null) {
					ret.add(h);
					if(h.getAlhelysegList() != null && !h.getAlhelysegList().isEmpty()) {
						for(Helyseg h1 : h.getAlhelysegList()) {
							ret.addAll(getHelysegListByUrlNevList(h1.getUrlNev()));
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Visszadja a Helysegek nevet, vesszovel elvalasztva a '+' jellel elvalasztott, URL-ben megadott
	 * lista alapjan. Arra hasznalom, hogy a HTML title tagben megjelenítsem az eppen keresett helysegek neveit.
	 * @param urlNevList
	 * @return
	 */
	public static String getHelysegNevekByUrlNevList(String urlNevList) {
		String ret = "";
		
		if(urlNevList != null) {
			for(String urlNev : urlNevList.split("\\+")) {
				if("osszes-helyseg".equalsIgnoreCase(urlNev)) {
					if(ret.length()>0) ret += ", ";
					ret += "Minden helység";
				}
				Helyseg k = CACHE_BY_URLNEV.get(urlNev);
				if(k != null) {
					if(ret.length()>0) ret += ", ";
					ret += k.getNev();
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Visszaadja a Kategoria nevek lancolatat egy stringkent a megadott KAtegoriatol felfele
	 * @param kategoriaId
	 * @return
	 */
	public static String getHelysegNevChain(ObjectId helysegId) {
		String ret = "";
		
		if(helysegId == null) {
			return ret;
		}
		
		Helyseg helyseg = CACHE_BY_ID.get(helysegId);
		
		if(helyseg == null) {
			return ret;
		}
		
		// Szulo neve
		for(Helyseg o : CACHE_BY_ID.values()) {
			if(o.getId().equals(helyseg.getSzuloId())) {
				ret = "<small>" + o.getNev() + " &raquo; </small>";
				break;
			}
		}
		
		// Megadott Helyseg neve
		ret +=  "<small>" + helyseg.getNev() + "</small>";
		
		return ret;
	}
	
	public static Helyseg getRandomHelyseg() {
		Random generator = new Random();
		Object[] values = CACHE_BY_ID.values().toArray();
		return (Helyseg) values[generator.nextInt(values.length)];
	}
}
