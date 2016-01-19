package com.aprohirdetes.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import org.restlet.data.Form;
import com.aprohirdetes.model.Attributum;
import com.aprohirdetes.model.AttributumCache;

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
		
		if(elteltIdo<0) {
			long nap = Math.abs(elteltIdo/86400);
			ret = nap + " napja - " + new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date(lejaratDatuma));
		} else if(elteltIdo<86400) {
			ret = "Holnap";
		} else if(elteltIdo>=86400 && elteltIdo<432000){
			ret = "5 napon belül";
		} else {
			long nap = elteltIdo/86400;
			ret = nap + " nap múlva - " + new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date(lejaratDatuma));
		}
		
		return ret;
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
