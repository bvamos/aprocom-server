package com.aprohirdetes.model;

/**
 * A Hirdetes feladasanak helye<br/>
 * @author bvamos
 *
 */
public enum HirdetesForras {
	/**
	 * A Hirdetest a webes feluleten adtak fel
	 */
	WEB(1),
	/**
	 * A Hirdetest mobil nativ appal adtak fel
	 */
	MOBILE(2),
	/**
	 * A Hirdetest a REST API-n keresztul adtak fel
	 */
	API(3);
	
	private final int value;
	
	HirdetesForras(int value) { 
		this.value = value; 
	}
	
	public int value() { 
		return value;
	}
}
