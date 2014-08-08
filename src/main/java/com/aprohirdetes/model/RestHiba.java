package com.aprohirdetes.model;

public class RestHiba {

	private int hibaKod;
	private String hibaUzenet;
	
	public RestHiba(int hibaKod, String hibaUzenet) {
		this.setHibaKod(hibaKod);
		this.setHibaUzenet(hibaUzenet);
	}

	public int getHibaKod() {
		return hibaKod;
	}

	public void setHibaKod(int hibaKod) {
		this.hibaKod = hibaKod;
	}

	public String getHibaUzenet() {
		return hibaUzenet;
	}

	public void setHibaUzenet(String hibaUzenet) {
		this.hibaUzenet = hibaUzenet;
	}
	
}
