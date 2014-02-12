package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.aprohirdetes.utils.MongoUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class KategoriaHelper extends BasicDBObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3053186992065105902L;
	private static HashMap<String, DBObject> CACHE_BY_ID = new HashMap<String, DBObject>();
	private static HashMap<String, DBObject> CACHE_BY_URLNEV = new HashMap<String, DBObject>();
	
	public static HashMap<String, DBObject> getCacheById() {
		return CACHE_BY_ID;
	}
	
	public static HashMap<String, DBObject> getCacheByUrlNev() {
		return CACHE_BY_URLNEV;
	}
	
	/**
	 * 
	 */
	public static void loadCache() {
		CACHE_BY_ID.clear();
		CACHE_BY_URLNEV.clear();
		
		DB db = MongoUtils.getMongoDB();
		if(db != null) {
			DBCollection collKategoria = db.getCollection("kategoria");
			
			DBCursor cursor = collKategoria.find();
			try {
			   while(cursor.hasNext()) {
				   BasicDBObject obj = (BasicDBObject) cursor.next();
			       CACHE_BY_ID.put(obj.getString("_id"), obj);
			       CACHE_BY_URLNEV.put(obj.getString("_id"), obj);
			   }
			} finally {
			   cursor.close();
			}
		}
		
		System.out.println(CACHE_BY_ID);
	}
	
	public static ArrayList<DBObject> getKategoriaListByParentId(String parentId) {
		ArrayList<DBObject> ret = new ArrayList<DBObject>();
		
		for(DBObject kat : CACHE_BY_ID.values()) {
			if(parentId == null) {
				if(kat.get("szuloId") == null) {
					ret.add(kat);
				}
			} else {
				if(kat.get("szuloId") != null && parentId.equals(kat.get("szuloId").toString())) {
					ret.add(kat);
				}
			}
		}
		
		return ret;
	}
}
