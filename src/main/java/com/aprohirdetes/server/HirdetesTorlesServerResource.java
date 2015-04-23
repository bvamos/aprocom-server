package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.bson.types.ObjectId;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticHtmlResource;
import com.aprohirdetes.model.HirdetesHelper;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.utils.AproUtils;

import freemarker.template.Template;

public class HirdetesTorlesServerResource extends ServerResource implements
	StaticHtmlResource {

	private String contextPath = "";
	private ObjectId hirdetesId = null;
	private Session session = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		this.session = AproUtils.getSession(this);
		
		String hirdetesIdString = (String) this.getRequestAttributes().get("hirdetesId");
		try {
			hirdetesId = new ObjectId(hirdetesIdString);
		} catch(IllegalArgumentException iae) {
			getLogger().warning("Hibas hirdetesId: " + hirdetesIdString);
		}
	}

	@Override
	public Representation representHtml() throws IOException {
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("hirdetes_torles.ftl.html");
		String uzenet = null;
		String hibaUzenet = null;
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", this.contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Hirdetés törlése");
		appDataModel.put("description", "Apróhirdetés törlése. Az apróhirdetés törléséhez be kell jelentkezned. Ha nem vagy regisztrált felhasználó, meg kell várnod, amíg a hirdetés lejár.");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		
		if (this.hirdetesId != null) {
			if(this.session != null) {
				dataModel.put("session", this.session);
				
				// Hirdetes torlese
				try {
					HirdetesHelper.delete(hirdetesId);
					
					getLogger().info("Hirdetes torolve: " + this.hirdetesId.toString());
					uzenet = "Hirdetésedet sikeresen töröltük. Köszönjük, hogy minket választottál! Ide kattintva <a href=\"" + contextPath + "/feladas\">feladhatsz egy új hirdetést</a>!";
				} catch (Exception e) {
					getLogger().severe(e.getMessage());
				}
			} else {
				ftl = AproApplication.TPL_CONFIG.getTemplate("forbidden.ftl.html");
				hibaUzenet = "Az apróhirdetés törléséhez be kell jelentkezned. Ha nem vagy regisztrált felhasználó, meg kell várnod, amíg a hirdetés lejár.";
			}
		} else {
			hibaUzenet = "A megadott apróhirdetés nem létezik.";
			// Vagyis valojaban nem tudjuk, hogy letezik-e, de ezt mondjuk, ha hibas az Id
		}
		
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("hirdetesId", hirdetesId!=null ? hirdetesId.toString() : null);
		dataModel.put("uzenet", uzenet);
		dataModel.put("hibaUzenet", hibaUzenet);
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
