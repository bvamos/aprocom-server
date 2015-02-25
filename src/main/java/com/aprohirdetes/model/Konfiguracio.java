package com.aprohirdetes.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

@Entity("konfiguracio")
public class Konfiguracio {

	@Id private ObjectId id;
	
	@Indexed(unique=true) private String kulcs;
	private String ertek;
	private Date modositva;
	
	public Konfiguracio(String kulcs, String ertek){
		this.setKulcs(kulcs);
		this.setErtek(ertek);
		this.setModositva(new Date());
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getKulcs() {
		return kulcs;
	}

	public void setKulcs(String kulcs) {
		this.kulcs = kulcs;
	}

	public String getErtek() {
		return ertek;
	}

	public void setErtek(String ertek) {
		this.ertek = ertek;
	}

	public Date getModositva() {
		return modositva;
	}

	public void setModositva(Date modositva) {
		this.modositva = modositva;
	}
	
}
