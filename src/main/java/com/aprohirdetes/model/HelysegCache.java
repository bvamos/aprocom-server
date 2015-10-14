package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
	
	/**
	 * Feltolti a Helyseg cache-eket az adatbazisbol, hogy kesobb csak ezt hasznaljuk, 
	 * es ne kelljen nyulni a db-hez.
	 * A legfelso szint minden Helyseget tartalmaz, es azok is tartalmazzak az alattuk
	 * levoket. Igy egy akar a 3. szinten levo Helyseget is ki lehet venni a cache-bol
	 * az Helyseg.id vagy a Helyseg.urlNev alapjan.
	 */
	public static void loadCache() {
		CACHE_BY_ID.clear();
		CACHE_BY_URLNEV.clear();
		
		for(Helyseg helyseg : loadById(null)) {
			CACHE_BY_ID.put(helyseg.getId(), helyseg);
			CACHE_BY_URLNEV.put(helyseg.getUrlNev(), helyseg);
		}
		
		//System.out.println(CACHE_BY_ID.toString());
		//System.out.println(CACHE_BY_URLNEV.toString());
	}
	
	/**
	 * Rekurzivan betolti a Helysegek listajat
	 * @param szuloId
	 * @return
	 */
	private static LinkedList<Helyseg> loadById(ObjectId szuloId) {
		LinkedList<Helyseg> ret = new LinkedList<Helyseg>();
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Helyseg> query = datastore.createQuery(Helyseg.class);
		if (szuloId!=null) {
			query.filter("szuloId", szuloId);
		}
		query.order("sorrend");
		
		for(Helyseg helyseg : query.asList()) {
			helyseg.setAlhelysegList(loadById(helyseg.getId()));
			ret.add(helyseg);
		}
		
		return ret;
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
				ret = o.getNev() + " &raquo; ";
				break;
			}
		}
		
		// Megadott Helyseg neve
		ret +=  helyseg.getNev();
		
		return ret;
	}
	
	/**
	 * Visszaadja a megadott Helyseg URL-ben hasznalt nevet
	 * @param helysegId
	 * @return
	 */
	public static String getHelysegUrlNev(ObjectId helysegId) {
		String ret = "";
		
		if(helysegId == null) {
			return ret;
		}
		
		Helyseg helyseg = CACHE_BY_ID.get(helysegId);
		
		if(helyseg == null) {
			return ret;
		}
		
		// Megadott Helyseg url neve
		return helyseg.getUrlNev();
	}
	
	public static Helyseg getRandomHelyseg() {
		Random generator = new Random();
		Object[] values = CACHE_BY_ID.values().toArray();
		return (Helyseg) values[generator.nextInt(values.length)];
	}
}
