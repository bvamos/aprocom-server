package com.aprohirdetes.model;

import java.util.Arrays;

import org.restlet.data.Form;

public class AttributumRadio extends Attributum {

	public AttributumRadio(String nev, String cim) {
		super(nev, AttributumTipus.RADIO, cim);
	}

	@Override
	public String toHtml() {
		String ret = null;
		
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
