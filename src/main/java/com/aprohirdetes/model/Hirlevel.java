package com.aprohirdetes.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("hirlevel")
public class Hirlevel {

	@Id private ObjectId id;
	private ObjectId hirdetesId;
	private int statusz;
	private Date kuldesKezdete;
	private Date kuldesVege;
	private long dbSikeres;
	private long dbHibas;
	private long dbOsszes;

	public Hirlevel() {
		setStatusz(Statusz.SZERKESZTES.value());
		setDbSikeres(0);
		setDbHibas(0);
		setDbOsszes(0);
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getHirdetesId() {
		return hirdetesId;
	}

	public void setHirdetesId(ObjectId hirdetesId) {
		this.hirdetesId = hirdetesId;
	}

	public int getStatusz() {
		return statusz;
	}

	public void setStatusz(int statusz) {
		this.statusz = statusz;
	}
	
	public void setStatusz(Statusz statusz) {
		this.statusz = statusz.value();
	}

	public Date getKuldesKezdete() {
		return kuldesKezdete;
	}

	public void setKuldesKezdete(Date kuldesKezdete) {
		this.kuldesKezdete = kuldesKezdete;
	}

	public Date getKuldesVege() {
		return kuldesVege;
	}

	public void setKuldesVege(Date kuldesVege) {
		this.kuldesVege = kuldesVege;
	}

	public long getDbSikeres() {
		return dbSikeres;
	}

	public void setDbSikeres(long dbSikeres) {
		this.dbSikeres = dbSikeres;
	}

	public long getDbHibas() {
		return dbHibas;
	}

	public void setDbHibas(long dbHibas) {
		this.dbHibas = dbHibas;
	}

	public long getDbOsszes() {
		return dbOsszes;
	}

	public void setDbOsszes(long dbOsszes) {
		this.dbOsszes = dbOsszes;
	}
	
	public enum Statusz {
		/**
		 * El van mentve a Hirlevel, de az alkalmazas nem csinal vele semmit a hatterben.
		 * Lehet modositani.
		 */
		SZERKESZTES(1),
		/**
		 * Ebben az allapotba kerul, ha elinditjuk a kikuldest.
		 * Beszurja a hirdetes_naplo tablaba a rekordokat, amik tartalmazzak a cimzettek adatait, 
		 * es a kikuldes allapotat.
		 * Ha ez kesz, akkor automatikusan atkerul KIKULDES statuszba.
		 */
		ELOKESZITES(2),
		/**
		 * Az alkalmazas a hatterben kuldi ki a leveleket es visszairja az allapotot a
		 * hirdetes_naploba.
		 * Ha minden rekordon vegigment, akkor atkerul KIKULDVE allapotba.
		 */
		KIKULDES(11),
		/**
		 * Megall a feldolgozas, mert minden rekord meg van jelolve a naploban valamilyen 
		 * statusszal.
		 */
		KIKULDVE(21),
		TOROLVE(31);
		
		private final int value;
		
		Statusz(int value) {
			this.value = value;
		}
		
		public int value() {
			return this.value;
		}
	}
}
