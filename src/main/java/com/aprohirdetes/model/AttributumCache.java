package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.restlet.Context;

public class AttributumCache {

	private static HashMap<String, LinkedList<Attributum>> KATEGORIA_ATTRIBUTUM = new HashMap<String, LinkedList<Attributum>>();

	public static HashMap<String, LinkedList<Attributum>> getKATEGORIA_ATTRIBUTUM() {
		return KATEGORIA_ATTRIBUTUM;
	}

	public static void loadAttributumCache() {
		Context.getCurrentLogger().info("Loading AttributumCache...");
		
		// **********************
		// Altalanos attributumok
		Attributum allapot = new AttributumSelectSingle("allapot", "Állapot");
		Map<String, String> allapotErtekMap = new LinkedHashMap<String, String>();
		allapotErtekMap.put("h", "Használt");
		allapotErtekMap.put("u", "Új");
		allapot.setErtekMap(allapotErtekMap);
		
		// ********************************************
		// Ingatlanokkal kapcsolatos kozos attributumok
		AttributumNumber ingatlanAlapterulet = new AttributumNumber("ingatlan-alapterulet", "Alapterület");
		ingatlanAlapterulet.setKotelezo(true);
		ingatlanAlapterulet.setMertekEgyseg("m&sup2;");
		
		Attributum ingatlanSzobakszama = new AttributumNumber("ingatlan-szobakszama", "Szobák száma");
		ingatlanSzobakszama.setKotelezo(true);
		ingatlanSzobakszama.setAlapErtek(1);
		
		Attributum ingatlanAllapot = new AttributumSelectSingle("ingatlan-allapot", "Ingatlan állapota");
		Map<String, String> ingatlanAllapotErtekMap = new LinkedHashMap<String, String>();
		ingatlanAllapotErtekMap.put("ujepitesu", "Új építésű");
		ingatlanAllapotErtekMap.put("uj", "Újszerű");
		ingatlanAllapotErtekMap.put("felujitott", "Felújított");
		ingatlanAllapotErtekMap.put("jo", "Jó/Beköltözhető");
		ingatlanAllapotErtekMap.put("felujitando", "Felújítandó");
		ingatlanAllapot.setErtekMap(ingatlanAllapotErtekMap);
		
		Attributum ingatlanKomfort = new AttributumSelectSingle("ingatlan-komfort", "Komfortfokozat");
		Map<String, String> ingatlanKomfortErtekMap = new LinkedHashMap<String, String>();
		ingatlanKomfortErtekMap.put("luxus", "Luxus");
		ingatlanKomfortErtekMap.put("osszkomfort", "Összkomfortos");
		ingatlanKomfortErtekMap.put("komfortos", "Komfortos");
		ingatlanKomfortErtekMap.put("komfortnelkuli", "Komfort nélküli");
		ingatlanKomfort.setErtekMap(ingatlanKomfortErtekMap);
		
		Attributum ingatlanFutes = new AttributumSelectSingle("ingatlan-futes", "Fűtés típusa");
		Map<String, String> ingatlanFutesErtekMap = new LinkedHashMap<String, String>();
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
		
		// **************************
		// Kategoria: Ingatlan->Epitesi Telek
		LinkedList<Attributum> telekList = new LinkedList<Attributum>();
		
		Attributum telekAlapterulet = new AttributumNumber("telek-alapterulet", "Telek alapterülete");
		telekAlapterulet.setKotelezo(true);
		telekAlapterulet.setMertekEgyseg("m&sup2;");
		
		telekList.add(telekAlapterulet);
		KATEGORIA_ATTRIBUTUM.put("epitesi-telek", telekList);
		
		// ************************************
		// Kategoria: Ingatlan->Szanto, kiskert
		LinkedList<Attributum> szantoList = new LinkedList<Attributum>();
		szantoList.add(ingatlanAlapterulet);
		
		KATEGORIA_ATTRIBUTUM.put("szanto-kiskert", szantoList);
		
		// **************************
		// Kategoria: Ingatlan->Lakas
		LinkedList<Attributum> lakasList = new LinkedList<Attributum>();
		
		Attributum lakasFalazat = new AttributumRadio("lakas-falazat", "Falazat anyaga");
		Map<String, String> lakasFalazatErtekMap = new LinkedHashMap<String, String>();
		lakasFalazatErtekMap.put("tegla", "Tégla");
		lakasFalazatErtekMap.put("panel", "Panel");
		lakasFalazat.setErtekMap(lakasFalazatErtekMap);
		
		Attributum lakasEmelet = new AttributumNumber("lakas-emelet", "Emelet");
		
		lakasList.add(lakasFalazat);
		lakasList.add(ingatlanAlapterulet);
		lakasList.add(ingatlanSzobakszama);
		lakasList.add(ingatlanAllapot);
		lakasList.add(lakasEmelet);
		lakasList.add(ingatlanKomfort);
		lakasList.add(ingatlanFutes);
		KATEGORIA_ATTRIBUTUM.put("lakas", lakasList);
		
		// ************************
		// Kategoria: Ingatlan->Haz
		LinkedList<Attributum> hazList = new LinkedList<Attributum>();
		
		Attributum hazPince = new AttributumYesNo("haz-pince", "Pince van?");
		
		hazList.add(ingatlanAlapterulet);
		hazList.add(ingatlanSzobakszama);
		hazList.add(ingatlanAllapot);
		hazList.add(ingatlanKomfort);
		hazList.add(ingatlanFutes);
		hazList.add(hazPince);
		hazList.add(telekAlapterulet);
		KATEGORIA_ATTRIBUTUM.put("haz", hazList);
		
		// *****************************
		// Kategoria: Ingatlan->Alberlet
		LinkedList<Attributum> alberletList = new LinkedList<Attributum>();
		
		alberletList.add(ingatlanAlapterulet);
		alberletList.add(ingatlanSzobakszama);
		alberletList.add(ingatlanAllapot);
		alberletList.add(ingatlanKomfort);
		alberletList.add(ingatlanFutes);
		KATEGORIA_ATTRIBUTUM.put("alberlet", alberletList);
		
		// *****************************************
		// Kategoria: Ingatlan->Iroda, uzlethelyiseg
		LinkedList<Attributum> irodaList = new LinkedList<Attributum>();
		irodaList.add(ingatlanAlapterulet);
		KATEGORIA_ATTRIBUTUM.put("iroda-uzlethelyiseg", irodaList);
		
		// ****************************
		// Kategoria: Ingatlan->Nyaralo
		KATEGORIA_ATTRIBUTUM.put("nyaralo", irodaList);
		
		// ***************************
		// Kategoria: Ingatlan->Garazs
		Attributum garazsElhelyezkedes = new AttributumRadio("garazs-elhelyezkedes", "Elhelyezkedés");
		Map<String, String> garazsElhelyezkedesErtekMap = new LinkedHashMap<String, String>();
		garazsElhelyezkedesErtekMap.put("o", "Önálló");
		garazsElhelyezkedesErtekMap.put("t", "Teremgarázs");
		garazsElhelyezkedes.setErtekMap(garazsElhelyezkedesErtekMap);
		garazsElhelyezkedes.setAlapErtek("o");
		
		LinkedList<Attributum> garazsList = new LinkedList<Attributum>();
		garazsList.add(ingatlanAlapterulet);
		garazsList.add(garazsElhelyezkedes);
		KATEGORIA_ATTRIBUTUM.put("garazs", garazsList);
		
		// *************************************
		// Kategoria: Elektronikai cikk->Telefon
		
		Attributum telefonMarka = new AttributumSelectSingle("telefon-marka", "Márka");
		Map<String, String> telefonMarkaErtekMap = new LinkedHashMap<String, String>();
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
		
		LinkedList<Attributum> telefonList = new LinkedList<Attributum>();
		telefonList.add(allapot);
		telefonList.add(telefonMarka);
		KATEGORIA_ATTRIBUTUM.put("telefon", telefonList);
		
		// *****************************
		// Kategoria: Jarmu->Gumi, felni
		
		Attributum gumiMarka = new AttributumSelectSingle("gumi-marka", "Márka");
		Map<String, String> gumiMarkaErtekMap = new LinkedHashMap<String, String>();
		//gumiMarkaErtekMap.put("", "");
		gumiMarkaErtekMap.put("barum", "Barum");
		gumiMarkaErtekMap.put("bfgoodrich", "BFGoodrich");
		gumiMarkaErtekMap.put("bridgestone", "Bridgestone");
		gumiMarkaErtekMap.put("continental", "Continental");
		gumiMarkaErtekMap.put("dayton", "Dayton");
		gumiMarkaErtekMap.put("debica", "Debica");
		gumiMarkaErtekMap.put("delinte", "Delinte");
		gumiMarkaErtekMap.put("dunlop", "Dunlop");
		gumiMarkaErtekMap.put("falken", "Falken");
		gumiMarkaErtekMap.put("firestone", "Firestone");
		gumiMarkaErtekMap.put("fulda", "Fulda");
		gumiMarkaErtekMap.put("goodyear", "Goodyear");
		gumiMarkaErtekMap.put("gtradial", "GT Radial");
		gumiMarkaErtekMap.put("hankook", "Hankook");
		gumiMarkaErtekMap.put("insa", "Insa Turbo");
		gumiMarkaErtekMap.put("kingstar", "Kingstar");
		gumiMarkaErtekMap.put("kleber", "Kleber");
		gumiMarkaErtekMap.put("kormoran", "Kormoran");
		gumiMarkaErtekMap.put("kumho", "Kumho Tires");
		gumiMarkaErtekMap.put("matador", "Matador");
		gumiMarkaErtekMap.put("michelin", "Michelin");
		gumiMarkaErtekMap.put("nokian", "Nokian");
		gumiMarkaErtekMap.put("pirelli", "Pirelli");
		gumiMarkaErtekMap.put("points", "Point S");
		gumiMarkaErtekMap.put("sava", "Sava");
		gumiMarkaErtekMap.put("semperit", "Semperit");
		gumiMarkaErtekMap.put("taurus", "Taurus");
		gumiMarkaErtekMap.put("toyo", "Toyo");
		gumiMarkaErtekMap.put("uniroyal", "Uniroyal");
		gumiMarkaErtekMap.put("voltyre", "Voltyre");
		gumiMarkaErtekMap.put("yokohama", "Yokohama");
		gumiMarkaErtekMap.put("gumi-marka-egyeb", "Egyéb");
		gumiMarka.setErtekMap(gumiMarkaErtekMap);
		gumiMarka.setKotelezo(true);
		
		Attributum gumiSzelesseg = new AttributumSelectSingle("gumi-szelesseg", "Szélesség");
		Map<String, String> gumiSzelessegErtekMap = new LinkedHashMap<String, String>();
		gumiSzelessegErtekMap.put("110", "110");
		gumiSzelessegErtekMap.put("120", "120");
		gumiSzelessegErtekMap.put("130", "130");
		gumiSzelessegErtekMap.put("135", "135");
		gumiSzelessegErtekMap.put("145", "145");
		gumiSzelessegErtekMap.put("155", "155");
		gumiSzelessegErtekMap.put("165", "165");
		gumiSzelessegErtekMap.put("175", "175");
		gumiSzelessegErtekMap.put("185", "185");
		gumiSzelessegErtekMap.put("195", "195");
		gumiSzelessegErtekMap.put("205", "205");
		gumiSzelessegErtekMap.put("215", "215");
		gumiSzelessegErtekMap.put("225", "225");
		gumiSzelessegErtekMap.put("235", "235");
		gumiSzelessegErtekMap.put("245", "245");
		gumiSzelessegErtekMap.put("255", "255");
		gumiSzelesseg.setErtekMap(gumiSzelessegErtekMap);
		gumiSzelesseg.setKotelezo(true);
		
		Attributum gumiAtmero = new AttributumSelectSingle("gumi-atmero", "Átmérő");
		Map<String, String> gumiAtmeroErtekMap = new LinkedHashMap<String, String>();
		gumiAtmeroErtekMap.put("10", "R10");
		gumiAtmeroErtekMap.put("11", "R11");
		gumiAtmeroErtekMap.put("12", "R12");
		gumiAtmeroErtekMap.put("13", "R13");
		gumiAtmeroErtekMap.put("14", "R14");
		gumiAtmeroErtekMap.put("15", "R15");
		gumiAtmeroErtekMap.put("16", "R16");
		gumiAtmeroErtekMap.put("17", "R17");
		gumiAtmeroErtekMap.put("18", "R18");
		gumiAtmeroErtekMap.put("19", "R19");
		gumiAtmeroErtekMap.put("20", "R20");
		gumiAtmeroErtekMap.put("21", "R21");
		gumiAtmeroErtekMap.put("22", "R22");
		gumiAtmeroErtekMap.put("23", "R23");
		gumiAtmeroErtekMap.put("24", "R24");
		gumiAtmeroErtekMap.put("25", "R25");
		gumiAtmeroErtekMap.put("26", "R26");
		gumiAtmeroErtekMap.put("27", "R27");
		gumiAtmeroErtekMap.put("28", "R28");
		gumiAtmeroErtekMap.put("29", "R29");
		gumiAtmeroErtekMap.put("30", "R30");
		gumiAtmero.setErtekMap(gumiAtmeroErtekMap);
		gumiAtmero.setKotelezo(true);
		
		Attributum gumiPer = new AttributumSelectSingle("gumi-per", "Per");
		Map<String, String> gumiPerErtekMap = new LinkedHashMap<String, String>();
		gumiPerErtekMap.put("20", "20");
		gumiPerErtekMap.put("25", "25");
		gumiPerErtekMap.put("30", "30");
		gumiPerErtekMap.put("35", "35");
		gumiPerErtekMap.put("40", "40");
		gumiPerErtekMap.put("45", "45");
		gumiPerErtekMap.put("50", "50");
		gumiPerErtekMap.put("55", "55");
		gumiPerErtekMap.put("60", "60");
		gumiPerErtekMap.put("65", "65");
		gumiPerErtekMap.put("70", "70");
		gumiPerErtekMap.put("75", "75");
		gumiPerErtekMap.put("80", "80");
		gumiPerErtekMap.put("85", "85");
		gumiPerErtekMap.put("90", "90");
		gumiPerErtekMap.put("95", "95");
		gumiPerErtekMap.put("100", "100");
		gumiPer.setErtekMap(gumiPerErtekMap);
		
		Attributum gumiIdoszak = new AttributumSelectSingle("gumi-idoszak", "Időszak");
		Map<String, String> gumiIdoszakErtekMap = new LinkedHashMap<String, String>();
		gumiIdoszakErtekMap.put("ny", "Nyári");
		gumiIdoszakErtekMap.put("t", "Téli");
		gumiIdoszakErtekMap.put("4", "4 évszakos");
		gumiIdoszak.setErtekMap(gumiIdoszakErtekMap);
		
		LinkedList<Attributum> gumiList = new LinkedList<Attributum>();
		gumiList.add(allapot);
		gumiList.add(gumiIdoszak);
		gumiList.add(gumiMarka);
		gumiList.add(gumiSzelesseg);
		gumiList.add(gumiPer);
		gumiList.add(gumiAtmero);
		KATEGORIA_ATTRIBUTUM.put("gumi-felni", gumiList);
		
		/**********************************
		 * Kategoria: Jarmu->Szemelyauto
		 */
		Attributum szautoMarka = new AttributumSelectSingle("szauto-marka", "Márka");
		Map<String, String> szautoMarkaErtekMap = new LinkedHashMap<String, String>();
		//szautoMarkaErtekMap.put("", "");
		szautoMarkaErtekMap.put("alfaromeo", "Alfa Romeo");
		szautoMarkaErtekMap.put("audi", "Audi");
		szautoMarkaErtekMap.put("bmw", "BMW");
		szautoMarkaErtekMap.put("chevrolet", "Chevrolet");
		szautoMarkaErtekMap.put("citroen", "Citroen");
		szautoMarkaErtekMap.put("dacia", "dacia");
		szautoMarkaErtekMap.put("daewoo", "Daewoo");
		szautoMarkaErtekMap.put("fiat", "Fiat");
		szautoMarkaErtekMap.put("ford", "Ford");
		szautoMarkaErtekMap.put("honda", "Honda");
		szautoMarkaErtekMap.put("hyundai", "Hyundai");
		szautoMarkaErtekMap.put("jaguar", "Jaguar");
		szautoMarkaErtekMap.put("kia", "Kia");
		szautoMarkaErtekMap.put("lada", "Lada");
		szautoMarkaErtekMap.put("lancia", "Lancia");
		szautoMarkaErtekMap.put("landrover", "Land Rover");
		szautoMarkaErtekMap.put("lexus", "Lexus");
		szautoMarkaErtekMap.put("mazda", "Mazda");
		szautoMarkaErtekMap.put("mercedes", "Mercedes");
		szautoMarkaErtekMap.put("mini", "Mini");
		szautoMarkaErtekMap.put("mitsubishi", "Mitsubushi");
		szautoMarkaErtekMap.put("nissan", "Nissan");
		szautoMarkaErtekMap.put("opel", "Opel");
		szautoMarkaErtekMap.put("peugeot", "Peugeot");
		szautoMarkaErtekMap.put("porsche", "Porsche");
		szautoMarkaErtekMap.put("renault", "Renault");
		szautoMarkaErtekMap.put("rover", "Rover");
		szautoMarkaErtekMap.put("saab", "Saab");
		szautoMarkaErtekMap.put("seat", "Seat");
		szautoMarkaErtekMap.put("skoda", "Skoda");
		szautoMarkaErtekMap.put("smart", "Smart");
		szautoMarkaErtekMap.put("subaru", "Subaru");
		szautoMarkaErtekMap.put("suzuki", "Suzuki");
		szautoMarkaErtekMap.put("toyota", "Toyota");
		szautoMarkaErtekMap.put("trabant", "Trabant");
		szautoMarkaErtekMap.put("vw", "Volkswagen");
		szautoMarkaErtekMap.put("volvo", "Volvo");
		szautoMarkaErtekMap.put("szauto-egyeb", "Egyéb");
		szautoMarka.setErtekMap(szautoMarkaErtekMap);
		
		AttributumNumber szautoEvjarat = new AttributumNumber("szauto-evjarat", "Évjárat");
		szautoEvjarat.setKotelezo(true);
		
		AttributumNumber szautoKm = new AttributumNumber("szauto-km", "Km-óra állása");
		
		Attributum szautoUzemanyag = new AttributumSelectSingle("szauto-uzemanyag", "Üzemanyag");
		Map<String, String> szautoUzemanyagErtekMap = new LinkedHashMap<String, String>();
		//szautoUzemanyagErtekMap.put("", "");
		szautoUzemanyagErtekMap.put("benzin", "Benzin");
		szautoUzemanyagErtekMap.put("diesel", "Diesel");
		szautoUzemanyagErtekMap.put("hibrid-benzin", "Hibrid/Benzin");
		szautoUzemanyagErtekMap.put("hibrid-diesel", "Hibrid/Diesel");
		szautoUzemanyagErtekMap.put("gaz-benzin", "Gáz/Benzin");
		szautoUzemanyagErtekMap.put("gaz-diesel", "Gáz/Diesel");
		szautoUzemanyagErtekMap.put("elektromos", "Elektromos");
		szautoUzemanyag.setErtekMap(szautoUzemanyagErtekMap);
		
		LinkedList<Attributum> szautoList = new LinkedList<Attributum>();
		szautoList.add(szautoMarka);
		szautoList.add(szautoEvjarat);
		szautoList.add(szautoKm);
		szautoList.add(szautoUzemanyag);
		KATEGORIA_ATTRIBUTUM.put("szemelyauto", szautoList);
		
		
		Context.getCurrentLogger().info("AttributumCache has been loaded");
	}
	
