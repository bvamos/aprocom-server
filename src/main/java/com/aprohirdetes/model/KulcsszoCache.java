package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.restlet.Context;

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
	public static JSONArray getCacheByPrefix(String prefix, int limit) {
		JSONArray ret = new JSONArray();
		
		int n = 0;
		for(String kulcsszo : CACHE_BY_KULCSSZO.keySet()) {
			if(n==limit) break;
			JSONObject map = new JSONObject();
			if(prefix==null || prefix.isEmpty() || kulcsszo.startsWith(prefix)) {
				try {
					map.put("name", kulcsszo);
					map.put("count", CACHE_BY_KULCSSZO.get(kulcsszo));
					
					ret.put(map);
				} catch (JSONException je) {
					Context.getCurrentLogger().severe(je.getMessage());
				}
				n++;
			}
		}
		
		return ret;
	}
}
