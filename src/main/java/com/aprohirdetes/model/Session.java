package com.aprohirdetes.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("session")
public class Session {

	@Id private ObjectId id;
	
	private String sessionId;
	private String felhasznaloNev;
	private ObjectId hirdetoId;
	private long kezdet;
	private long lejarat;
	private long utolsoKeres;
	
	public Session() {
		long time = new Date().getTime();
		this.kezdet = time;
		this.utolsoKeres = time;
		this.lejarat = time + (3600*24*7);
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getFelhasznaloNev() {
		return felhasznaloNev;
	}
	
	public void setFelhasznaloNev(String felhasznaloNev) {
		this.felhasznaloNev = felhasznaloNev;
	}
	
	public ObjectId getHirdetoId() {
		return hirdetoId;
	}

	public void setHirdetoId(ObjectId hirdetoId) {
		this.hirdetoId = hirdetoId;
	}

	public long getLejarat() {
		return lejarat;
	}
	
	public void setLejarat(long lejarat) {
		this.lejarat = lejarat;
	}
	
	public long getUtolsoKeres() {
		return utolsoKeres;
	}
	
	public void setUtolsoKeres(long utolsoKeres) {
		this.utolsoKeres = utolsoKeres;
	}
	
	public ObjectId getId() {
		return id;
	}
	
	public long getKezdet() {
		return kezdet;
	}
	
}