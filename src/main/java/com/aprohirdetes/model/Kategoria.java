package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Entity("kategoria")
public class Kategoria {

	@Id private ObjectId id;
	
	private String nev;
	private String urlNev;
	private int sorrend = 1;
	
	private ObjectId szuloId;
	
	@Reference(idOnly=true) private List<Kategoria> alkategoriak = new ArrayList<Kategoria>();
	
	public Kategoria() {
		
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
}
