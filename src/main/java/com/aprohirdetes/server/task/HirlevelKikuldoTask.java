package com.aprohirdetes.server.task;

import java.util.Date;
import java.util.logging.Logger;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesHelper;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.Hirlevel;
import com.aprohirdetes.model.HirlevelHirdeto;
import com.aprohirdetes.utils.MailUtils;
import com.aprohirdetes.utils.MongoUtils;

public class HirlevelKikuldoTask implements Runnable {

	private Logger logger;
	
	public HirlevelKikuldoTask(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public void run() {
		this.logger.info("HirlevelKikuldoTask start");
		Thread.currentThread().setName("Apro-HirlevelKikuldoTask");
		
		final Datastore datastore = MongoUtils.getDatastore();
		
		try {
		// Elokeszites
		Query<Hirlevel> queryHirlevelELokeszites = datastore.createQuery(Hirlevel.class);
		queryHirlevelELokeszites.criteria("statusz").equal(Hirlevel.Statusz.ELOKESZITES.value());
		
		for(Hirlevel h : queryHirlevelELokeszites) {
			this.logger.info("Hirlevel Elokeszites: " + h.getId().toString());
			Query<Hirdeto> queryHirdeto = datastore.createQuery(Hirdeto.class);
			queryHirdeto.criteria("hitelesitve").equal(true);
			
			for(Hirdeto hirdeto : queryHirdeto) {
				HirlevelHirdeto hh = new HirlevelHirdeto(h.getId(), hirdeto.getId());
				hh.setEmail(hirdeto.getEmail());
				datastore.save(hh);
			}
			
			long cnt = queryHirdeto.countAll();
			this.logger.info("Hirlevel Elokeszites: " + h.getId().toString() + ". Cimzettek szama: " + cnt);
			
			Query<Hirlevel> queryHirlevel = datastore.createQuery(Hirlevel.class);
			queryHirlevel.criteria("id").equal(h.getId());
			UpdateOperations<Hirlevel> ops = datastore.createUpdateOperations(Hirlevel.class)
					.set("statusz", Hirlevel.Statusz.KIKULDES.value())
					.set("dbSikeres", 0)
					.set("dbHibas", 0)
					.set("dbOsszes", cnt);
			datastore.update(queryHirlevel, ops);
			
			this.logger.info("Hirlevel elokeszitve: " + h.getId().toString());
		}
		
		// Kikuldes
		Query<Hirlevel> queryHirleveKikuldes = datastore.createQuery(Hirlevel.class);
		queryHirleveKikuldes.criteria("statusz").equal(Hirlevel.Statusz.KIKULDES.value());
		
		for(Hirlevel h : queryHirleveKikuldes) {
			this.logger.info("Hirlevel Kikuldes: " + h.getId().toString());
			
			Hirdetes hirdetes = HirdetesHelper.load(h.getHirdetesId());
			
			Query<HirlevelHirdeto> queryHirlevelHirdeto = datastore.createQuery(HirlevelHirdeto.class);
			queryHirlevelHirdeto.criteria("hirlevelId").equal(h.getId());
			queryHirlevelHirdeto.criteria("statusz").equal(0);
			
			int dbSikeres = 0;
			int dbHibas = 0;
			for(HirlevelHirdeto hh : queryHirlevelHirdeto) {
				boolean statusz = MailUtils.sendMailHirlevel(hirdetes, hh.getEmail());
				
				if(statusz) {
					hh.setStatusz(1);
					dbSikeres++;
				} else {
					hh.setStatusz(0);
					dbHibas++;
				}
				hh.setKuldesDatum(new Date());
				datastore.save(hh);

				//Query<HirlevelHirdeto> queryUpdate = datastore.createQuery(HirlevelHirdeto.class);
				//queryUpdate.criteria("id").equal(hh.getId());
				//UpdateOperations<HirlevelHirdeto> ops = datastore.createUpdateOperations(HirlevelHirdeto.class).set("statusz", statusz ? 1 : 0);
				//datastore.update(queryUpdate, ops);
				
				this.logger.info("HirlevelKikuldes " + hh.getEmail() + ": " + statusz);
			}
			
			Query<Hirlevel> queryHirlevel = datastore.createQuery(Hirlevel.class);
			queryHirlevel.criteria("id").equal(h.getId());
			UpdateOperations<Hirlevel> ops = datastore.createUpdateOperations(Hirlevel.class)
					.set("statusz", Hirlevel.Statusz.KIKULDVE.value())
					.set("dbHibas", dbHibas)
					.set("dbSikeres", dbSikeres);
			datastore.update(queryHirlevel, ops);
			
			this.logger.info("Hirlevel Kikuldve: " + h.getId().toString() + ". Sikeres=" + dbSikeres + ", Hibas=" + dbHibas);
		}
			
		} catch(Exception e) {
			this.logger.severe(e.getMessage());
		}
		this.logger.info("HirlevelKikuldoTask end");
	}

}
