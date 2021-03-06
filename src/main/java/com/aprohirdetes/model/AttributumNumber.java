package com.aprohirdetes.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.restlet.data.Form;

public class AttributumNumber extends Attributum {

	private boolean formazott;
	
	public AttributumNumber(String nev, String cim) {
		super(nev, AttributumTipus.NUMBER, cim);
		setFormazott(true);
	}
	
	public boolean getFormazott() {
		return this.formazott;
	}
	
	public void setFormazott(boolean formazott) {
		this.formazott = formazott;
	}
	
	@Override
	public String toHtml() {
		String ret = "";
		
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
		
		return ret;
	}

	@Override
	public String toSearchHtml(Form query) {
		String ret = "";
		
		// Minimum ertek
		ret = "<div class=\"form-group\">\n" + 
				"	<label class=\"control-label\" for=\"" + getNev() + "\">" + getCim() + "</label>\n";
		if(getMertekEgyseg() != null) {
			ret += "		<div class=\"input-group\">\n";
		}
		ret += "		<input type=\"number\" class=\"form-control input-sm\" " +
				"id=\"" + getNev() + "_min\" name=\"" + getNev() + "_min\" autofocus=\"\"" +
				" placeholder=\"-tól\" value=\"" + query.getFirstValue(getNev()+"_min", "") + "\" onChange=\"$('#btnKereses').click();\">\n";
		if(getMertekEgyseg() != null) {
			ret += "		<span class=\"input-group-addon\">" + getMertekEgyseg() + "</span>" +
					"		</div>\n";
		}
		ret +=  "</div>\n";
		
		// Maximum ertek
		ret += "<div class=\"form-group\">\n";
		if(getMertekEgyseg() != null) {
			ret += "		<div class=\"input-group\">\n";
		}
		ret += "		<input type=\"number\" class=\"form-control input-sm\" " +
				"id=\"" + getNev() + "_max\" name=\"" + getNev() + "_max\" autofocus=\"\"" +
				" placeholder=\"-ig\" value=\"" + query.getFirstValue(getNev()+"_max", "") + "\" onChange=\"$('#btnKereses').click();\">\n";
		if(getMertekEgyseg() != null) {
			ret += "		<span class=\"input-group-addon\">" + getMertekEgyseg() + "</span>" +
					"		</div>\n";
		}
		ret +=  "</div>\n";
		
		return ret;
	}
	
	@Override
	public String toString(Object value) {
		String ret = "";
		
		if(getFormazott()) {
			NumberFormat nf = NumberFormat.getNumberInstance(new Locale("hu", "HU"));
			DecimalFormat df = (DecimalFormat) nf;
			try {
				ret = df.format(value);
			} catch (IllegalArgumentException iae) {
				//System.out.println(getNev() + "=" + value);
			}
		} else {
			ret = value.toString();
		}
		
		if(getMertekEgyseg() != null) {
			ret += " " + getMertekEgyseg();
		}
		
		return ret;
	}
}
