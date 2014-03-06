package com.aprohirdetes.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("hirdeto")
public class Hirdeto {

	@Id private ObjectId id;
	
	private String nev;
	private String email;
	private String iranyitoSzam;
	private String telepules;
	private String cim;
	private String orszag;
	private String telefon;
	
	public Hirdeto() {
		
	}
	
	public ObjectId getId() {
		return id;
	}
	
	public String getIdAsString() {
		return id.toString();
	}
	
	public String getNev() {
		return nev;
	}
	
	public void setNev(String nev) {
		this.nev = nev;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIranyitoSzam() {
		return iranyitoSzam;
	}

	public void setIranyitoSzam(String iranyitoSzam) {
		this.iranyitoSzam = iranyitoSzam;
	}

	public String getTelepules() {
		return telepules;
	}

	public void setTelepules(String telepules) {
		this.telepules = telepules;
	}

	public String getCim() {
		return cim;
	}

	public void setCim(String cim) {
		this.cim = cim;
	}

	public String getOrszag() {
		return orszag;
	}

	public void setOrszag(String orszag) {
		this.orszag = orszag;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	
}
