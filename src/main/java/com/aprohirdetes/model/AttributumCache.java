package com.aprohirdetes.model;

import java.util.HashMap;
import java.util.LinkedList;

public class AttributumCache {

	private static HashMap<String, LinkedList<Attributum>> KATEGORIA_ATTRIBUTUM = new HashMap<String, LinkedList<Attributum>>();

	public static HashMap<String, LinkedList<Attributum>> getKATEGORIA_ATTRIBUTUM() {
		return KATEGORIA_ATTRIBUTUM;
	}

	public static void loadAttributumCache() {
		LinkedList<Attributum> lakasList = new LinkedList<Attributum>();
		
		Attributum lakasAlapterulet = new Attributum("lakas-alapterulet", AttributumTipus.NUMBER, "Alapterület");
		lakasAlapterulet.setKotelezo(true);
		lakasList.add(lakasAlapterulet);
		
		KATEGORIA_ATTRIBUTUM.put("lakas", lakasList);
	}
}
