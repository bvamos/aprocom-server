package com.aprohirdetes.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Hirdeteshez csatolt kep
 * 
 * @author bvamos
 *
 */
@Entity("kep")
public class HirdetesKep {

	@Id
	@JsonIgnore
	private ObjectId id;
	
	private int sorszam;
	private String fileNev;
	private String thumbFileNev;
	private long fileMeret;
	private String contentType;
	
	@JsonIgnore
	private ObjectId hirdetesId;
	
	public HirdetesKep() {
		setSorszam(1);
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public int getSorszam() {
		return sorszam;
	}

	public void setSorszam(int sorszam) {
		this.sorszam = sorszam;
	}

	public String getFileNev() {
		return fileNev;
	}

	public void setFileNev(String fileNev) {
		this.fileNev = fileNev;
	}

	public String getThumbFileNev() {
		return thumbFileNev;
	}

	public void setThumbFileNev(String thumbFileNev) {
		this.thumbFileNev = thumbFileNev;
	}

	public long getMeret() {
		return fileMeret;
	}

	public void setMeret(long meret) {
		this.fileMeret = meret;
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
