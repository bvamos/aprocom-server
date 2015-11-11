package com.aprohirdetes.model;

public class HirdetesTipus {

	public static final int KERES = 1;
	public static final int KINAL = 2;
	public static final int BEREL = 3;
	public static final int KIAD = 4;
	
	public static int getHirdetesTipus(String tipus) {
		int ret = KINAL;
		
		switch(tipus) {
		case "kinal":
			ret = KINAL;
			break;
		case "keres":
			ret = KERES;
			break;
		case "berel":
			ret = BEREL;
			break;
		case "kiad":
			ret = KIAD;
			break;
		}
		
		return ret;
	}
	
	public static String getHirdetesTipusNev(int tipus) {
		String[] a = {"", "Keres", "Kínál", "Bérel", "Kiad"};
		String ret = a[tipus];
		return ret;
	}
}
