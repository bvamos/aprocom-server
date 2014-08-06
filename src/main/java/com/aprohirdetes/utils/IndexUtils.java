package com.aprohirdetes.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IndexUtils {

	/**
	 * List of Hungarian stopwords
	 */
	public static ArrayList<String> STOPWORDS_HU = new ArrayList<String>(
			Arrays.asList("a", "aha", "aki", "ami", "arra", "az", "azt", "át", 
					"be", 
					"csak", 
					"de", 
					"egy", "el", "erre", "ez", "ezt", "én", "és", 
					"fel", 
					"ha", "hát", "hogy", 
					"ide", "igen", "is", "itt", 
					"ki", 
					"le", "lesz",
					"más", "még", "meg", "mi", "mint", 
					"nem", 
					"oda", "össze", "ő", "ők", "ön",   
					"pl", 
					"rá", 
					"stb", "szét", 
					"te", "ti",
					"vagy", "van", "vissza", "volt"
				)
			);

	/**
	 * Egy adott szoveg tokenekre bontasat vegzi. 
	 * Kiszedi azokat a magyar szavakat, amiket nem erdemes indexelni. Kiszedi a felesleges karaktereket. Kisbetusse alakit minden szot.
	 * 
	 * @param text Felbontando szoveg
	 * @return Szavak tombje
	 */
	public static Set<String> tokenize(String text) {
		Set<String> ret = new HashSet<String>();

		if (text == null || text.isEmpty()) {
			return ret;
		}

		for (String s : text.split("[ .,;:?!]+")) {
			String word = s.replaceAll("['\"]", "").toLowerCase();

			// Ures szavak kiszurese
			if(word.isEmpty()) {
				continue;
			}
			
			// Stopword vagy letezo szavak atugrasa
			if (IndexUtils.STOPWORDS_HU.contains(word) || ret.contains(word)) {
				continue;
			}
			
			// Szamokat tartalmazo szavak atugrasa
			if(word.matches(".*\\d.*")){
				continue;
			}
			
			// Hozzadas a kulcsszavak listajahoz
			ret.add(word);
		}

		return ret;
	}
}
