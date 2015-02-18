package com.aprohirdetes.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.aprohirdetes.utils.MongoUtils;

public class HirdetesHelper {

	public static void saveLejarErtesites(ObjectId hirdetesId, Date d) {
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		query.criteria("id").equal(hirdetesId);
		UpdateOperations<Hirdetes> ops = datastore.createUpdateOperations(Hirdetes.class).set("lejarErtesites", d);
		datastore.update(query, ops);
	}
}
