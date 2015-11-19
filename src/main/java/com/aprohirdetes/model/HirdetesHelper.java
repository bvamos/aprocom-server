package com.aprohirdetes.model;

import java.util.Date;
import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import com.aprohirdetes.exception.HirdetesValidationException;
import com.aprohirdetes.utils.MongoUtils;

public class HirdetesHelper {

	public static Hirdetes load(String hirdetesId) {
		return load(new ObjectId(hirdetesId));
	}
	
	public static Hirdetes load(ObjectId hirdetesId) {
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		query.criteria("id").equal(hirdetesId);
		Hirdetes hirdetes = query.get();
		
		if(hirdetes != null) {
			// Kepek betoltese
			Query<HirdetesKep> queryKep = datastore.createQuery(HirdetesKep.class);
			queryKep.criteria("hirdetesId").equal(hirdetesId);
	
			LinkedList<HirdetesKep> hirdetesKepList = new LinkedList<HirdetesKep>();
			for(HirdetesKep hk : queryKep) {
				hirdetesKepList.add(hk);
			}
			if(hirdetesKepList.size()>0) {
				hirdetes.setKepek(hirdetesKepList);
			}
		}
		
		return hirdetes;
	}
	
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
	 * Hirdetes statuszanak beallitasa TOROLVE ertekre 
	 * es a torolveDatum beallitasa az aktualis idopontra
	 * @param hirdetesId A Hirdetes azonositoja
	 * @throws Exception
	 */
	public static void delete(ObjectId hirdetesId) throws Exception {
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		query.criteria("id").equal(hirdetesId);
		UpdateOperations<Hirdetes> ops = datastore.createUpdateOperations(Hirdetes.class).set("torolveDatum", new Date()).set("statusz", Hirdetes.Statusz.TOROLVE.value());
		datastore.update(query, ops);
	}
	
	/**
	 * Hirdetes statuszanak beallitasa a megadott ertekre 
	 * es a torolveDatum beallitasa az aktualis idopontra
	 * @param hirdetesId A Hirdetes azonositoja
	 * @throws Exception
	 */
	public static void delete(ObjectId hirdetesId, Hirdetes.Statusz statusz) throws Exception {
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		query.criteria("id").equal(hirdetesId);
		UpdateOperations<Hirdetes> ops = datastore.createUpdateOperations(Hirdetes.class).set("torolveDatum", new Date()).set("statusz", statusz.value());
		datastore.update(query, ops);
	}
	
	/**
	 * A Hirdetes tartalmanak validacioja az egesz adatbazist felhasznalva.
	 * Pl.: Van-e ugyanilyen feladoval es ugyanilyen Cimsorral mar hirdetes az adatbazisban...
	 * @param hirdetes Hirdetes
	 * @throws HirdetesValidationException
	 * @see Hirdetes.validate()
	 */
	public static void validate(Hirdetes hirdetes) throws HirdetesValidationException {
		if(hirdetes.getHirdeto()==null) {
			throw new HirdetesValidationException(1016, "A Hirdeto adatainak megadasa kotelezo");
		}
		if(hirdetes.getHirdeto().getEmail()==null) {
			throw new HirdetesValidationException(1015, "A Hirdeto email címe nem lehet üres");
		}
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		query.criteria("hirdeto.email").exists();
		query.criteria("hirdeto.email").equal(hirdetes.getHirdeto().getEmail());
		query.criteria("cim").equal(hirdetes.getCim());
		query.criteria("statusz").equal(Hirdetes.Statusz.JOVAHAGYVA.value());
		
		// TODO: Lehet, hogy a query.get() gyorsabb, mint a countAll()
		if(query.countAll()>0) {
			throw new HirdetesValidationException(1013, "A megadott email címmel és Rövid leírással már létezik aktív hirdetés");
		}
	}
	
	/**
	 * Megnoveli az adatbazisban a megadott hirdetes megjeleneseinek szamat
	 * @param hirdetes
	 */
	public static void increaseMegjelenes(Hirdetes hirdetes) {
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
		query.criteria("id").equal(hirdetes.getId());
		UpdateOperations<Hirdetes> ops = datastore.createUpdateOperations(Hirdetes.class).set("megjelenes", hirdetes.getMegjelenes()+1);
		datastore.update(query, ops);
		//UpdateResults<Hirdetes> results = datastore.update(query, ops);
	    //return (int)results.getWriteResult().getField("megjelenes");
	}
}
