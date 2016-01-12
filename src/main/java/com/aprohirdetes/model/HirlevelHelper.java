package com.aprohirdetes.model;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.aprohirdetes.utils.MongoUtils;

public class HirlevelHelper {

	private HirlevelHelper() {
		// Utility class
	}

	public static Query<Hirlevel> getHirlevelList() {
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirlevel> query = datastore.createQuery(Hirlevel.class);
		
		return query;
	}
}
