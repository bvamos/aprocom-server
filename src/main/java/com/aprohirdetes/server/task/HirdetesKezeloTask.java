package com.aprohirdetes.server.task;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesHelper;
import com.aprohirdetes.model.KeresesHelper;
import com.aprohirdetes.utils.MailUtils;
import com.aprohirdetes.utils.MongoUtils;

public class HirdetesKezeloTask implements Runnable {

	private Logger logger;
	
	public HirdetesKezeloTask(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public void run() {
		this.logger.info("HirdetesKezeloTask start");
		Thread.currentThread().setName("Apro-HirdetesKezeloTask");
		
		Calendar c = Calendar.getInstance();
		// Datum 5 nap mulva. Az 5 napon belul lejaro hirdeteseket listazzuk vele.
		c.setTime(new Date()); 
		c.add(Calendar.DATE, 5);
		Date otNapMulva = c.getTime();
		
		// Ma 00:00:00 ora. Ezt allitjuk be, mint utolso ertesites datuma, es ellenorizzuk, hogy ne kuldjunk ki egy nap tobb ertesitest.
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		try {
			// 5 napon belul lejaro hirdetesek, amiknel ma meg nem kuldtunk ki ertesitest
			Datastore datastore = MongoUtils.getDatastore();
			Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
			query.criteria("lejar").lessThanOrEq(otNapMulva);
			query.criteria("lejarErtesites").notEqual(c.getTime());
			query.criteria("statusz").equal(Hirdetes.Statusz.AKTIV.value());
			
			// Egy lepesben max 100 levelet kuldjunk ki. Kesobb majd novelhetjuk, ha ez nem eleg, a task orankent fut.
			query.limit(100);
			
			for(Hirdetes h : query) {
				String s = "Hirdetes lejar: " + h.getId().toString();
				if(h.getLejarErtesites()!=null) s += ", Utolso ertesites: " + h.getLejarErtesites().toString();
				if(h.getLejar()!=null) s += ", Lejar: " + h.getLejar().toString();
				this.logger.info(s);
				
				if(h.getLejaratDatuma()<new Date().getTime()) {
					// Lejart hirdetes, toroljuk
					HirdetesHelper.delete(h.getId(), Hirdetes.Statusz.INAKTIV_LEJART);
					this.logger.info("Hirdetes torolve: " + h.getId().toString());
				} else {
					// 5 napon belul lejar, ertesitest kuldunk
					if(MailUtils.sendMailHirdetesLejar(h)) {
						// Utolso ertesites beallitasa
						HirdetesHelper.saveLejarErtesites(h.getId(), c.getTime());
					} else {
						this.logger.severe("Hiba az ertesito level kikuldese kozben");
					}
				}
			}
			
		} catch(Exception e) {
			this.logger.severe("ERROR: " + e.getMessage());
		}
		
		
		// 2 hete inaktiv hirdetesek torlese
		Calendar c2hete = Calendar.getInstance();
		// Datum: 2 hete
		c2hete.setTime(new Date()); 
		c2hete.add(Calendar.DATE, -14);
		Date datumKetHete = c2hete.getTime();
		
		Datastore datastore = MongoUtils.getDatastore();
		Query<Hirdetes> queryInaktiv = datastore.createQuery(Hirdetes.class);
		queryInaktiv.criteria("statusz").in(Arrays.asList(Hirdetes.Statusz.INAKTIV_ELADVA.value(), Hirdetes.Statusz.INAKTIV_LEJART.value()));
		queryInaktiv.criteria("torolveDatum").lessThan(datumKetHete);
		
		UpdateOperations<Hirdetes> updateOpsInaktiv = datastore.createUpdateOperations(Hirdetes.class)
				.set("statusz", Hirdetes.Statusz.TOROLVE.value())
				.set("torolveDatum", new Date());
		datastore.update(queryInaktiv, updateOpsInaktiv);
		
		
		// HirdetesFigyelo
		KeresesHelper.sendMails();
		
		this.logger.info("HirdetesKezeloTask end");
	}

}
