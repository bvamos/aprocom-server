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
	
	public Kategoria() {
	}
	
	public Kategoria(String id, String nev, String urlNev, int sorrend, String szuloId) {
		setId(id);
		setNev(nev);
		setUrlNev(urlNev);
		setSorrend(sorrend);
		setSzuloId(szuloId);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(urlNev);
		sb.append("(" + getHirdetesekSzama() + ")");
		
		if(getAlkategoriaList()!=null) {
			sb.append(" {");
			for(Kategoria ak : getAlkategoriaList()) {
				sb.append(ak.toString() + ", ");
			}
			sb.append("} ");
		}
		
		return sb.toString();
	}
	
	public ObjectId getId() {
		return id;
	}
	
	public String getIdAsString() {
		return id.toString();
	}
	
	private void setId(String id) {
		this.id = new ObjectId(id);
	}
	
	public ObjectId getSzuloId() {
		return this.szuloId;
	}
	
	private void setSzuloId(String szuloId) {
		if(szuloId != null) {
			this.szuloId = new ObjectId(szuloId);
		}
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

	public synchronized int getHirdetesekSzama() {
		return hirdetesekSzama;
	}

	public synchronized void setHirdetesekSzama(int hirdetesekSzama) {
		this.hirdetesekSzama = hirdetesekSzama;
	}

}
