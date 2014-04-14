package com.aprohirdetes.server.task;

import java.util.logging.Logger;

import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.utils.MongoUtils;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Megszamolja a hirdetesek szamat kategoriankent, es beirja a KategoriaCache-be.
 * Idozitve fut, a TaskService kezeli.
 * 
 * @author bvamos
 *
 */
public class KategoriaCountTask implements Runnable {

	private Logger logger;
	
	public KategoriaCountTask(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public void run() {
		this.logger.info("KategoriaCountTask start");
		
		DB db = MongoUtils.getMongoDB();
		DBCollection hirdetesCollection = db.getCollection("hirdetes");
		
		DBObject groupFields = new BasicDBObject( "_id", "$kategoriaId");
		groupFields.put("count", new BasicDBObject( "$sum", 1));
		DBObject group = new BasicDBObject("$group", groupFields);
		
		AggregationOutput output = hirdetesCollection.aggregate(group);
		
		for(DBObject rec : output.results()) {
			Kategoria kat = KategoriaCache.getCacheById().get(rec.get("_id"));
			kat.setHirdetesekSzama((Integer) rec.get("count"));
		}
		
		// Alkategoriak dbszamainak hozzadasa a fo kategoriahoz
		for(Kategoria kat : KategoriaCache.getCacheById().values()) {
			int count = kat.getHirdetesekSzama();
			for(Kategoria alKat : kat.getAlkategoriaList()) {
				count += KategoriaCache.getCacheById().get(alKat.getId()).getHirdetesekSzama();
			}
			kat.setHirdetesekSzama(count);
		}
		
		this.logger.info("KategoriaCountTask end");
	}

}
