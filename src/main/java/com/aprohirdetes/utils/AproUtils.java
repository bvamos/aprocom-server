package com.aprohirdetes.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
		} else {
			ret = new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date(feladasDatuma));
		}
		
		return ret;
	}
}
