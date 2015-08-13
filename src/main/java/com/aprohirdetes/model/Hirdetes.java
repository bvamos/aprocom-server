package com.aprohirdetes.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

import com.aprohirdetes.exception.HirdetesValidationException;
import com.aprohirdetes.utils.IndexUtils;

@Entity("hirdetes")
public class Hirdetes {
	
	@Id private ObjectId id;
	
	/**
	 * Hirdetes tipusa (Keres, Kinal). 
	 * @see HirdetesTipus
	 */
	private int tipus = HirdetesTipus.KINAL;
	private String cim;
	/**
	 * Hirdetes szovege. Maximum 3000 karakter.
	 */
	private String szoveg;
	public static final int MAX_SZOVEG_LENGTH = 3000;
	/**
	 * Egyeb informaciora mutato link
	 */
	private String egyebInfo;
	public static final int MAX_EGYEBINFO_LENGTH = 1000;
	private int ar;
	
	private ObjectId helysegId;
	private ObjectId kategoriaId;
	
	/**
	 * A felado azonositoja. API-n lehetoseg van mas neveben is feladni hirdetest.
	 */
	private ObjectId feladoId;
	private ObjectId hirdetoId;
	@Embedded private Hirdeto hirdeto;
	
	@NotSaved private LinkedList<HirdetesKep> kepek = new LinkedList<HirdetesKep>();
	private HashMap<String, Object> attributumok = new HashMap<String, Object>();
	@NotSaved private HashMap<String, String> egyebMezok = new HashMap<String, String>();
	private HashSet<String> kulcsszavak = new HashSet<String>();
	private boolean hitelesitve;
	/**
	 * Megjelenesek szama. Minden megjeleniteskor no az erteke.
	 */
	private int megjelenes;
	/**
	 * Utolso modositas datuma
	 */
	private Date modositva;
	/**
	 * Lejarat datuma
	 */
	private Date lejar;
	/**
	 * Utolso ertesites datuma
	 */
	private Date lejarErtesites;
	/**
	 * Torolt statusz. Alapertelmezett: false
	 */
	private boolean torolve = false;
	/**
	 * Torles datuma
	 */
	private Date torolveDatum;
	/**
	 * Hirdetes forrasa. HirdetesForras
	 */
	private int forras;
	/**
	 * A hirdetes helysegenek urlNeve. A modosito html formon kell a helyseg kivalasztasahoz
	 */
	@NotSaved private String helysegUrlNev;
	
