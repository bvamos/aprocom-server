package com.aprohirdetes.model;

import java.util.Arrays;

import org.restlet.data.Form;

public class AttributumSelectSingle extends Attributum {

	public AttributumSelectSingle(String nev, String cim) {
		super(nev, AttributumTipus.SELECT_SINGLE, cim);
	}
	
	@Override
	public String toHtml() {
		String ret = null;
		
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
	
		return ret;
	}
	
	@Override
	public String toSearchHtml(Form query) {
		String ret = null;
		
		ret = "<div class=\"form-group\">\n" + 
				"	<label class=\"control-label\" for=\"" + getNev() + "\">" + getCim() + "</label>\n";
		for(String ertek : getErtekMap().keySet()) {
			String alapErtek = "";
			try {
				alapErtek = (Arrays.asList(query.getValuesArray(getNev())).contains(ertek)) ? " checked=\"\"" : "";
			} catch(NullPointerException npe) {
				
			}
			
			ret += "	<div class=\"checkbox input-sm\">\n" + 
					"		<label><input type=\"checkbox\" value=\"" + ertek + "\" name=\"" + getNev() + "\"" + alapErtek + "> " + getErtekMap().get(ertek).toString() + "</label>\n" + 
					"	</div>\n";
		}
		ret +=  "</div>\n";
	
		return ret;
	}
	
	@Override
	public String toString(Object value) {
		String ret = "";
		
		ret = getErtekMap().get(value.toString());
		
		return ret;
	}
}
