package com.aprohirdetes.utils;

import hu.u_szeged.magyarlanc.resource.ResourceHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
	
	/**
	 * Egy adott szoveg tokenekre bontasat vegzi a magyarlanc 2.0 tokenizer segitsegevel
	 * Kiszedi azokat a magyar szavakat, amiket nem erdemes indexelni. Kiszedi a felesleges karaktereket. Kisbetusse alakit minden szot.
	 * @param text
	 * @return
	 */
	public static HashSet<String> tokenizeMagyarlanc(String text) {
		HashSet<String> ret = new HashSet<String>();

		if (text == null || text.isEmpty()) {
			return ret;
		}
		
		List<String[][]> morph = null;
	    morph = new ArrayList<String[][]>();

	    for (String[] sentence : ResourceHolder.getHunSplitter().splitToArray(text)) {
	      morph.add(ResourceHolder.getMaxentTagger().morpSentence(sentence));
	    }
	    
	    for(int i=0; i<morph.size(); i++) {
	    	String[][] a = morph.get(i);
	    	for(int j=0; j<a.length; j++) {
	    		String[] b = a[j];
	    		// Fonevek (N), melleknevek (A), igek (V) szotovei, Ismeretlen szavak (X)
	    		// Forras: Magyar Nemzeti Szövegtár - Az MSD-kódok rendszere
	    		// http://corpus.nytud.hu/mnsz/sugo_hun.html#msdrendszer
	    		if(b[2].startsWith("N") || b[2].startsWith("A") || b[2].startsWith("V") || b[2].startsWith("X")) {
	    			ret.add(b[1].toLowerCase());
	    		}
	    		//System.out.println(b[0] + ";" + b[1] + ";" + b[2]);
	    	}
	    }
		
		return ret;
	}
}
