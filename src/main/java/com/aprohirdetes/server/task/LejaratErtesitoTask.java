package com.aprohirdetes.server.task;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesHelper;
import com.aprohirdetes.utils.MailUtils;
import com.aprohirdetes.utils.MongoUtils;

public class LejaratErtesitoTask implements Runnable {

	private Logger logger;
	
	public LejaratErtesitoTask(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public void run() {
		this.logger.info("LejaratErtesitoTask start");
		
		Date otNapMulva = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(otNapMulva); 
		c.add(Calendar.DATE, 5);
		otNapMulva = c.getTime();
		this.logger.info(otNapMulva.toString());
		
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		try {
			Datastore datastore = MongoUtils.getDatastore();
			Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
			query.or(
				query.criteria("lejar").doesNotExist(),
				query.criteria("lejar").lessThanOrEq(otNapMulva)
			);
			query.criteria("lejarErtesites").notEqual(c.getTime());
			
			// Egy lepesben max 100 levelet kuldjunk ki. Kesobb majd novelhetjuk, ha ez nem eleg, a task orankent fut.
			query.limit(100);
			
			for(Hirdetes h : query) {
				String s = "";
				s = h.getCim();
				s +=", Datum: " + c.getTime().toString();
				if(h.getLejarErtesites()!=null) s += ", Utolso ertesites: " + h.getLejarErtesites().toString();
				if(h.getLejar()!=null) s += ", Lejar: " + h.getLejar().toString();
				this.logger.info(s);
				
				if(MailUtils.sendMailHirdetesLejar(h)) {
					// Utolso ertesites beallitasa
					HirdetesHelper.saveLejarErtesites(h.getId(), c.getTime());
				} else {
					this.logger.severe("Hiba az ertesito level kikuldese kozben");
				}
			}
			
		} catch(Exception e) {
			this.logger.severe("ERROR: " + e.getMessage());
		}
		
		this.logger.info("LejaratErtesitoTask end");
	}

}
