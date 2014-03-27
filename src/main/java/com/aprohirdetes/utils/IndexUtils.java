package com.aprohirdetes.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndexUtils {

	/**
	 * List of Hungarian stopwords
	 */
	public static ArrayList<String> STOPWORDS_HU = new ArrayList<String>(
			Arrays.asList("a", "az", "egy", "be", "ki", "le", "fel", "meg",
					"el", "át", "rá", "ide", "oda", "szét", "össze", "vissza",
					"de", "hát", "és", "vagy", "hogy", "van", "lesz", "volt",
					"csak", "nem", "igen", "mint", "én", "te", "õ", "mi", "ti",
					"õk", "ön"));

	/**
	 * Egy adott szoveg tokenekre bontasat vegzi. 
	 * Kiszedi azokat a magyar szavakat, amiket nem erdemes indexelni. Kiszedi a felesleges karaktereket. Kisbetusse alakit minden szot.
	 * 
	 * @param text Felbontando szoveg
	 * @return Szavak tombje
	 */
	public static List<String> tokenize(String text) {
		ArrayList<String> ret = new ArrayList<String>();

		if (text == null || text.isEmpty()) {
			return ret;
		}

		for (String s : text.split("\\s")) {
			String word = s.replaceAll("[\\.,:;'\"]", "").toLowerCase();

			if (!IndexUtils.STOPWORDS_HU.contains(word) && !ret.contains(word)) {
				ret.add(word);
			}
		}

		return ret;
	}
}
