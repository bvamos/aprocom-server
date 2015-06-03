package com.aprohirdetes.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.aprohirdetes.utils.MongoUtils;

public class UzenetHelper {

	public UzenetHelper() {
	}

	/**
	 * 
	 * @param uzenet
	 * @return
	 */
	public static ObjectId add(Uzenet uzenet) {
		Datastore datastore = MongoUtils.getDatastore();
		Key<Uzenet> key = datastore.save(uzenet);
		
		return (ObjectId) key.getId();
	}
	
	/**
	 * Beallitja az Uzenetet olvasottra (elolvasva=true)
	 * @param uzenetId Uzenet azonositoja
	 */
	public static void setElolvasva(ObjectId uzenetId) {
		Datastore datastore = MongoUtils.getDatastore();
		Query<Uzenet> query = datastore.createQuery(Uzenet.class);
		query.criteria("id").equal(uzenetId);
		UpdateOperations<Uzenet> ops = datastore.createUpdateOperations(Uzenet.class).set("elolvasva", true);
		datastore.update(query, ops);
	}
}
