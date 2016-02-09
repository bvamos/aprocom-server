package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

@Entity("kereses")
public class Kereses {

	@Id private ObjectId id;
	private String nev;
	private ObjectId hirdetoId;
	private String email;
	private int kuldesGyakorisaga;
	private Date utolsoKuldes;
	private int statusz;
	
	private String url;
	@NotSaved private int hirdetesTipus;
	@NotSaved private List<ObjectId> kategoriak = new ArrayList<ObjectId>();
	@NotSaved private List<ObjectId> helysegek = new ArrayList<ObjectId>();
	@NotSaved private LinkedHashMap<String, String> feltetelek = new LinkedHashMap<String, String>();
	
	public Kereses() {
		setKuldesGyakorisaga(Kereses.KuldesGyakorisaga.NAPONTA.value());
		setStatusz(Kereses.Statusz.AKTIV.value());
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getNev() {
		return nev;
	}

	public void setNev(String nev) {
		this.nev = nev;
	}

	public ObjectId getHirdetoId() {
		return hirdetoId;
	}

	public void setHirdetoId(ObjectId hirdetoId) {
		this.hirdetoId = hirdetoId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getKuldesGyakorisaga() {
		return kuldesGyakorisaga;
	}

	public void setKuldesGyakorisaga(int kuldesGyakorisaga) {
		this.kuldesGyakorisaga = kuldesGyakorisaga;
	}

	public Date getUtolsoKuldes() {
		return utolsoKuldes;
	}

	public void setUtolsoKuldes(Date utolsoKuldes) {
		this.utolsoKuldes = utolsoKuldes;
	}

	public int getStatusz() {
		return statusz;
	}

	public void setStatusz(int statusz) {
		this.statusz = statusz;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the hirdetesTipus
	 */
	public int getHirdetesTipus() {
		return hirdetesTipus;
	}

	/**
	 * @param hirdetesTipus the hirdetesTipus to set
	 */
	public void setHirdetesTipus(int hirdetesTipus) {
		this.hirdetesTipus = hirdetesTipus;
	}

	/**
	 * @return the kategoriak
	 */
	public List<ObjectId> getKategoriak() {
		return kategoriak;
	}

	/**
	 * @param kategoriak the kategoriak to set
	 */
	public void setKategoriak(List<ObjectId> kategoriak) {
		this.kategoriak = kategoriak;
	}

	/**
	 * @return the helysegek
	 */
	public List<ObjectId> getHelysegek() {
		return helysegek;
	}

	/**
	 * @param helysegek the helysegek to set
	 */
	public void setHelysegek(List<ObjectId> helysegek) {
		this.helysegek = helysegek;
	}

	/**
	 * @return the feltetelek
	 */
	public LinkedHashMap<String, String> getFeltetelek() {
		return feltetelek;
	}

	/**
	 * @param feltetelek the feltetelek to set
	 */
	public void setFeltetelek(LinkedHashMap<String, String> feltetelek) {
		this.feltetelek = feltetelek;
	}

	public enum KuldesGyakorisaga {
		SOHA(1),
		AZONNAL(11),
		NAPONTA(21),
		HETENTE(22),
		HAVONTA(23);
		
		private final int value;
		
		KuldesGyakorisaga(int value) {
			this.value = value;
		}
		
		public int value() {
			return this.value;
		}
		
		public static KuldesGyakorisaga valueOf(int value) {
			for(KuldesGyakorisaga kgy : values()) {
				if(kgy.value()==value) {
					return kgy;
				}
			}
			return KuldesGyakorisaga.SOHA;
		}
		
		public static int getValueByString(String nev) {
			for(KuldesGyakorisaga kgy : values()) {
				if(kgy.name().equals(nev)) {
					return kgy.value();
				}
			}
			
			return SOHA.value();
		}
	}
	
	public enum Statusz {
		AKTIV(1),
		INAKTIV(2),
		TOROLVE(99);
		
		private final int value;
		
		Statusz(int value) {
			this.value = value;
		}
		
		public int value() {
			return this.value;
		}
	}
}
