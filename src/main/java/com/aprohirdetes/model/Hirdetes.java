package com.aprohirdetes.model;

import java.util.HashMap;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("hirdetes")
public class Hirdetes {
	
	@Id private ObjectId id;
	
	private int tipus = 1;
	private String cim;
	private String szoveg;
	private int ar;
	
	private ObjectId helysegId;
	private ObjectId kategoriaId;
	private ObjectId hirdetoId;
	
	@Embedded private Hirdeto hirdeto;

	private HashMap<String, String> egyebMezok = new HashMap<String, String>();
	
	public Hirdetes() {
		
	}
	
	public int getTipus() {
		return tipus;
	}

	public void setTipus(int tipus) {
		this.tipus = tipus;
	}

	public String getCim() {
		return cim;
	}

	public void setCim(String cim) {
		this.cim = cim;
	}

	public String getSzoveg() {
		return szoveg;
	}

	public void setSzoveg(String szoveg) {
		this.szoveg = szoveg;
	}

	public int getAr() {
		return ar;
	}

	public void setAr(int ar) {
		this.ar = ar;
	}

	public ObjectId getHelysegId() {
		return helysegId;
	}

	public void setHelysegId(ObjectId helysegId) {
		this.helysegId = helysegId;
	}

	public ObjectId getKategoriaId() {
		return kategoriaId;
	}

	public void setKategoriaId(ObjectId kategoriaId) {
		this.kategoriaId = kategoriaId;
	}

	public ObjectId getHirdetoId() {
		return hirdetoId;
	}

	public void setHirdetoId(ObjectId hirdetoId) {
		this.hirdetoId = hirdetoId;
	}

	public HashMap<String, String> getEgyebMezok() {
		return egyebMezok;
	}

	public void setEgyebMezok(HashMap<String, String> egyebMezok) {
		this.egyebMezok = egyebMezok;
	}

	public long getFeladasDatuma() {
		return id.getTime();
	}
}
