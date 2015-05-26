package com.aprohirdetes.model;

import org.restlet.data.Form;

public class AttributumYesNo extends Attributum {

	public AttributumYesNo(String nev, String cim) {
		super(nev, AttributumTipus.YESNO, cim);
	}

	@Override
	public String toHtml() {
		String ret = null;
		
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
		
		return ret;
	}
	
	@Override
	public String toSearchHtml(Form query) {
		String ret = null;
		
		String alapErtek = query.getFirstValue(getNev(), "");
		System.out.println(alapErtek);
		String selectedIgen = ("igen".equalsIgnoreCase(alapErtek)) ? " selected" : "";
		String selectedNem = ("nem".equalsIgnoreCase(alapErtek)) ? " selected" : "";
		
		ret = "<div class=\"form-group\">\n" + 
				"	<label class=\"control-label\" for=\"" + getNev() + "\">" + getCim() + "</label>\n" + 
				"	<div class=\"\">\n" + 
				"		<select class=\"form-control input-sm\" id=\"" + getNev() + "\" name=\"" + getNev() + "\">\n";
			ret += "			<option value=\"\"></option>\n";
			ret += "			<option value=\"igen\"" + selectedIgen + ">Igen</option>\n";
			ret += "			<option value=\"nem\"" + selectedNem + ">Nem</option>\n";
		ret += "		</select>\n";
		ret +=  "	</div>\n" + 
				"</div>\n";
		
		return ret;
	}
}
