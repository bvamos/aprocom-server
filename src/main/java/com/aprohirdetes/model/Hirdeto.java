package com.aprohirdetes.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

@Entity("hirdeto")
public class Hirdeto {

	@Id private ObjectId id;
	
	/**
	 * Hirdeto tipusa. Maganszemely (1) vagy Ceg (2)
	 */
	private int tipus;
	
	private String nev;
	private String cegNev;
	@Indexed(value=IndexDirection.ASC, name="ix_email", unique=true) private String email;
	private String iranyitoSzam;
	private String telepules;
	private String cim;
	private String orszag;
	private String telefon;
	private boolean hirlevel;
	
	private String jelszo;
	@Indexed(value=IndexDirection.ASC, name="ix_apiKey", unique=true) private String apiKey;
	private boolean hitelesitve;
	private Date regisztralas;
	private Date utolsoBelepes;
	
	public Hirdeto() {
		setTipus(1);
		setHirlevel(true);
		setHitelesitve(false);
		setRegisztralas(new Date());
	}
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getIdAsString() {
		return (id==null) ? null : id.toString();
	}
	
	/**
	 * @return the tipus
	 */
	public int getTipus() {
		return tipus;
	}

	/**
	 * @param tipus the tipus to set
	 */
	public void setTipus(int tipus) {
		this.tipus = tipus;
	}

	public String getNev() {
		return nev;
	}
	
	public void setNev(String nev) {
		this.nev = nev;
	}

	/**
	 * @return the cegNev
	 */
	public String getCegNev() {
		return cegNev;
	}

	/**
	 * @param cegNev the cegNev to set
	 */
	public void setCegNev(String cegNev) {
		this.cegNev = cegNev;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIranyitoSzam() {
		return iranyitoSzam;
	}

	public void setIranyitoSzam(String iranyitoSzam) {
		this.iranyitoSzam = iranyitoSzam;
	}

	public String getTelepules() {
		return telepules;
	}

	public void setTelepules(String telepules) {
		this.telepules = telepules;
	}

	public String getCim() {
		return cim;
	}

	public void setCim(String cim) {
		this.cim = cim;
	}

	public String getOrszag() {
		return orszag;
	}

	public void setOrszag(String orszag) {
		this.orszag = orszag;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}

	public boolean isHirlevel() {
		return hirlevel;
	}

	public void setHirlevel(boolean hirlevel) {
		this.hirlevel = hirlevel;
	}

	public String getJelszo() {
		return jelszo;
	}

	public void setJelszo(String jelszo) {
		this.jelszo = jelszo;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public boolean isHitelesitve() {
		return hitelesitve;
	}

	public void setHitelesitve(boolean hitelesitve) {
		this.hitelesitve = hitelesitve;
	}

	/**
	 * @return the regisztralas
	 */
	public Date getRegisztralas() {
		return regisztralas;
	}

	/**
	 * @param regisztralas the regisztralas to set
	 */
	public void setRegisztralas(Date regisztralas) {
		this.regisztralas = regisztralas;
	}

	public Date getUtolsoBelepes() {
		return utolsoBelepes;
	}

	public void setUtolsoBelepes(Date utolsoBelepes) {
		this.utolsoBelepes = utolsoBelepes;
	}

	public JSONObject toJSONObject() throws JSONException {
		JSONObject hirdetoJson = new JSONObject();
		
		if(this.id != null) hirdetoJson.put("id", this.id.toString());
		if(this.nev != null) hirdetoJson.put("nev", this.nev);
		
		return hirdetoJson;
	}
}
