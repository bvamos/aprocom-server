package com.aprohirdetes.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.engine.util.FormReader;

import com.aprohirdetes.utils.MailUtils;
import com.aprohirdetes.utils.MongoUtils;

public class KeresesHelper {

	private KeresesHelper() {
		
	}

	public static void setUtolsoKuldes(Kereses kereses) throws Exception {
		final Datastore datastore = MongoUtils.getDatastore();
		Query<Kereses> queryKereses = datastore.createQuery(Kereses.class);
		queryKereses.criteria("id").equal(kereses.getId());
		
		final UpdateOperations<Kereses> ops = datastore.createUpdateOperations(Kereses.class).set("utolsoKuldes", new Date());
		datastore.update(queryKereses, ops);
	}
	
	public static void sendMails() throws Exception {
		Context.getCurrentLogger().info("HirdetesFigyelo Start");
		
		final Datastore datastore = MongoUtils.getDatastore();
		Query<Kereses> queryKereses = datastore.createQuery(Kereses.class);
		queryKereses.criteria("statusz").equal(Kereses.Statusz.AKTIV.value());
		queryKereses.criteria("kuldesGyakorisaga").greaterThan(Kereses.KuldesGyakorisaga.SOHA.value());
		
		for(Kereses kereses : queryKereses) {
			Context.getCurrentLogger().info("Kereses: " + kereses.getNev());
			if(!isTimeToSendMail(kereses)) continue;
			
			ArrayList<Hirdetes> hirdetesList = getHirdetesList(kereses);
			Context.getCurrentLogger().info("Kereses '" + kereses.getNev() + "', hirdetesek szama: " + hirdetesList.size());
			if(hirdetesList.size()>0) {
				MailUtils.sendMailKereses(kereses, hirdetesList);
			}
			setUtolsoKuldes(kereses);
		}
		
		Context.getCurrentLogger().info("HirdetesFigyelo End");
	}
	
