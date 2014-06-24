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
		ingatlanSzobakszama.setAlapErtek(1);
		
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
		
		// Kategoria: Ingatlan->Lakas
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
		
		// Kategoria: Ingatlan->Haz
		LinkedList<Attributum> hazList = new LinkedList<Attributum>();
		
		Attributum hazPince = new Attributum("haz-pince", AttributumTipus.YESNO, "Pince van?");
		
		hazList.add(ingatlanAlapterulet);
		hazList.add(ingatlanSzobakszama);
		hazList.add(ingatlanAllapot);
		hazList.add(ingatlanKomfort);
		hazList.add(ingatlanFutes);
		hazList.add(hazPince);
		KATEGORIA_ATTRIBUTUM.put("haz", hazList);
		
		// Kategoria: Ingatlan->Alberlet
		LinkedList<Attributum> alberletList = new LinkedList<Attributum>();
		
		alberletList.add(ingatlanAlapterulet);
		alberletList.add(ingatlanSzobakszama);
		alberletList.add(ingatlanAllapot);
		alberletList.add(ingatlanKomfort);
		alberletList.add(ingatlanFutes);
		KATEGORIA_ATTRIBUTUM.put("alberlet", alberletList);
		
		// Kategoria: Elektronikai cikk->Telefon
		LinkedList<Attributum> telefonList = new LinkedList<Attributum>();
		
		Attributum telefonMarka = new Attributum("telefon-marka", AttributumTipus.SELECT_SINGLE, "Márka");
		Map<String, Object> telefonMarkaErtekMap = new LinkedHashMap<String, Object>();
		telefonMarkaErtekMap.put("acer", "Acer");
		telefonMarkaErtekMap.put("alcatel", "Alcatel");
		telefonMarkaErtekMap.put("apple", "Apple/Iphone");
		telefonMarkaErtekMap.put("asus", "Asus");
		telefonMarkaErtekMap.put("blackberry", "Blackberry");
		telefonMarkaErtekMap.put("cat", "CAT");
		telefonMarkaErtekMap.put("htc", "HTC");
		telefonMarkaErtekMap.put("huawei", "Huawei");
		telefonMarkaErtekMap.put("lg", "LG");
		telefonMarkaErtekMap.put("motorola", "Motorola");
		telefonMarkaErtekMap.put("nokia", "Nokia");
		telefonMarkaErtekMap.put("samsung", "Samsung");
		telefonMarkaErtekMap.put("sony", "Sony");
		telefonMarkaErtekMap.put("zte", "ZTE");
		telefonMarkaErtekMap.put("telefon-marka-egyeb", "Egyéb");
		telefonMarka.setErtekMap(telefonMarkaErtekMap);
		telefonMarka.setKotelezo(true);
		
		telefonList.add(telefonMarka);
		KATEGORIA_ATTRIBUTUM.put("telefon", telefonList);
	}
}