	/**
	 * Visszaadja azokat az Attributumokat egy listaban, amik a kivalasztott kategoriakhoz tartoznak
	 * @param kategoriaList
	 * @return
	 */
	public static ArrayList<Attributum> getAttributumListByKategoriaList(List<Kategoria> kategoriaList) {
		ArrayList<Attributum> ret = new ArrayList<Attributum>();
		
		for(Kategoria kategoria : kategoriaList) {
			LinkedList<Attributum> attributumList = KATEGORIA_ATTRIBUTUM.get(kategoria.getUrlNev());
			if(attributumList != null) {
				for(Attributum attributum : attributumList) {
					ret.add(attributum);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Visszaad egy Attributum objektumot a kategoria es az attributum neve alapjan, ha letezik.
	 * @param kategoriaUrlNev
	 * @param attributumNev
	 * @return
	 */
	public static Attributum getAttributum(String kategoriaUrlNev, String attributumNev) {
		Attributum ret = null;
		
		LinkedList<Attributum> attributumList = KATEGORIA_ATTRIBUTUM.get(kategoriaUrlNev);
		if(attributumList != null) {
			for(Attributum attributum : attributumList) {
				if(attributumNev.equalsIgnoreCase(attributum.getNev())) {
					ret = attributum;
				}
			}
		}
		
		return ret;
	}
}
