package com.aprohirdetes.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("uzenet")
public class Uzenet {

	@Id private ObjectId id;
	
	private ObjectId elozmenyId;
	private String targy;
	private String szoveg;
	
	private ObjectId feladoId;
	private String feladoEmail;
	private ObjectId cimzettId;
	private String cimzettEmail;
	
	private Date kuldesDatum;
	private boolean kezbesitve;
	private boolean elolvasva;
	private boolean torolve;
	
	public Uzenet() {
		setKezbesitve(false);
		setElolvasva(false);
		setTorolve(false);
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public ObjectId getElozmenyId() {
		return elozmenyId;
	}
	public void setElozmenyId(ObjectId elozmenyId) {
		this.elozmenyId = elozmenyId;
	}
	public String getTargy() {
		return targy;
	}
	public void setTargy(String targy) {
		this.targy = targy;
	}
	public String getSzoveg() {
		return szoveg;
	}
	public void setSzoveg(String szoveg) {
		this.szoveg = szoveg;
	}
	public ObjectId getFeladoId() {
		return feladoId;
	}
	public void setFeladoId(ObjectId feladoId) {
		this.feladoId = feladoId;
	}
	public String getFeladoEmail() {
		return feladoEmail;
	}
	public void setFeladoEmail(String feladoEmail) {
		this.feladoEmail = feladoEmail;
	}
	public ObjectId getCimzettId() {
		return cimzettId;
	}
	public void setCimzettId(ObjectId cimzettId) {
		this.cimzettId = cimzettId;
	}
	public String getCimzettEmail() {
		return cimzettEmail;
	}
	public void setCimzettEmail(String cimzettEmail) {
		this.cimzettEmail = cimzettEmail;
	}
	public Date getKuldesDatum() {
		return kuldesDatum;
	}
	public void setKuldesDatum(Date kuldesDatum) {
		this.kuldesDatum = kuldesDatum;
	}
	public boolean isKezbesitve() {
		return kezbesitve;
	}
	public void setKezbesitve(boolean kezbesitve) {
		this.kezbesitve = kezbesitve;
	}
	public boolean isElolvasva() {
		return elolvasva;
	}
	public void setElolvasva(boolean elolvasva) {
		this.elolvasva = elolvasva;
	}

	public boolean isTorolve() {
		return torolve;
	}

	public void setTorolve(boolean torolve) {
		this.torolve = torolve;
	}
	
}
