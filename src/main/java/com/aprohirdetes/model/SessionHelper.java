package com.aprohirdetes.model;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.aprohirdetes.server.AproApplication;
import com.aprohirdetes.utils.MongoUtils;
import com.aprohirdetes.utils.PasswordHash;

public class SessionHelper {

	/**
	 * Betolti a Session objektumot az adatbazisbol
	 * @param sessionId
	 * @return
	 */
	public static Session load(String sessionId) {
		Session session = null;
		
		Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
		Query<Session> query = datastore.createQuery(Session.class);
		
		query.criteria("sessionId").equal(sessionId);
		
		session = query.get();
		
		return session;
	}
	
	/**
	 * Felhasznalonev es jelszo alapjan autentikal egy Hirdetot 
	 * @param felhasznaloNev
	 * @param jelszo
	 * @return Hirdeto
	 */
	public static Hirdeto authenticate(String felhasznaloNev, String jelszo) {
		Hirdeto ret = null;
		
		if(felhasznaloNev == null || felhasznaloNev.isEmpty()) {
			return ret;
		}
		
		if(jelszo == null || jelszo.isEmpty()) {
			return ret;
		}
		
		Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
		Query<Hirdeto> query = datastore.createQuery(Hirdeto.class);
		
		query.criteria("email").equal(felhasznaloNev);
		
		Hirdeto hirdeto = query.get();
		
		if(hirdeto!=null) {
			String jelszoHash = hirdeto.getJelszo();
			try {
				ret = PasswordHash.validatePassword(jelszo, jelszoHash) ? hirdeto : null;
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
}
