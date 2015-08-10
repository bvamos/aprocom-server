package com.aprohirdetes.model;

import java.util.ArrayList;

public class RestHiba {
	
	private ArrayList<Hiba> hibak = new ArrayList<Hiba>();
	
	public RestHiba() {
		
	}
	
	public void addHiba(int hibaKod, String hibaUzenet) {
		Hiba hiba = new Hiba(hibaKod, hibaUzenet);
		hibak.add(hiba);
	}

	public ArrayList<Hiba> getHibak() {
		return hibak;
	}

	public void setHibak(ArrayList<Hiba> hibak) {
		this.hibak = hibak;
	}


	private class Hiba {
		private int hibaKod;
		private String hibaUzenet;
		
		public Hiba(int hibaKod, String hibaUzenet) {
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
}
