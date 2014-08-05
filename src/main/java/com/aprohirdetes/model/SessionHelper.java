package com.aprohirdetes.model;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

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
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Session> query = datastore.createQuery(Session.class);
		
		query.criteria("sessionId").equal(sessionId);
		
		session = query.get();
		
		if(session != null) {
			session.setUtolsoKeres(new Date());
		}
		
		return session;
	}
	
	/**
	 * Betolti a Hirdetohoz tartozo Session objectumot az adatbazisbol
	 * @param hirdetoId
	 * @return
	 */
	public static Session load(ObjectId hirdetoId) {
		Session session = null;
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Session> query = datastore.createQuery(Session.class);
		
		query.criteria("hirdetoId").equal(hirdetoId);
		
		session = query.get();
		
		if(session != null) {
			session.setUtolsoKeres(new Date());
		}
		
		return session;
	}
	
	/**
	 * Felhasznalonev es jelszo alapjan autentikal egy Hirdetot 
	 * @param felhasznaloNev
	 * @param jelszo
	 * @return Hirdeto objektum vagy null
	 * @throws InterruptedException 
	 */
	public static Hirdeto authenticate(String felhasznaloNev, String jelszo) {
		Hirdeto ret = null;
		
		// Varunk 2 mp-et, igy nem erdemes brute-force tamadast inditani, mert tul lassu a rendszer
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// Ezt nem lehet megszakitani
		}
		
		if(felhasznaloNev == null || felhasznaloNev.isEmpty()) {
			return ret;
		}
		
		if(jelszo == null || jelszo.isEmpty()) {
			return ret;
		}
		
		Datastore datastore = MongoUtils.getDatastore();
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
