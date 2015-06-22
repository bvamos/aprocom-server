package com.aprohirdetes.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("naplo")
public class Esemeny {
	
	/**
	 * Az Esemenyek kulonbozo kategoriakba vannak sorolva
	 * @author bvamos
	 *
	 */
	public enum EsemenyTipus {
		HIRDETES_FELADAS,
		HIRDETES_SZERKESZTES,
		HIRDETES_TORLES,
		FELHASZNALO_REGISZTRACIO,
		FELHASZNALO_BELEPES,
		FELHASZNALO_KILEPES,
		FELHASZNALO_SZERKESZTES,
		UZENET_KULDES,
		UZENET_OLVASAS,
		UZENET_TORLES,
		SMTP
	}
	
	public enum EsemenySzint {
		INFO,
		ERROR
	}

	@Id private ObjectId id;

	/**
	 * Az Esemeny kategoriaja
	 */
	private EsemenyTipus tipus;
	private EsemenySzint szint;
	private ObjectId hirdetesId;
	private ObjectId hirdetoId;
	private String uzenet;
	/**
	 * Azonositja a pontos Esemenyt (tipus+szint+tipuson beluli eredmeny)
	 */
	private int esemenyId;
	
	public Esemeny(EsemenyTipus tipus, EsemenySzint szint, int esemenyId, String uzenet) {
		this.tipus = tipus;
		this.szint = szint;
		this.setEsemenyId(esemenyId);
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

	/**
	 * @return the esemenyId
	 */
	public int getEsemenyId() {
		return esemenyId;
	}

	/**
	 * @param esemenyId the esemenyId to set
	 */
	public void setEsemenyId(int esemenyId) {
		this.esemenyId = esemenyId;
	}
}
