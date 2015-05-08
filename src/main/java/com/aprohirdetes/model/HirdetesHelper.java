package com.aprohirdetes.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.aprohirdetes.exception.HirdetesValidationException;
import com.aprohirdetes.utils.MongoUtils;

public class HirdetesHelper {

	/**
	 * A megadott Hirdetes lejarErtesites mezojenek beallitasa a megadott datumra
	 * @see Hirdetes
	 * @param hirdetesId A Hirdetes azonositoja
	 * @param datum A beallitando datum. Az ora, perc, mperc mindig 00, mert csak a datum (aktualis nap) szamit a lejaratkor!
	 */
	public static void saveLejarErtesites(ObjectId hirdetesId, Date datum) throws Exception {
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		query.criteria("id").equal(hirdetesId);
		UpdateOperations<Hirdetes> ops = datastore.createUpdateOperations(Hirdetes.class).set("lejarErtesites", datum);
		datastore.update(query, ops);
	}
	
	/**
	 * Hirdetes torolve mezojenek igazra allitasa es a torolveDatum beallitasa az aktualis idopontra
	 * @param hirdetesId A Hirdetes azonositoja
	 * @throws Exception
	 */
	public static void delete(ObjectId hirdetesId) throws Exception {
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		query.criteria("id").equal(hirdetesId);
		UpdateOperations<Hirdetes> ops = datastore.createUpdateOperations(Hirdetes.class).set("torolve", true).set("torolveDatum", new Date());
		datastore.update(query, ops);
	}
	
	/**
	 * A Hirdetes tartalmanak validacioja az egesz adatbazist felhasznalva.
	 * Pl.: Van-e ugyanilyen feladoval es ugyanilyen Cimsorral mar hirdetes az adatbazisban...
	 * @param hirdetesId A Hirdetes azonositoja
	 * @throws HirdetesValidationException
	 * @see Hirdetes.validate()
	 */
	public static void validate(ObjectId hirdetesId) throws HirdetesValidationException {
		
	}
}
