package com.aprohirdetes.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

@Entity("hirdeto")
public class Hirdeto {

	@Id private ObjectId id;
	
	private String nev;
	@Indexed(unique=true) private String email;
	private String iranyitoSzam;
	private String telepules;
	private String cim;
	private String orszag;
	private String telefon;
	private boolean hirlevel;
	
	private String jelszo;
	private boolean hitelesitve;
	private Date utolsoBelepes;
	
	private int regiId;
	
	public Hirdeto() {
		setHirlevel(true);
		setHitelesitve(true);
		setRegiId(-1);
	}
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
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

	public boolean isHirlevel() {
		return hirlevel;
	}

	public void setHirlevel(boolean hirlevel) {
		this.hirlevel = hirlevel;
	}

	public String getJelszo() {
		return jelszo;
	}

	public void setJelszo(String jelszo) {
		this.jelszo = jelszo;
	}

	public boolean isHitelesitve() {
		return hitelesitve;
	}

	public void setHitelesitve(boolean hitelesitve) {
		this.hitelesitve = hitelesitve;
	}

	public Date getUtolsoBelepes() {
		return utolsoBelepes;
	}

	public void setUtolsoBelepes(Date utolsoBelepes) {
		this.utolsoBelepes = utolsoBelepes;
	}

	public int getRegiId() {
		return regiId;
	}

	public void setRegiId(int regiId) {
		this.regiId = regiId;
	}
	
}
