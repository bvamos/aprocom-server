package com.aprohirdetes.model;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.aprohirdetes.utils.MongoUtils;

public class KonfiguracioHelper {

	/**
	 * Betolt egy Konfiguracio objektumot a megadott kulcs alapjan
	 * 
	 * @param kulcs A Konfiguracio kulcsa, egyedi azonositoja
	 * @return A Konfiguracio objektum, vagy null, ha a kulcs alapjan nem talalta meg
	 */
	public static Konfiguracio load(String kulcs) {
		Konfiguracio konfiguracio = null;
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Konfiguracio> query = datastore.createQuery(Konfiguracio.class);
		
		query.criteria("kulcs").equal(kulcs);
		
		konfiguracio = query.get();
		
		return konfiguracio;
	}
}
