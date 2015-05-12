package com.aprohirdetes.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("naplo")
public class Esemeny {
	
	public enum EsemenyTipus {
		HIRDETES_FELADAS
	}
	
	public enum EsemenySzint {
		INFO,
		ERROR
	}

	@Id private ObjectId id;
	
	private EsemenyTipus tipus;
	private EsemenySzint szint;
	private ObjectId hirdetesId;
	private ObjectId hirdetoId;
	private String uzenet;
	
	public Esemeny(EsemenyTipus tipus, EsemenySzint szint, String uzenet) {
		this.tipus = tipus;
		this.szint = szint;
		this.uzenet = uzenet;
	}

	public EsemenyTipus getTipus() {
		return tipus;
	}
	
	public EsemenySzint getSzint() {
		return szint;
	}

	public ObjectId getHirdetesId() {
		return hirdetesId;
	}

	public void setHirdetesId(ObjectId hirdetesId) {
		this.hirdetesId = hirdetesId;
	}

	/**
	 * @return the hirdetoId
	 */
	public ObjectId getHirdetoId() {
		return hirdetoId;
	}

	/**
	 * @param hirdetoId the hirdetoId to set
	 */
	public void setHirdetoId(ObjectId hirdetoId) {
		this.hirdetoId = hirdetoId;
	}

	public String getUzenet() {
		return uzenet;
	}

	public void setUzenet(String uzenet) {
		this.uzenet = uzenet;
	}

	public ObjectId getId() {
		return id;
	}
}
