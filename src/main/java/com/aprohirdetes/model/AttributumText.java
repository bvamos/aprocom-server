package com.aprohirdetes.model;

import org.restlet.data.Form;

public class AttributumText extends Attributum {

	public AttributumText(String nev, String cim) {
		super(nev, AttributumTipus.TEXT, cim);
	}
	
	@Override
	public String toHtml() {
		String ret = "";
		
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
		
		return ret;
	}

	@Override
	public String toSearchHtml(Form query) {
		String ret = "";
		
		ret = "<div class=\"form-group\">\n" + 
				"	<label class=\"control-label\" for=\"" + getNev() + "\">" + getCim() + "</label>\n" + 
				"	<div class=\"\">\n" + 
				"		<input type=\"text\" class=\"form-control input-sm\" id=\"" + getNev() + "\" name=\"" + getNev() + "\" autofocus=\"\"";
		ret +=  " placeholder=\"\" value=\"\" onChange=\"$('#btnKereses').click();\">\n";
		ret +=  "	</div>\n" + 
				"</div>\n";
		
		return ret;
	}
	
	@Override
	public String toString(Object value) {
		String ret = "";
		
		ret = value.toString();
		
		return ret;
	}
}
