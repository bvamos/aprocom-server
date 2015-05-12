package com.aprohirdetes.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

import com.aprohirdetes.model.Esemeny.EsemenySzint;
import com.aprohirdetes.model.Esemeny.EsemenyTipus;
import com.aprohirdetes.utils.MongoUtils;

public class EsemenyHelper {
	
	public static void add(EsemenyTipus tipus, EsemenySzint szint, String uzenet) {
		Esemeny esemeny = new Esemeny(tipus, szint, uzenet);
		Datastore datastore = MongoUtils.getDatastore();
		datastore.save(esemeny);
	}
	
	public static void add(Esemeny esemeny) {
		Datastore datastore = MongoUtils.getDatastore();
		datastore.save(esemeny);
	}
	
	public static void addHirdetesFeladasInfo(ObjectId hirdetesId, String uzenet) {
		Esemeny esemeny = new Esemeny(EsemenyTipus.HIRDETES_FELADAS, EsemenySzint.INFO, uzenet);
		esemeny.setHirdetesId(hirdetesId);
		add(esemeny);
	}
	
	public static void addHirdetesFeladasError(String uzenet) {
		add(EsemenyTipus.HIRDETES_FELADAS, EsemenySzint.ERROR, uzenet);
	}
}
