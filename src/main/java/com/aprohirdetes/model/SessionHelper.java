package com.aprohirdetes.model;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.resource.Resource;

import com.aprohirdetes.exception.AproException;
import com.aprohirdetes.server.AproApplication;
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
	
	public static Session login(String felhasznaloNev, String jelszo) throws AproException {
		Hirdeto hirdeto = null;
		Session session = null;
		
		if ((hirdeto = SessionHelper.authenticate(felhasznaloNev, jelszo)) != null) {
			// Session betoltese
			session = SessionHelper.load(hirdeto.getId());
			if(session == null) {
				// Nincs session az adatbazisban, generalunk ujat, es elmentjuk
				session = new Session();
				session.setSessionId(UUID.randomUUID().toString());
				session.setHirdetoId(hirdeto.getId());
			}
			Context.getCurrentLogger().info("Sikeres belepes: " + hirdeto.getEmail() + "; AproSession: " + session.getSessionId());
			
			Datastore datastore = MongoUtils.getDatastore();
			
			// Session mentese az adatbazisba
			datastore.save(session);
			
			// Utolso belepes mentese
			datastore.update(hirdeto, datastore.createUpdateOperations(Hirdeto.class).set("utolsoBelepes", new Date()));
		}
		
		return session;
	}
	
	public static Session login(Hirdeto hirdeto) throws AproException {
		Session session = null;
		
		// Session betoltese
		session = SessionHelper.load(hirdeto.getId());
		if(session == null) {
			// Nincs session az adatbazisban, generalunk ujat, es elmentjuk
			session = new Session();
			session.setSessionId(UUID.randomUUID().toString());
			session.setHirdetoId(hirdeto.getId());
		}
		Context.getCurrentLogger().info("Sikeres belepes: " + hirdeto.getEmail() + "; AproSession: " + session.getSessionId());
		
		Datastore datastore = MongoUtils.getDatastore();
		
		// Session mentese az adatbazisba
		datastore.save(session);
		
		// Utolso belepes mentese
		datastore.update(hirdeto, datastore.createUpdateOperations(Hirdeto.class).set("utolsoBelepes", new Date()));
		
		return session;
	}
	
	public static void logout() throws AproException {
		// TODO: Logout
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
			throw(new AproException(0, "A felhasználónév nem lehet üres"));
		}
		
		if(jelszo == null || jelszo.isEmpty()) {
			throw(new AproException(0, "A jelszó nem lehet üres"));
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
		
		if(ret==null) {
			throw(new AproException(2003, "Hibás felhasználónév vagy jelszó"));
		}
		
		return ret;
	}
	
	/**
	 * HTTP Header alapu authentikacio az API-khoz.
	 * @param apiKey API-Key HTTP header erteke
	 * @return Hirdeto.apiKey egyedi mezovel azonositott Hirdeto objektum vagy null
	 */
	public static Hirdeto authenticate(String apiKey) {
		Hirdeto ret = null;
		
		if(apiKey == null || apiKey.isEmpty()) {
			return ret;
		}
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdeto> query = datastore.createQuery(Hirdeto.class);
		
		query.criteria("apiKey").equal(apiKey);
		
		ret = query.get();
		
		return ret;
	}
	
	public static void setSessionCookie(Resource resource, String sessionId) {
		ServletContext sc = (ServletContext) resource.getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		String contextPath = sc.getContextPath();
		
		// Session Cookie uj lejarat ertekkel: 31 nap mostantol
		CookieSetting cookieSetting = new CookieSetting("AproSession", sessionId);
		cookieSetting.setVersion(0);
		cookieSetting.setAccessRestricted(false);
		cookieSetting.setPath(contextPath + "/");
		cookieSetting.setComment("Session Id");
		cookieSetting.setMaxAge(3600*24*31);
		
		resource.getResponse().getCookieSettings().removeAll("AproSession");
		resource.getResponse().getCookieSettings().add(cookieSetting);
		Context.getCurrentLogger().info("AproSession cookie hozzaadva: " + cookieSetting.toString());
	}
	
	public static void updateSessionCookie(Response response, Cookie cookie) {
		CookieSetting cookieSetting = new CookieSetting("AproSession", cookie.getValue());
		cookieSetting.setVersion(0);
		cookieSetting.setAccessRestricted(false);
		cookieSetting.setPath("/");
		cookieSetting.setComment("Session Id");
		cookieSetting.setMaxAge(3600*24*31);
		
		response.getCookieSettings().removeAll("AproSession");
		response.getCookieSettings().add(cookieSetting);
		Context.getCurrentLogger().info("AproSession cookie modositva: " + cookieSetting.toString());
	}
	
	/**
	 * Session cookie torlese kilepesnel
	 * @param resource
	 */
	public static void removeSessionCookie(Resource resource) {
		ServletContext sc = (ServletContext) resource.getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		String contextPath = sc.getContextPath();
		
		// Cookie torlese
		try {
			resource.getResponse().getCookieSettings().removeAll("AproSession");
			
			CookieSetting cookieSetting = new CookieSetting("AproSession", "");
			cookieSetting.setVersion(0);
			cookieSetting.setAccessRestricted(false);
			cookieSetting.setPath(contextPath + "/");
			cookieSetting.setComment("Session Id");
			cookieSetting.setMaxAge(0);
			resource.getResponse().getCookieSettings().add(cookieSetting);
			
			resource.getLogger().info("AproSession cookie torolve");
		} catch(NullPointerException npe) {
			resource.getLogger().severe("Hiba az AproSession cookie torlesenel: " + npe.getMessage());
		}
	}
	
	/**
	 * Session betoltese a Session cookie alapjan es a cookie lejaratanak frissitese
	 * @param request
	 * @return
	 */
	public static Session getSession(Request request, Response response) {
		Session session = null;
		String sessionId = null;
		
		Cookie sessionCookie = request.getCookies().getFirst("AproSession");
		if(sessionCookie == null) {
			return null;
		}
		
		sessionId = sessionCookie.getValue();
		AproApplication.getCurrent().getLogger().info("AproSession cookie betoltve: " + sessionId);
		
		updateSessionCookie(response, sessionCookie);
		session = SessionHelper.load(sessionId);
		
		return session;
	}
	
	/**
	 * Session betoltese a Session cookie alapjan es a cookie lejaratanak frissitese
	 * @param resource
	 * @return
	 */
	public static Session getSession(Resource resource) {
		return getSession(resource.getRequest(), resource.getResponse());
	}
	
}
