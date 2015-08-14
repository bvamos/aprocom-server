package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

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
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Kategoria> query = datastore.createQuery(Kategoria.class).order("sorrend");
		
		for(Kategoria level1 : query.asList()) {
			Query<Kategoria> query1 = datastore.createQuery(Kategoria.class).filter("szuloId", level1.getId()).order("sorrend");
			level1.setAlkategoriaList(query1.asList());
			
			for(Kategoria level2 : query1.asList()) {
				Query<Kategoria> query2 = datastore.createQuery(Kategoria.class).filter("szuloId", level2.getId()).order("sorrend");
				level2.setAlkategoriaList(query2.asList());
				
				for(Kategoria level3 : query2.asList()) {
					Query<Kategoria> query3 = datastore.createQuery(Kategoria.class).filter("szuloId", level3.getId()).order("sorrend");
					level3.setAlkategoriaList(query3.asList());
				}
			}
			CACHE_BY_ID.put(level1.getId(), level1);
			CACHE_BY_URLNEV.put(level1.getUrlNev(), level1);
		
			//System.out.println(level1.toString());
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
	
	/**
	 * Visszadja a Kategoriak listajat az URL-ben szereplo, + jellel elvalasztott lista alapjan.
	 * Ehhez a cache-t hasznalja es belerakja az alkategoriakat is.
	 * @param urlNevList '+' jellel elvalasztott lista, az URL-ben szerepel a kategoriak megadasara
	 * @return ArrayList<Kategoria> Kategoriak listaja
	 */
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
	
	/**
	 * Visszadja a Kategirak nevet, vesszovel elvalasztva a '+' jellel elvalasztott, URL-ben megadott
	 * lista alapjan. Arra hasznalom, hogy a HTML title tagben megjelenítsem az eppen keresett kategoriak neveit,
	 * @param urlNevList
	 * @return
	 */
	public static String getKategoriaNevekByUrlNevList(String urlNevList) {
		String ret = "";
		
		if(urlNevList != null) {
			for(String urlNev : urlNevList.split("\\+")) {
				Kategoria k = CACHE_BY_URLNEV.get(urlNev);
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
	public static String getKategoriaNevChain(ObjectId kategoriaId) {
		String ret = "";
		
		if(kategoriaId == null) {
			return ret;
		}
		
		Kategoria kategoria = CACHE_BY_ID.get(kategoriaId);
		
		if(kategoria == null) {
			return ret;
		}
		
		// Szulo kategoria neve
		for(Kategoria kat : CACHE_BY_ID.values()) {
			if(kat.getId().equals(kategoria.getSzuloId())) {
				ret = kat.getNev() + " &raquo; ";
				break;
			}
		}
		
		// Megadott kategoria neve
		ret +=  kategoria.getNev();
		
		return ret;
	}
	
	/**
	 * Visszaadja a Kategoria nevek lancolatat egy stringkent a megadott Kategoriatol felfele
	 * ugy, hogy linkekke alakitja a szovegeket. A kereses eredmenylistajaban hasznaljuk.
	 * @param kategoriaId
	 * @return
	 */
	public static String getKategoriaNevChainAsLink(ObjectId kategoriaId) {
		String ret = "";
		
		if(kategoriaId == null) {
			return ret;
		}
		
		Kategoria kategoria = CACHE_BY_ID.get(kategoriaId);
		
		if(kategoria == null) {
			return ret;
		}
		
		// Szulo kategoria neve
		for(Kategoria kat : CACHE_BY_ID.values()) {
			if(kat.getId().equals(kategoria.getSzuloId())) {
				ret = "<a href=\"javascript:setKategoria('" + kat.getUrlNev() + "');\">" + kat.getNev() + "</a> &raquo; ";
				break;
			}
		}
		
		// Megadott kategoria neve
		ret +=  "<a href=\"javascript:setKategoria('" + kategoria.getUrlNev() + "');\">" + kategoria.getNev() + "</a>";
		
		return ret;
	}
	
	/**
	 * Visszaad egy Kategoriat veletlenszeruen. Teszteleshez, adatfeltolteshez hasznalom.
	 * @return Kategoria 
	 */
	public static Kategoria getRandomKategoria() {
		Random generator = new Random();
		Object[] values = CACHE_BY_ID.values().toArray();
		return (Kategoria) values[generator.nextInt(values.length)];
	}
}
