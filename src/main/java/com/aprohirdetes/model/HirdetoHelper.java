package com.aprohirdetes.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.aprohirdetes.utils.MongoUtils;

public class HirdetoHelper {

	public static Hirdeto load(ObjectId hirdetoId) {
		Hirdeto hirdeto = null;
		
		Query<Hirdeto> query = MongoUtils.getDatastore().createQuery(Hirdeto.class);
		query.criteria("id").equal(hirdetoId);
		hirdeto = query.get();
		
		return hirdeto;
	}
	
	/**
	 * Megkeresi a Hirdetot az apiKey alapjan. Rest API-nal kell, mert apiKey segitsegevel azonositja megat a Hirdeto minden hivasban.
	 * @param apiKey Hirdeto egyedi azonosito kulcsa
	 * @return Az azonositott Hirdeto, vagy null
	 */
	public static Hirdeto load(String apiKey) {
		Hirdeto hirdeto = null;
		
		Query<Hirdeto> query = MongoUtils.getDatastore().createQuery(Hirdeto.class);
		query.criteria("apiKey").equal(apiKey);
		hirdeto = query.get();
		
		return hirdeto;
	}
}
