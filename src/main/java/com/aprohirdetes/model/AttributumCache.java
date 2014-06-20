package com.aprohirdetes.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class AttributumCache {

	private static HashMap<String, LinkedList<Attributum>> KATEGORIA_ATTRIBUTUM = new HashMap<String, LinkedList<Attributum>>();

	public static HashMap<String, LinkedList<Attributum>> getKATEGORIA_ATTRIBUTUM() {
		return KATEGORIA_ATTRIBUTUM;
	}

	public static void loadAttributumCache() {
		
		// Kategoria: Lakas
		LinkedList<Attributum> lakasList = new LinkedList<Attributum>();
		
		Attributum lakasFalazat = new Attributum("lakas-falazat", AttributumTipus.RADIO, "Falazat anyaga");
		Map<String, Object> lakasFalazatErtekMap = new LinkedHashMap<String, Object>();
		lakasFalazatErtekMap.put("tegla", "Tégla");
		lakasFalazatErtekMap.put("panel", "Panel");
		lakasFalazat.setErtekMap(lakasFalazatErtekMap);
		lakasList.add(lakasFalazat);
		
		Attributum lakasAlapterulet = new Attributum("lakas-alapterulet", AttributumTipus.NUMBER, "Alapterület");
		lakasAlapterulet.setKotelezo(true);
		lakasList.add(lakasAlapterulet);
		
		KATEGORIA_ATTRIBUTUM.put("lakas", lakasList);
	}
}
