package com.aprohirdetes.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.utils.IndexDirection;

@Entity("session")
public class Session {

	@Id 
	private ObjectId id;
	@Indexed(value=IndexDirection.ASC, name="ix_sessionId", unique=true, dropDups=true)
	private String sessionId;
	@Indexed(value=IndexDirection.ASC, name="ix_hirdetoId", unique=true, dropDups=true)
	private ObjectId hirdetoId;
	@NotSaved private Hirdeto hirdeto;
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
		
		if(hirdetoId != null) {
			this.hirdeto = HirdetoHelper.load(hirdetoId);
		}
	}
	
	public Hirdeto getHirdeto() {
		return hirdeto;
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
		return (hirdeto != null) ? hirdeto.getNev() : null;
	}
	
	/**
	 * Ha van HirdetoId (miert ne lenne, ha van session), akkor visszaadja a Hirdeto email cimet
	 * @return Email cim
	 */
	public String getFelhasznaloEmail() {
		return (hirdeto != null) ? hirdeto.getEmail() : null;
	}
	
	public String getFelhasznaloApikey() {
		return (hirdeto != null) ? hirdeto.getApiKey() : null;
	}
}