	public static ArrayList<Hirdetes> getHirdetesList(Kereses kereses) {
		ArrayList<Hirdetes> ret = new ArrayList<Hirdetes>();
		
		try {
			// /kereses/kinal/budapest/haz/
			// &ingatlan-allapot=ujepitesu&ingatlan-komfort=luxus&ingatlan-komfort=osszkomfort
			URL url = new URL("https://www.aprohirdetes.com" + kereses.getUrl());
			
			final String urlPath = url.getPath();
			String[] urlPathArray = urlPath.split("\\/");
			
			int hirdetesTipus = HirdetesTipus.getHirdetesTipus(urlPathArray[2]);
			List<ObjectId> selectedHelysegIdList = HelysegCache.getHelysegIdListByUrlNevList(urlPathArray[3]);
			List<ObjectId> selectedKategoriaIdList = KategoriaCache.getKategoriaIdListByUrlNevList(urlPathArray[4]);
			List<Kategoria> selectedKategoriaList = KategoriaCache.getKategoriaListByUrlNevList(urlPathArray[4]);
			
			final String urlQuery = url.getQuery();
			Form f = new Form();
			Integer arMin = null, arMax = null;
			String kulcsszo = null;
			try {
				f = new FormReader(urlQuery, '&').read();
				arMin = f.getFirstValue("ar_min")==null ? null : Integer.parseInt(f.getFirstValue("ar_min"));
				arMax = f.getFirstValue("ar_max")==null ? null : Integer.parseInt(f.getFirstValue("ar_max"));
				kulcsszo = f.getFirstValue("q")==null ? null : f.getFirstValue("q");
			} catch (Exception e) {
				e.printStackTrace();
				Context.getCurrentLogger().severe(e.getMessage());
			}
			
			final Datastore datastore = MongoUtils.getDatastore();
			Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
			if(kereses.getUtolsoKuldes()!=null) {
				query.criteria("modositva").greaterThan(kereses.getUtolsoKuldes());
			}
			query.criteria("statusz").equal(Hirdetes.Statusz.AKTIV.value());
			query.criteria("tipus").equal(hirdetesTipus);
			if(selectedHelysegIdList.size()>0) {
				query.criteria("helysegId").in(selectedHelysegIdList);
			}
			if(selectedKategoriaIdList.size()>0) {
				query.criteria("kategoriaId").in(selectedKategoriaIdList);
			}
			if(arMin != null) {
				query.criteria("ar").greaterThanOrEq(arMin);
			}
			if(arMax != null) {
				query.criteria("ar").lessThanOrEq(arMax);
			}
			if(kulcsszo != null) {
				query.criteria("kulcsszavak").equal(kulcsszo);
			}
			
			// Attributumok
			ArrayList<Attributum> attributumList = new ArrayList<Attributum>();
			ArrayList<Attributum> kategoriaAttributumList = AttributumCache.getAttributumListByKategoriaList(selectedKategoriaList);
			for(Parameter queryParameter : f) {
				String parameterNev = queryParameter.getName();
				String attributumNev = parameterNev;
				if(parameterNev.contains("_")) {
					attributumNev = parameterNev.substring(0, parameterNev.indexOf("_"));
				}
				//System.out.println(parameterNev + ", " + attributumNev);
				
				for(Attributum attributum : kategoriaAttributumList) {
					// Csak azok a parameterek vannak itt, amik a kivalasztott kategoriak attributumai
					if(attributum.getNev().equalsIgnoreCase(attributumNev) && !attributumList.contains(attributum)) {
						//System.out.println(attributum.getNev());
						// Belerakjuk a listaba, amiben a kivalasztott kategoriakhoz tartozo es URL-ben szereplo attributumok kerulnek
						attributumList.add(attributum);
					}
				}
			}
			
			for(Attributum attributum : attributumList) {
				String nev = attributum.getNev();
				// Szam tipusu attributum
				if(attributum.getTipus()==AttributumTipus.NUMBER) {
					if(f.getNames().contains(nev+"_min")) {
						Integer ertek = Integer.parseInt(f.getFirstValue(nev+"_min"));
						query.criteria("attributumok."+nev).greaterThanOrEq(ertek);
					}
					if(f.getNames().contains(nev+"_max")) {
						Integer ertek = Integer.parseInt(f.getFirstValue(nev+"_max"));
						query.criteria("attributumok."+nev).lessThanOrEq(ertek);
					}
				}
				
				// Lista
				if(attributum.getTipus()==AttributumTipus.SELECT_SINGLE || attributum.getTipus()==AttributumTipus.RADIO) {
					query.criteria("attributumok."+nev).in(Arrays.asList(f.getValuesArray(nev)));
				}
				
				// Yes/No
				if(attributum.getTipus()==AttributumTipus.YESNO) {
					String ertek = f.getFirstValue(nev);
					if("igen".equalsIgnoreCase(ertek)) {
						query.criteria("attributumok."+nev).equal(true);
					} else if("nem".equalsIgnoreCase(ertek)) {
						query.criteria("attributumok."+nev).equal(false);
					}
				}
			}
						
			query.limit(50);
			
			Context.getCurrentLogger().info("Kereses '" + kereses.getNev() + "', Query: " + query.toString());
			
			for(Hirdetes hirdetes : query) {
				ret.add(hirdetes);
			}
			
		} catch (MalformedURLException e) {
			Context.getCurrentLogger().throwing("KeresesHelper", "getHirdetesList", e);
		}
		
		return ret;
	}
	
	private static boolean isTimeToSendMail(Kereses kereses) {
		boolean ret = false;
		
		if(kereses.getUtolsoKuldes()==null) return true;
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		Date datum = null;
		
		switch(kereses.getKuldesGyakorisaga()) {
		case 21:
			// Naponta
			c.add(Calendar.DATE, -1);
			datum = c.getTime();
			break;
		case 22:
			// Hetente
			c.add(Calendar.DATE, -7);
			datum = c.getTime();
			break;
		case 23:
			// Havonta
			c.add(Calendar.MONTH, -1);
			datum = c.getTime();
			break;
		}
		
		if(datum!=null && kereses.getUtolsoKuldes().before(datum)) {
			ret = true;
		}
		
		Context.getCurrentLogger().info("Kereses '" + kereses.getNev() +"', Utolso kuldes: " + kereses.getUtolsoKuldes().toString() + ", Gyakorisag: " + Kereses.KuldesGyakorisaga.valueOf(kereses.getKuldesGyakorisaga()) + ", Fut: " + ret);
		return ret;
	}
}
