package com.aprohirdetes.exception;

public class HirdetesValidationException extends Exception {

	private int esemenyId;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7180287590442638844L;

	public HirdetesValidationException() {
		super();
	}
	
	public HirdetesValidationException(int esemenyId, String msg) {
		super(msg);
		this.esemenyId = esemenyId;
	}

	/**
	 * @return the esemenyId
	 */
	public int getEsemenyId() {
		return esemenyId;
	}

	/**
	 * @param esemenyId the esemenyId to set
	 */
	public void setEsemenyId(int esemenyId) {
		this.esemenyId = esemenyId;
	}
	
}
