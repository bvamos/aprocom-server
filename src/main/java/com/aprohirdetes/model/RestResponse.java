package com.aprohirdetes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API valasz objektum. 
 * Ebben angol nevuek az elemek, hogy a JSON reprezentacioban is azok legyenek.
 * @author bvamos
 *
 */
public class RestResponse {

	private boolean success;
	private ArrayList<Hiba> errors;
	private Map<String, Object> data = new HashMap<String, Object>();
	
	public RestResponse() {
		this.success = false;
		this.errors = new ArrayList<Hiba>();
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void addError(int hibaKod, String hibaUzenet) {
		Hiba hiba = new Hiba(hibaKod, hibaUzenet);
		this.errors.add(hiba);
	}

	public ArrayList<Hiba> getErrors() {
		return errors;
	}
	
	public boolean hasError() {
		return errors.size()>0; 
	}

	public void setErrors(ArrayList<Hiba> hibak) {
		this.errors = hibak;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	public void addData(String name, Object value) {
		this.getData().put(name, value);
	}


	public class Hiba {
		private int hibaKod;
		private String hibaUzenet;
		
		Hiba(int hibaKod, String hibaUzenet) {
			this.setHibaKod(hibaKod);
			this.setHibaUzenet(hibaUzenet);
		}
	
		public int getHibaKod() {
			return hibaKod;
		}
	
		void setHibaKod(int hibaKod) {
			this.hibaKod = hibaKod;
		}
	
		public String getHibaUzenet() {
			return hibaUzenet;
		}
	
		void setHibaUzenet(String hibaUzenet) {
			this.hibaUzenet = hibaUzenet;
		}
	}
}
