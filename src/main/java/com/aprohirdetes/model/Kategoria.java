package com.aprohirdetes.model;

import java.util.HashMap;

import com.aprohirdetes.utils.MongoUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class Kategoria extends BasicDBObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3053186992065105902L;
	private static HashMap<String, Kategoria> CACHE_BY_ID = new HashMap<String, Kategoria>();
	private static HashMap<String, Kategoria> CACHE_BY_URLNEV = new HashMap<String, Kategoria>();
	
	/**
	 * 
	 */
	public static void loadCache() {
		CACHE_BY_ID.clear();
		CACHE_BY_URLNEV.clear();
		
		DB db = MongoUtils.getMongoDB();
		if(db != null) {
			DBCollection collKategoria = db.getCollection("kategoria");
			collKategoria.setObjectClass(Kategoria.class);
			
			DBCursor cursor = collKategoria.find();
			try {
			   while(cursor.hasNext()) {
				   Kategoria obj = (Kategoria) cursor.next();
			       CACHE_BY_ID.put(obj.getString("_id"), obj);
			       CACHE_BY_URLNEV.put(obj.getString("urlNev"), obj);
			   }
			} finally {
			   cursor.close();
			}
		}
		
		System.out.println(CACHE_BY_ID);
	}
	
}
