package com.aprohirdetes.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

/**
 * Hirdeteshez csatolt kep
 * 
 * @author bvamos
 *
 */
public class HirdetesKep {

	@Id private ObjectId id;
	
	private int size;
	private String contentType;
	
	private ObjectId hirdetesId;
	
	public HirdetesKep() {
		
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public ObjectId getHirdetesId() {
		return hirdetesId;
	}

	public void setHirdetesId(ObjectId hirdetesId) {
		this.hirdetesId = hirdetesId;
	}
	
	
}
