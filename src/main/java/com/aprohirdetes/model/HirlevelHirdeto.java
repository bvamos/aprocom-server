package com.aprohirdetes.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("hirlevelhirdeto")
public class HirlevelHirdeto {

	@Id private ObjectId id;
	private ObjectId hirlevelId;
	private ObjectId hirdetoId;
	private String email;
	private Date kuldesDatum;
	private int statusz;
	private String uzenet;
	
	public HirlevelHirdeto() {
		
	}
	
	public HirlevelHirdeto(ObjectId hirlevelId, ObjectId hirdetoId) {
		this.hirlevelId = hirlevelId;
		this.hirdetoId = hirdetoId;
		//this.setEmail(HirdetoHelper.load(hirdetoId).getEmail());
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	/**
	 * @return the hirlevelId
	 */
	public ObjectId getHirlevelId() {
		return hirlevelId;
	}

	/**
	 * @param hirlevelId the hirlevelId to set
	 */
	public void setHirlevelId(ObjectId hirlevelId) {
		this.hirlevelId = hirlevelId;
	}

	public ObjectId getHirdetoId() {
		return hirdetoId;
	}

	public void setHirdetoId(ObjectId hirdetoId) {
		this.hirdetoId = hirdetoId;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	public Date getKuldesDatum() {
		return kuldesDatum;
	}

	public void setKuldesDatum(Date kuldesDatum) {
		this.kuldesDatum = kuldesDatum;
	}

	public int getStatusz() {
		return statusz;
	}

	public void setStatusz(int statusz) {
		this.statusz = statusz;
	}

	public String getUzenet() {
		return uzenet;
	}

	public void setUzenet(String uzenet) {
		this.uzenet = uzenet;
	}

}
