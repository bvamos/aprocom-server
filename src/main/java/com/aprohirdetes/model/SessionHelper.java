package com.aprohirdetes.model;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.aprohirdetes.exception.AproException;
import com.aprohirdetes.utils.MongoUtils;
import com.aprohirdetes.utils.PasswordHash;

public class SessionHelper {

	private SessionHelper(){};
	
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
	public static Hirdeto authenticate(String felhasznaloNev, String jelszo) throws AproException {
		Hirdeto ret = null;
		
		// Varunk 2 mp-et, igy nem erdemes brute-force tamadast inditani, mert tul lassu a rendszer
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// Ezt nem lehet megszakitani
		}
		
		if(felhasznaloNev == null || felhasznaloNev.isEmpty()) {
			throw(new AproException(0, "A felhasznalonev nem lehet ures"));
		}
		
		if(jelszo == null || jelszo.isEmpty()) {
			throw(new AproException(0, "A jelszo nem lehet ures"));
		}
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdeto> query = datastore.createQuery(Hirdeto.class);
		
		query.criteria("email").equal(felhasznaloNev);
		
		Hirdeto hirdeto = query.get();
		
		if(hirdeto!=null) {
			if(hirdeto.isHitelesitve()) {
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
			} else {
				throw(new AproException(2002, "A felhasználói fiókod még nem aktivált."));
			}
		}
		
		return ret;
	}
	
	/**
	 * HTTP Header alapu authentikacio az API-khoz.
	 * @param apiKey Auth-Key HTTP header erteke
	 * @return Hirdeto.apiKey egyedi mezovel azonositott Hirdeto objektum vagy null
	 */
	public static Hirdeto authenticate(String apiKey) {
		Hirdeto ret = null;
		
		// Varunk 2 mp-et, igy nem erdemes brute-force tamadast inditani, mert tul lassu a rendszer
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// Ezt nem lehet megszakitani
		}
		
		if(apiKey == null || apiKey.isEmpty()) {
			return ret;
		}
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdeto> query = datastore.createQuery(Hirdeto.class);
		
		query.criteria("apiKey").equal(apiKey);
		
		ret = query.get();
		
		return ret;
	}
}
