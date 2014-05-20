package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.aprohirdetes.utils.IndexUtils;

@Entity("hirdetes")
public class Hirdetes {
	
	@Id private ObjectId id;
	
	private int tipus = 1;
	private String cim;
	private String szoveg;
	private String egyebInfo;
	private int ar;
	
	private ObjectId helysegId;
	private ObjectId kategoriaId;
	private ObjectId hirdetoId;
	
	@Embedded private Hirdeto hirdeto;
	private LinkedList<HirdetesKep> kepek = new LinkedList<HirdetesKep>();
	private HashMap<String, String> egyebMezok = new HashMap<String, String>();
	private List<String> kulcsszavak = new ArrayList<String>();
	private boolean hitelesitve;
	
	private int regiId;
	
	public Hirdetes() {
		setAr(0);
		setHitelesitve(true);
		setRegiId(-1);
	}
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public int getTipus() {
		return tipus;
	}

	public void setTipus(int tipus) {
		this.tipus = tipus;
	}

	public String getCim() {
		return cim;
	}

	public void setCim(String cim) {
		this.cim = cim;
	}

	public String getSzoveg() {
		return szoveg == null ? "" : szoveg;
	}

	public void setSzoveg(String szoveg) {
		this.szoveg = szoveg;
	}

	public String getEgyebInfo() {
		return egyebInfo;
	}

	public void setEgyebInfo(String egyebInfo) {
		this.egyebInfo = egyebInfo;
	}

	public int getAr() {
		return ar;
	}

	public void setAr(int ar) {
		this.ar = ar;
	}

	public ObjectId getHelysegId() {
		return helysegId;
	}

	public void setHelysegId(ObjectId helysegId) {
		this.helysegId = helysegId;
	}

	public ObjectId getKategoriaId() {
		return kategoriaId;
	}

	public void setKategoriaId(ObjectId kategoriaId) {
		this.kategoriaId = kategoriaId;
	}

	public ObjectId getHirdetoId() {
		return hirdetoId;
	}

	public void setHirdetoId(ObjectId hirdetoId) {
		this.hirdetoId = hirdetoId;
	}

	public Hirdeto getHirdeto() {
		return hirdeto;
	}
	
	public void setHirdeto(Hirdeto hirdeto) {
		this.hirdeto = hirdeto;
	}
	
	public HashMap<String, String> getEgyebMezok() {
		return egyebMezok;
	}

	public void setEgyebMezok(HashMap<String, String> egyebMezok) {
		this.egyebMezok = egyebMezok;
	}

	public long getFeladasDatuma() {
		return id.getTime();
	}

	public LinkedList<HirdetesKep> getKepek() {
		return kepek;
	}

	public void setKepek(LinkedList<HirdetesKep> kepek) {
		this.kepek = kepek;
	}
	
	public boolean isHitelesitve() {
		return hitelesitve;
	}

	public void setHitelesitve(boolean hitelesitve) {
		this.hitelesitve = hitelesitve;
	}

	public int getRegiId() {
		return regiId;
	}

	public void setRegiId(int regiId) {
		this.regiId = regiId;
	}

	public void tokenize() {
		this.kulcsszavak.addAll(IndexUtils.tokenize(this.cim));
		this.kulcsszavak.addAll(IndexUtils.tokenize(this.szoveg));
	}
}
