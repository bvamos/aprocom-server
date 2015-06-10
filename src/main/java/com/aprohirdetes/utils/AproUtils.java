package com.aprohirdetes.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import javax.servlet.ServletContext;

import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.resource.Resource;

import com.aprohirdetes.model.Attributum;
import com.aprohirdetes.model.AttributumCache;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;

public class AproUtils {

	/**
	 * Visszaadja szoveges formaban, hogy a feladas ota mennyi ido telt el. Pl: 1 perce, 2 napja, 3 hete.
	 * 
	 * @param feladasDatuma long A feladas datuma
	 * @return Eltelt ido szoveges formaban
	 */
	public static String getHirdetesFeladvaSzoveg(long feladasDatuma) {
		String ret = "";
		
		long elteltIdo = (new Date().getTime() - feladasDatuma) / 1000;
		
		if(elteltIdo<60) {
			ret = "éppen most";
		} else if(elteltIdo>=60 && elteltIdo<3600){
			long perc = elteltIdo/60;
			ret = perc + " perce";
		} else if(elteltIdo>=3600 && elteltIdo<86400) {
			long ora = elteltIdo/3600;
			ret = ora + " órája";
		} else {
			long nap = elteltIdo/86400;
			ret = nap + " napja - " + new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date(feladasDatuma));
		}
		
		return ret;
	}
	
	/**
	 * Visszaadja szoveges formaban, hogy a lejaratig mennyi ido van hatra. Pl: 1 nap, 2 nap, 30 nap.
	 * 
	 * @param lejaratDatuma long A lejarat datuma
	 * @return Hatralevo ido szoveges formaban
	 */
	public static String getHirdetesLejaratSzoveg(long lejaratDatuma) {
		String ret = "";
		
		long elteltIdo = (lejaratDatuma - new Date().getTime()) / 1000;
		
		if(elteltIdo<86400) {
			ret = "Holnap";
		} else if(elteltIdo>=86400 && elteltIdo<432000){
			ret = "5 napon belül";
		} else {
			long nap = elteltIdo/86400;
			ret = nap + " nap múlva - " + new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date(lejaratDatuma));
		}
		
		return ret;
	}
	
	/**
	 * Session cookie torlese kilepesnel
	 * @param resource
	 * @param sessionId
	 */
	public static void removeSessionCookie(Resource resource, String sessionId) {
		ServletContext sc = (ServletContext) resource.getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		String contextPath = sc.getContextPath();
		
		// Cookie torlese
		try {
			resource.getResponse().getCookieSettings().removeAll("AproSession");
			
			CookieSetting cookieSetting = new CookieSetting("AproSession", "");
			cookieSetting.setVersion(0);
			cookieSetting.setAccessRestricted(true);
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
	 * @param resource
	 * @return
	 */
	public static Session getSession(Resource resource) {
		Session session = null;
		String sessionId = null;
		ServletContext sc = (ServletContext) resource.getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		String contextPath = sc.getContextPath();
		
		Cookie sessionCookie = resource.getRequest().getCookies().getFirst("AproSession");
		if(sessionCookie != null) {
			sessionId = sessionCookie.getValue();
			resource.getLogger().info("AproSession cookie betoltve: " + sessionId);
			
			// Session Cookie uj lejarat ertekkel: 31 nap mostantol
			CookieSetting cookieSetting = new CookieSetting(sessionCookie.getName(), sessionId);
			cookieSetting.setVersion(sessionCookie.getVersion());
			cookieSetting.setAccessRestricted(true);
			cookieSetting.setPath(contextPath + "/");
			cookieSetting.setComment("Session Id");
			cookieSetting.setMaxAge(3600*24*31);
			
			resource.getResponse().getCookieSettings().removeAll("AproSession");
			resource.getResponse().getCookieSettings().add(cookieSetting);
		}
		
		session = SessionHelper.load(sessionId);
		
		return session;
	}
	
	public static String getAttributumHtmlByKategoria(String kategoriaUrlNev) {
		StringBuilder ret = new StringBuilder();
		
		LinkedList<Attributum> attributumList = AttributumCache.getKATEGORIA_ATTRIBUTUM().get(kategoriaUrlNev);
		if(attributumList != null) {
			for(Attributum attr : attributumList) {
				ret.append(attr.toHtml());
			}
		}
		
		return ret.toString();
	}
	
	public static String getAttributumSearchHtmlByKategoria(String kategoriaUrlNev, Form query) {
		StringBuilder ret = new StringBuilder();
		
		LinkedList<Attributum> attributumList = AttributumCache.getKATEGORIA_ATTRIBUTUM().get(kategoriaUrlNev);
		if(attributumList != null) {
			for(Attributum attr : attributumList) {
				ret.append(attr.toSearchHtml(query));
			}
		}
		
		return ret.toString();
	}
	
}
