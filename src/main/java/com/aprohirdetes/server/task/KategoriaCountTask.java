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
 * Az alkategoriak darabszamainak osszeget meg hozzadja a szulo kategoria darabszamahoz.
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
		
		DBObject matchFields = new BasicDBObject("torolve", false);
		matchFields.put("hitelesitve", true);
		DBObject match = new BasicDBObject("$match", matchFields);
		
		DBObject groupFields = new BasicDBObject( "_id", "$kategoriaId");
		groupFields.put("count", new BasicDBObject( "$sum", 1));
		DBObject group = new BasicDBObject("$group", groupFields);
		
		AggregationOutput output = hirdetesCollection.aggregate(match, group);
		
		// Fokategoriak nullazasa
		for(Kategoria kat : KategoriaCache.getCacheById().values()) {
			kat.setHirdetesekSzama(0);
		}
		
		// Osszegyujtott darabszamok beirasa
		for(DBObject rec : output.results()) {
			//Kategoria kat = KategoriaCache.getCacheById().get(rec.get("_id"));
			KategoriaCache.getCacheById().get(rec.get("_id")).setHirdetesekSzama((Integer) rec.get("count"));
		}
		
		// Alkategoriak dbszamainak hozzadasa a fo kategoriahoz
		for(Kategoria kat : KategoriaCache.getCacheById().values()) {
			int count = kat.getHirdetesekSzama();
			for(Kategoria alKat : kat.getAlkategoriaList()) {
				count += alKat.getHirdetesekSzama();
			}
			kat.setHirdetesekSzama(count);
		}
		
		this.logger.info("KategoriaCountTask end");
	}

}
