package com.aprohirdetes.model;

import java.util.LinkedList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

@Entity("helyseg")
public class Helyseg {

	@Id private ObjectId id;
	
	private String nev;
	private String urlNev;
	private int sorrend = 1;
	
	private ObjectId szuloId;
	
	@NotSaved private List<Helyseg> alhelysegList = new LinkedList<Helyseg>();
	
	private int regiId;

	public Helyseg() {
		setRegiId(-1);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(urlNev);
		
		sb.append("{");
		for(Helyseg h : getAlhelysegList()) {
			sb.append(h.getUrlNev());
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
		return this.nev;
	}
	
	public String getUrlNev() {
		return this.urlNev;
	}
	
	public int getSorrend() {
		return this.sorrend;
	}
	
	public List<Helyseg> getAlhelysegList() {
		return this.alhelysegList;
	}
	
	public void setAlhelysegList(List<Helyseg> list) {
		this.alhelysegList = list;
	}

	public int getRegiId() {
		return regiId;
	}

	public void setRegiId(int regiId) {
		this.regiId = regiId;
	}
}
