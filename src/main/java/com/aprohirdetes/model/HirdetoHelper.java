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
	
	/**
	 * Visszaad egy ervenyes hirdetoTipus erteket a FORM mezoje alapjan
	 * @param value
	 * @return
	 */
	public static int getHirdetoTipus(String value) {
		// Maganszemely:1, Ceg: 2
		int hirdetoTipus = 1;
		try{
			hirdetoTipus = Integer.parseInt(value);
			if(hirdetoTipus != 1 && hirdetoTipus != 2) {
				hirdetoTipus = 1;
			}
		} catch (NumberFormatException nfe) {
			hirdetoTipus = 1;
		}
		
		return hirdetoTipus;
	}
}
