package com.aprohirdetes.model;

import java.util.Map;

import org.restlet.data.Form;

public class Attributum {

	private String nev;
	private AttributumTipus tipus;
	private String cim;
	private String segitseg;
	private Object alapErtek;
	private Map<String, String> ertekMap;
	private boolean kotelezo;
	private String mertekEgyseg;
	
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

	public String getSegitseg() {
		return segitseg;
	}

	public void setSegitseg(String segitseg) {
		this.segitseg = segitseg;
	}

	public Object getAlapErtek() {
		return alapErtek;
	}

	public void setAlapErtek(Object alapErtek) {
		this.alapErtek = alapErtek;
	}

	public Map<String, String> getErtekMap() {
		return ertekMap;
	}

	public void setErtekMap(Map<String, String> ertekMap) {
		this.ertekMap = ertekMap;
	}

	public boolean isKotelezo() {
		return kotelezo;
	}

	public void setKotelezo(boolean kotelezo) {
		this.kotelezo = kotelezo;
	}
	
	public String getMertekEgyseg() {
		return mertekEgyseg;
	}

	public void setMertekEgyseg(String mertekEgyseg) {
		this.mertekEgyseg = mertekEgyseg;
	}

	public String toHtml() {
		return "";
	}
	
	/**
	 * Visszaadja a kereseshez hasznalt HTML Form reszletet
	 * @param query URL parameterek az alapertelmezett ertekek beallitasahoz
	 * @return
	 */
	public String toSearchHtml(Form query) {
		return "";
	}
}