	public Hirdetes() {
		setAr(0);
		setHitelesitve(false);
		setMegjelenes(0);
		setModositva(new Date());
		setLejar(30);
		setTorolve(false);
		setForras(HirdetesForras.WEB);
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
	
	public void setTipus(String tipus) {
		if("keres".equalsIgnoreCase(tipus)) {
			this.setTipus(HirdetesTipus.KERES);
		} else {
			this.setTipus(HirdetesTipus.KINAL);
		}
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
		if(szoveg!=null && szoveg.length()>MAX_SZOVEG_LENGTH) {
			this.szoveg = szoveg.substring(0, MAX_SZOVEG_LENGTH);
		} else {
			this.szoveg = szoveg;
		}
	}

	public String getEgyebInfo() {
		return egyebInfo;
	}

	public void setEgyebInfo(String egyebInfo) {
		if(egyebInfo!=null && egyebInfo.length()>MAX_SZOVEG_LENGTH) {
			this.egyebInfo = egyebInfo.substring(0, MAX_SZOVEG_LENGTH);
		} else {
			this.egyebInfo = egyebInfo;
		}
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
	
	public String getHelysegUrlNev() {
		return HelysegCache.getHelysegUrlNev(helysegId);
	}

	public ObjectId getKategoriaId() {
		return kategoriaId;
	}

	public void setKategoriaId(ObjectId kategoriaId) {
		this.kategoriaId = kategoriaId;
	}

	/**
	 * @return the feladoId
	 */
	public ObjectId getFeladoId() {
		return feladoId;
	}

	/**
	 * @param feladoId the feladoId to set
	 */
	public void setFeladoId(ObjectId feladoId) {
		this.feladoId = feladoId;
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
	
	public Date getFeladvaAsDate() {
		return new Date(id.getTime());
	}

	public LinkedList<HirdetesKep> getKepek() {
		return kepek;
	}

	public void setKepek(LinkedList<HirdetesKep> kepek) {
		this.kepek = kepek;
	}
	
	public HashMap<String, Object> getAttributumok() {
		return attributumok;
	}

	public void setAttributumok(HashMap<String, Object> attributumok) {
		this.attributumok = attributumok;
	}

	public HashSet<String> getKulcsszavak() {
		return kulcsszavak;
	}
	
	public boolean isHitelesitve() {
		return hitelesitve;
	}

	public void setHitelesitve(boolean hitelesitve) {
		this.hitelesitve = hitelesitve;
	}

	public int getMegjelenes() {
		return megjelenes;
	}

	public void setMegjelenes(int megjelenes) {
		this.megjelenes = megjelenes;
	}

	public void increaseMegjelenesByOne() {
		this.megjelenes++;
	}

	/**
	 * Utolso modositas datuma. Ha nem letezik, akkor az id-bol veszi az idot, es azt adja vissza.
	 * @return
	 */
	public Date getModositva() {
		return modositva==null ? new Date(id.getTime()) : modositva;
	}

	/**
	 * Utolso modositas datumanak beallitasa
	 * @param modositva Datum, vagy null, akkor a modositas datuma az aktualis datum lesz
	 */
	public void setModositva(Date modositva) {
		if(modositva==null) {
			this.modositva = new Date();
		} else {
			this.modositva = modositva;
		}
	}

	public Date getLejar() {
		return lejar;
	}

	public void setLejar(Date lejar) {
		this.lejar = lejar;
	}
	
	/**
	 * Beallitja a lejarat datumat az aktualis naptol szamitva nap napra
	 * @param nap Ennyi nap mulva fog lejarni a hirdetes
	 */
	public void setLejar(int nap) {
		Calendar c = Calendar.getInstance(); 
		c.setTime(new Date()); 
		c.add(Calendar.DATE, nap);
		this.lejar = c.getTime();
	}
	
	/**
	 * A lejarat datuma timestamp formatumban (millisecond 1970-tol)
	 * @return
	 */
	public long getLejaratDatuma() {
		return (lejar!=null) ? lejar.getTime() : 0;
	}
	
	/**
	 * Az utolso modositas datuma timestamp formatumban (millisecond 1970-tol)
	 * @return
	 */
	public long getModositvaDatumAsLong() {
		return (modositva!=null) ? modositva.getTime() : 0;
	}

	/**
	 * @return the lejarErtesites
	 */
	public Date getLejarErtesites() {
		return lejarErtesites;
	}

	/**
	 * @param lejarErtesites the lejarErtesites to set
	 */
	public void setLejarErtesites(Date lejarErtesites) {
		this.lejarErtesites = lejarErtesites;
	}
	
	/**
	 * Beallitja a lejarat utolso ertesitesenek a datumat az aktualis napra
	 */
	@SuppressWarnings("deprecation")
	public void setLejarErtesites() {
		Date d = new Date();
		d.setHours(0);
		d.setMinutes(0);
		d.setSeconds(0);
		this.lejarErtesites = d;
	}

	public boolean isTorolve() {
		return torolve;
	}

	public void setTorolve(boolean torolve) {
		this.torolve = torolve;
	}

	public Date getTorolveDatum() {
		return torolveDatum;
	}

	public void setTorolveDatum(Date torolveDatum) {
		this.torolveDatum = torolveDatum;
	}

	/**
	 * @return the forras
	 */
	public int getForras() {
		return forras;
	}

	/**
	 * @param forras the forras to set
	 */
	public void setForras(int forras) {
		this.forras = forras;
	}
	
	public void setForras(HirdetesForras forras) {
		this.forras = forras.value();
	}

	/**
	 * Tokenekre bontja a hirdetes cimet es szoveget, es elmenti a tokeneket a kulcsszavak tombben
	 */
	public void tokenize() {
		this.kulcsszavak.clear();
		this.kulcsszavak.addAll(IndexUtils.tokenizeMagyarlanc(this.cim + ". " + this.szoveg));
	}
	
	/**
	 * Hirdetes objektum adattartalmanak validalasa.
	 * @throws HirdetesValidationException
	 */
	public void validate() throws HirdetesValidationException {
		if(getAr()<0) {
			throw new HirdetesValidationException(1014, "Az ár nem lehet nullánál kisebb.");
		}
	}
	
}
