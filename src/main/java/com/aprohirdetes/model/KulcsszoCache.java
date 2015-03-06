package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.aprohirdetes.utils.MongoUtils;

public class KulcsszoCache {

	private static Map<String, Integer> CACHE_BY_KULCSSZO = new LinkedHashMap<String, Integer>();

	/**
	 * @return the cACHE_BY_KULCSSZO
	 */
	public static Map<String, Integer> getCacheByKulcsszo() {
		return CACHE_BY_KULCSSZO;
	}

	public static void loadCache() {
		// Kiuritjuk a cache-t
		System.out.println("KulcsszoCache torles");
		CACHE_BY_KULCSSZO.clear();
		System.out.println("KulcsszoCache torolve");
		
		// Vegig megyunk a Hirdeteseken, kiszedjuk a kulcsszavakat
		System.out.println("KulcsszoCache feltoltes");
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class).retrievedFields(true, "kulcsszavak");
		query.criteria("hitelesitve").equal(true);
		query.criteria("torolve").equal(false);
		
		Iterator<Hirdetes> ih = query.iterator();
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		while(ih.hasNext()) {
			Hirdetes h = ih.next();
			for(String kulcsszo : h.getKulcsszavak()) {
				// Ha a kulcszo mar a cache-ben van, noveljuk a darabszamot, ha nincs, berakunk 1-et
				int n = 1;
				if(map.containsKey(kulcsszo)) {
					n = map.get(kulcsszo)+1;
					map.remove(kulcsszo);
				}
				map.put(kulcsszo, n);
			}
		}
		
		// Sorba rendezve berakjuk a cache-be
		List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
				return b.getValue().compareTo(a.getValue());
			}
		});
		for(Map.Entry<String, Integer> entry : entries) {
			CACHE_BY_KULCSSZO.put(entry.getKey(), entry.getValue());
		}
		
		System.out.println("KulcsszoCache feltoltve");
		for(String kulcsszo : CACHE_BY_KULCSSZO.keySet()) {
			System.out.println(kulcsszo + ": " + CACHE_BY_KULCSSZO.get(kulcsszo));
		}
	}
}
