package com.aprohirdetes.model;

import java.util.Map;

public class Attributum {

	private String nev;
	private AttributumTipus tipus;
	private String cim;
	private String segitseg;
	private Object alapErtek;
	private Map<String, Object> ertekMap;
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

	public Map<String, Object> getErtekMap() {
		return ertekMap;
	}

	public void setErtekMap(Map<String, Object> ertekMap) {
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
		String ret = "";
		
		switch(getTipus()) {
		case TEXT:
			ret = "<div class=\"form-group\">\n" + 
					"	<label class=\"col-sm-3 control-label\" for=\"" + getNev() + "\">" + getCim() + "</label>\n" + 
					"	<div class=\"col-sm-7\">\n" + 
					"		<input type=\"text\" class=\"form-control\" id=\"" + getNev() + "\" name=\"" + getNev() + "\" autofocus=\"\"";
			if(isKotelezo()) {
				ret += " required=\"\""; 
			}
			ret +=  " placeholder=\"\" value=\"\">\n";
			if(getSegitseg() != null) {
				ret += "		<span class=\"help-block\">" + getSegitseg() + "</span>\n";
			}
			ret +=  "	</div>\n" + 
					"</div>\n";
			break;
		case RADIO:
			ret = "<div class=\"form-group\">\n" + 
					"	<label class=\"col-sm-3 control-label\">" + getCim() + "</label>\n" + 
					"	<div class=\"col-sm-9\">\n";
			for(String ertek : getErtekMap().keySet()) {
				String checked = "";
				if(getAlapErtek() != null){
					checked = ertek.equalsIgnoreCase(getAlapErtek().toString()) ? " checked=\"\"" : "";
				}
				ret += "			<label class=\"radio-inline\">\n" + 
						"				<input type=\"radio\" name=\"" + getNev() + "\" id=\"" + getNev() + ertek + "\" value=\"" + ertek + "\" " + checked + ">\n" + 
						"				" + getErtekMap().get(ertek).toString() + "\n" + 
						"			</label>\n";
			}
			ret += "	</div>\n" + 
					"</div>\n";
		case CHECKBOX:
			break;
		case YESNO:
			String alapErtek = "";
			try {
				alapErtek = ((Boolean) getAlapErtek()) ? " checked=\"\"" : "";
			} catch(NullPointerException npe) {
				
			}
			ret = "<div class=\"form-group\">\n" + 
					"	<div class=\"checkbox col-sm-7 col-sm-offset-3\">\n" + 
					"		<div class=\"checkbox\">\n" + 
					"			<label><input type=\"checkbox\" value=\"true\" name=\"" + getNev() + "\"" + alapErtek + "> " + getCim() + "</label>\n" + 
					"		</div>\n" + 
					"	</div>\n" + 
					"</div>\n";
			break;
		case NUMBER:
			ret = "<div class=\"form-group\">\n" + 
					"	<label class=\"col-sm-3 control-label\" for=\"" + getNev() + "\">" + getCim() + "</label>\n" + 
					"	<div class=\"col-sm-3\">\n";
			if(getMertekEgyseg() != null) {
				ret += "		<div class=\"input-group\">\n";
			}
			ret += "		<input type=\"number\" class=\"form-control\" id=\"" + getNev() + "\" name=\"" + getNev() + "\" autofocus=\"\"";
			if(isKotelezo()) {
				ret += " required=\"\""; 
			}
			ret +=  " placeholder=\"\" value=\"\">\n";
			if(getMertekEgyseg() != null) {
				ret += "		<span class=\"input-group-addon\">" + getMertekEgyseg() + "</span>" +
						"		</div>\n";
			}
			if(getSegitseg() != null) {
				ret += "		<span class=\"help-block\">" + getSegitseg() + "</span>\n";
			}
			ret +=  "	</div>\n" + 
					"</div>\n";
			break;
		case SELECT_MULTI:
			break;
		case SELECT_SINGLE:
			ret = "<div class=\"form-group\">\n" + 
					"	<label class=\"col-sm-3 control-label\" for=\"" + getNev() + "\">" + getCim() + "</label>\n" + 
					"	<div class=\"col-sm-7\">\n" + 
					"		<select class=\"form-control\" id=\"" + getNev() + "\" name=\"" + getNev() + "\">\n";
			if(!isKotelezo()) {
				ret += "			<option value=\"\">Válassz a listából</option>\n";
			}
			for(String ertek : getErtekMap().keySet()) {
				ret += "			<option value=\"" + ertek + "\">" + getErtekMap().get(ertek).toString() + "</option>\n";
			}
			ret += "		</select>\n";
			if(getSegitseg() != null) {
				ret += "		<span class=\"help-block\">" + getSegitseg() + "</span>\n";
			}
			ret +=  "	</div>\n" + 
					"</div>\n";
			break;
		case TEXTAREA:
			break;
		default:
			break;
		}
		
		return ret;
	}
}
