package com.aprohirdetes.model;

import java.util.Map;

public class Attributum {

	private String nev;
	private AttributumTipus tipus;
	private String cim;
	private Object alapErtek;
	private Map<String, Object> ertekMap;
	private boolean kotelezo;
	
	public Attributum(String nev, AttributumTipus tipus, String cim) {
		setNev(nev);
		setTipus(tipus);
		setCim(cim);
		setKotelezo(false);
	}

	public String getNev() {
		return nev;
	}

	public void setNev(String nev) {
		this.nev = nev;
	}

	public AttributumTipus getTipus() {
		return tipus;
	}

	public void setTipus(AttributumTipus tipus) {
		this.tipus = tipus;
	}

	public String getCim() {
		return cim;
	}

	public void setCim(String cim) {
		this.cim = cim;
	}

	public Object getAlapErtek() {
		return alapErtek;
	}

	public void setAlapErtek(Object alapErtek) {
		this.alapErtek = alapErtek;
	}

	public Map<String, Object> getErtekMap() {
		return ertekMap;
	}

	public void setErtekMap(Map<String, Object> ertekMap) {
		this.ertekMap = ertekMap;
	}

	public boolean isKotelezo() {
		return kotelezo;
	}

	public void setKotelezo(boolean kotelezo) {
		this.kotelezo = kotelezo;
	}
}