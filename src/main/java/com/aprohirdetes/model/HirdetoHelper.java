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
}
