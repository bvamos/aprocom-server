package com.aprohirdetes.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

@Entity("session")
public class Session {

	@Id 
	private ObjectId id;
	@Indexed(value=IndexDirection.ASC, name="ix_sessionId", unique=true, dropDups=true)
	private String sessionId;
	@Indexed(value=IndexDirection.ASC, name="ix_hirdetoId", unique=true, dropDups=true)
	private ObjectId hirdetoId;
	private Date utolsoKeres;
	
	public Session() {
		this.utolsoKeres = new Date();
	}
	
	public ObjectId getId() {
		return id;
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public ObjectId getHirdetoId() {
		return hirdetoId;
	}
	
	public String getHirdetoIdAsString() {
		return hirdetoId.toString();
	}

	public void setHirdetoId(ObjectId hirdetoId) {
		this.hirdetoId = hirdetoId;
	}

	public Date getUtolsoKeres() {
		return utolsoKeres;
	}
	
	public void setUtolsoKeres(Date utolsoKeres) {
		this.utolsoKeres = utolsoKeres;
	}
	
	/**
	 * Ha van HirdetoId (miert ne lenne, ha van session), akkor visszaadja a Hirdeto nevet es email cimet
	 * @return Nev (email cim)
	 */
	public String getFelhasznaloNev() {
		String ret = null;
		
		if(hirdetoId != null) {
			Hirdeto h = HirdetoHelper.load(hirdetoId);
			if(h != null) {
				if(h.getNev() != null && !h.getNev().isEmpty()) {
					ret = h.getNev();
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Ha van HirdetoId (miert ne lenne, ha van session), akkor visszaadja a Hirdeto email cimet
	 * @return Email cim
	 */
	public String getFelhasznaloEmail() {
		String ret = null;
		
		if(hirdetoId != null) {
			Hirdeto h = HirdetoHelper.load(hirdetoId);
			if(h != null) {
				ret = h.getEmail();
			}
		}
		
		return ret;
	}
	
	public String getFelhasznaloApikey() {
		String ret = null;
		
		if(hirdetoId != null) {
			Hirdeto h = HirdetoHelper.load(hirdetoId);
			if(h != null) {
				ret = h.getApiKey();
			}
		}
		
		return ret;
	}
}