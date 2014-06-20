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
		
		// Ingatlanokkal kapcsolatos kozos attributumok
		Attributum ingatlanAlapterulet = new Attributum("ingatlan-alapterulet", AttributumTipus.NUMBER, "Alapterület");
		ingatlanAlapterulet.setKotelezo(true);
		ingatlanAlapterulet.setMertekEgyseg("m&sup2;");
		
		Attributum ingatlanSzobakszama = new Attributum("ingatlan-szobakszama", AttributumTipus.NUMBER, "Szobák száma");
		ingatlanSzobakszama.setKotelezo(true);
		
		Attributum ingatlanAllapot = new Attributum("ingatlan-allapot", AttributumTipus.SELECT_SINGLE, "Ingatlan állapota");
		Map<String, Object> ingatlanAllapotErtekMap = new LinkedHashMap<String, Object>();
		ingatlanAllapotErtekMap.put("ujepitesu", "Új építésű");
		ingatlanAllapotErtekMap.put("uj", "Újszerű");
		ingatlanAllapotErtekMap.put("felujitott", "Felújított");
		ingatlanAllapotErtekMap.put("jo", "Jó/Beköltözhető");
		ingatlanAllapotErtekMap.put("felujitando", "Felújítandó");
		ingatlanAllapot.setErtekMap(ingatlanAllapotErtekMap);
		
		Attributum ingatlanKomfort = new Attributum("ingatlan-komfort", AttributumTipus.SELECT_SINGLE, "Komfortfokozat");
		Map<String, Object> ingatlanKomfortErtekMap = new LinkedHashMap<String, Object>();
		ingatlanKomfortErtekMap.put("luxus", "Luxus");
		ingatlanKomfortErtekMap.put("osszkomfort", "Összkomfortos");
		ingatlanKomfortErtekMap.put("komfortos", "Komfortos");
		ingatlanKomfortErtekMap.put("komfortnelkuli", "Komfort nélküli");
		ingatlanKomfort.setErtekMap(ingatlanKomfortErtekMap);
		
		Attributum ingatlanFutes = new Attributum("ingatlan-futes", AttributumTipus.SELECT_SINGLE, "Fűtés típusa");
		Map<String, Object> ingatlanFutesErtekMap = new LinkedHashMap<String, Object>();
		ingatlanFutesErtekMap.put("cirko", "Gáz (cirkó)");
		ingatlanFutesErtekMap.put("kovektor", "Gáz (konvektor)");
		ingatlanFutesErtekMap.put("elektormos", "Elektromos");
		ingatlanFutesErtekMap.put("tavho", "Távhő");
		ingatlanFutesErtekMap.put("tavhoegyedi", "Távhő egyedi méressel");
		ingatlanFutesErtekMap.put("kozponti", "Házközponti");
		ingatlanFutesErtekMap.put("kozpontiegyedi", "Házközponti egyedi méressel");
		ingatlanFutesErtekMap.put("egyeb", "Egyéb");
		ingatlanFutes.setErtekMap(ingatlanFutesErtekMap);
		ingatlanFutes.setKotelezo(true);
		
		// Kategoria: Lakas
		LinkedList<Attributum> lakasList = new LinkedList<Attributum>();
		
		Attributum lakasFalazat = new Attributum("lakas-falazat", AttributumTipus.RADIO, "Falazat anyaga");
		Map<String, Object> lakasFalazatErtekMap = new LinkedHashMap<String, Object>();
		lakasFalazatErtekMap.put("tegla", "Tégla");
		lakasFalazatErtekMap.put("panel", "Panel");
		lakasFalazat.setErtekMap(lakasFalazatErtekMap);
		
		Attributum lakasEmelet = new Attributum("lakas-emelet", AttributumTipus.NUMBER, "Emelet");
		
		lakasList.add(lakasFalazat);
		lakasList.add(ingatlanAlapterulet);
		lakasList.add(ingatlanSzobakszama);
		lakasList.add(ingatlanAllapot);
		lakasList.add(lakasEmelet);
		lakasList.add(ingatlanKomfort);
		lakasList.add(ingatlanFutes);
		KATEGORIA_ATTRIBUTUM.put("lakas", lakasList);
	}
}
