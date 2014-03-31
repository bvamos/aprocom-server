package com.aprohirdetes.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.aprohirdetes.server.AproApplication;
import com.aprohirdetes.utils.MongoUtils;

public class SessionHelper {

	public static Session load(String sessionId) {
		Session session = null;
		
		Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
		Query<Session> query = datastore.createQuery(Session.class);
		
		query.criteria("sessionId").equal(sessionId);
		
		session = query.get();
		
		return session;
	}
	
	public static boolean authenticate(String felhasznaloNev, String jelszo) {
		if(felhasznaloNev == null || felhasznaloNev.isEmpty()) {
			return false;
		}
		
		if(jelszo == null || jelszo.isEmpty()) {
			return false;
		}
		
		Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
		Query<Hirdeto> query = datastore.createQuery(Hirdeto.class);
		
		query.criteria("email").equal(felhasznaloNev);
		query.criteria("jelszo").equal(jelszo);
		
		Hirdeto hirdeto = query.get();
		
		if(hirdeto!=null) {
			return true;
		}
		
		return false;
	}
}
