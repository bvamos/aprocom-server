package com.aprohirdetes.model;

import java.util.LinkedList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

@Entity("kategoria")
public class Kategoria {

	@Id private ObjectId id;
	
	private String nev;
	private String urlNev;
	private int sorrend = 1;
	
	private ObjectId szuloId;
	
	@NotSaved private List<Kategoria> alkategoriaList = new LinkedList<Kategoria>();
	
	private int hirdetesekSzama = 0;
	
	private int regiId;
	
	public Kategoria() {
		setRegiId(-1);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(urlNev);
		
		sb.append("{");
		for(Kategoria ak : getAlkategoriaList()) {
			sb.append(ak.getUrlNev());
		}
		sb.append("} ");
		
		return sb.toString();
	}
	
	public ObjectId getId() {
		return id;
	}
	
	public String getIdAsString() {
		return id.toString();
	}
	
	public ObjectId getSzuloId() {
		return this.szuloId;
	}

	public String getNev() {
		return nev;
	}

	public void setNev(String nev) {
		this.nev = nev;
	}

	public String getUrlNev() {
		return urlNev;
	}

	public void setUrlNev(String urlNev) {
		this.urlNev = urlNev;
	}

	public int getSorrend() {
		return sorrend;
	}

	public void setSorrend(int sorrend) {
		this.sorrend = sorrend;
	}
	
	public List<Kategoria> getAlkategoriaList() {
		return alkategoriaList;
	}
	
	public void setAlkategoriaList(List<Kategoria> alkategoriaList) {
		this.alkategoriaList = alkategoriaList;
	}

	public int getHirdetesekSzama() {
		return hirdetesekSzama;
	}

	public void setHirdetesekSzama(int hirdetesekSzama) {
		this.hirdetesekSzama = hirdetesekSzama;
	}

	public int getRegiId() {
		return regiId;
	}

	public void setRegiId(int regiId) {
		this.regiId = regiId;
	}
}
