package com.aprohirdetes.server.task;

import java.util.logging.Logger;

import org.bson.types.ObjectId;

import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.model.KulcsszoCache;
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
		Thread.currentThread().setName("Apro-KategoriaCountTask");
		
		DB db = MongoUtils.getMongoDB();
		DBCollection hirdetesCollection = db.getCollection("hirdetes");
		
		DBObject matchFields = new BasicDBObject("statusz", Hirdetes.Statusz.JOVAHAGYVA.value());
		//matchFields.put("hitelesitve", true);
		DBObject match = new BasicDBObject("$match", matchFields);
		
		DBObject groupFields = new BasicDBObject( "_id", "$kategoriaId");
		groupFields.put("count", new BasicDBObject( "$sum", 1));
		DBObject group = new BasicDBObject("$group", groupFields);
		
		AggregationOutput output = hirdetesCollection.aggregate(match, group);
		
		// Darabszamok nullazasa
		for(Kategoria kat : KategoriaCache.getCacheById().values()) {
			kat.setHirdetesekSzama(0);
			for(Kategoria alKat : kat.getAlkategoriaList()) {
				alKat.setHirdetesekSzama(0);
			}
		}
		
		// Osszegyujtott darabszamok beirasa
		for(DBObject rec : output.results()) {
			//System.out.println(rec.toString());
			for(Kategoria kat : KategoriaCache.getCacheById().values()) {
				if(((ObjectId)rec.get("_id")).equals(kat.getId())) {
					// Fokategoria hirdeteseinek darabszama
					kat.setHirdetesekSzama((Integer) rec.get("count"));
				}
				for(Kategoria alKat : kat.getAlkategoriaList()) {
					// Alkategoria hirdeteseinek darabszama
					if(((ObjectId)rec.get("_id")).equals(alKat.getId())) {
						alKat.setHirdetesekSzama((Integer) rec.get("count"));
					}
				}
			}
		}
		
		// Alkategoriak dbszamainak hozzaadasa a fo kategoriahoz
		for(Kategoria kat : KategoriaCache.getCacheById().values()) {
			int count = kat.getHirdetesekSzama();
			for(Kategoria alKat : kat.getAlkategoriaList()) {
				count += alKat.getHirdetesekSzama();
			}
			kat.setHirdetesekSzama(count);
			//System.out.println(kat.toString());
		}
		
		this.logger.info("KulcsszoCache feltoltese");
		KulcsszoCache.loadCache();
		this.logger.info("KulcsszoCache feltoltve");
		
		this.logger.info("KategoriaCountTask end");
	}

}
