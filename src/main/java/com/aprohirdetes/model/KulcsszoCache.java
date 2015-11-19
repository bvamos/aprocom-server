package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

	public static void clearCache() {
		CACHE_BY_KULCSSZO.clear();
	}
	
	public static void loadCache() {
		clearCache();
		
		// Vegig megyunk a Hirdeteseken, kiszedjuk a kulcsszavakat
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> query = datastore.createQuery(Hirdetes.class).retrievedFields(true, "kulcsszavak");
		query.criteria("statusz").equal(Hirdetes.Statusz.JOVAHAGYVA.value());
		
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
		
		/*for(String kulcsszo : CACHE_BY_KULCSSZO.keySet()) {
			System.out.println(kulcsszo + ": " + CACHE_BY_KULCSSZO.get(kulcsszo));
		}*/
	}
	
	/**
	 * Visszadja a prefix-szel kezdodo kulcsszavak listajat a cache-bol
	 * @param prefix Csak a prefix-szel kezdodo kulcsszavakat adjuk vissza. Lehet üres!
	 * @param limit Max ennyi eredmenyt adunk vissza
	 * @return
	 */
	public static LinkedList<HashMap<String, Object>> getCacheByPrefix(String prefix, int limit) {
		LinkedList<HashMap<String, Object>> ret = new LinkedList<HashMap<String, Object>>();
		
		int n = 0;
		for(String kulcsszo : CACHE_BY_KULCSSZO.keySet()) {
			if(n==limit) break;
			HashMap<String, Object> map = new HashMap<String, Object>();
			if(prefix==null || prefix.isEmpty() || kulcsszo.startsWith(prefix)) {
				map.put("name", kulcsszo);
				map.put("cnt", CACHE_BY_KULCSSZO.get(kulcsszo));
				ret.add(map);
				n++;
			}
		}
		
		return ret;
	}
}
