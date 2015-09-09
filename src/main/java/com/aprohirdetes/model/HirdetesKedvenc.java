package com.aprohirdetes.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

@Entity("kedvenc")
public class HirdetesKedvenc {

	@Id private ObjectId id;
	@Indexed(value=IndexDirection.ASC, name="ix_hirdetoId") private ObjectId hirdetoId;
	@Indexed(value=IndexDirection.ASC, name="ix_hirdetesId") private ObjectId hirdetesId;
	@Embedded private Hirdetes hirdetes;
	
	public HirdetesKedvenc() {
	}
	
	public HirdetesKedvenc(ObjectId hirdetoId, ObjectId hirdetesId) {
		this.setHirdetoId(hirdetoId);
		this.setHirdetes(HirdetesHelper.load(hirdetesId));
	}

	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(ObjectId id) {
		this.id = id;
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

	public ObjectId getHirdetesId() {
		return hirdetesId;
	}

	public void setHirdetesId(ObjectId hirdetesId) {
		this.hirdetesId = hirdetesId;
	}

	/**
	 * @return the hirdetes
	 */
	public Hirdetes getHirdetes() {
		return hirdetes;
	}

	/**
	 * @param hirdetes the hirdetes to set
	 */
	public void setHirdetes(Hirdetes hirdetes) {
		this.hirdetes = hirdetes;
	}

}
