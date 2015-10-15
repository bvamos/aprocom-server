package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.FormResource;
import com.aprohirdetes.exception.AproException;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;

import freemarker.template.Template;

public class UserBelepesServerResource extends ServerResource implements
		FormResource {

	private String contextPath = "";
	private Session session = null;
	private String referrer = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		session = SessionHelper.getSession(this);
		
		referrer = getQueryValue("referrer");
	}

	@Override
	public Representation representHtml() throws IOException {
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("urlBase", AproConfig.APP_CONFIG.getProperty("URLBASE"));
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Belépés");
		appDataModel.put("description", "Hozzáférés a felhasználói oldalakhoz");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", session);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		if(session != null) {
			dataModel.put("hibaUzenet", "Már be vagy lépve. Ha mégsem Te vagy " + this.session.getFelhasznaloNev() + ", <a href=\"" + this.contextPath + "kilepes\">kattints ide és lépj ki</a> gyorsan!");
		}
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("belepes.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

	@Override
	public Representation accept(Form form) throws IOException {
		String message = null;
		String errorMessage = null;
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("belepes.ftl.html");
		
		String felhasznaloNev = form.getFirstValue("signinEmail");
		String jelszo = form.getFirstValue("signinPassword");
		
		try {
			session = SessionHelper.login(felhasznaloNev, jelszo);
			
			// Session Cookie
			SessionHelper.setSessionCookie(this, session.getSessionId());
			
			// Atiranyitas a Hirdeto profiljara vagy a feladas oldalra
			// TODO: Relativ URL eseten kiegesziti, es h01.aprohirdtes.com lesz. Talan csak mas template-et kellene betolteni atiranyitas helyett
			if("feladas".equalsIgnoreCase(this.referrer)) {
				redirectPermanent(this.contextPath + "/feladas");
			} else {
				redirectPermanent(this.contextPath + "/felhasznalo/hirdetesek");
			}
		} catch(AproException ae) {
			getLogger().severe("Hibas belepes: " + ae.getMessage());
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("urlBase", AproConfig.APP_CONFIG.getProperty("URLBASE"));
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Belépés");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("uzenet", message);
		dataModel.put("hibaUzenet", errorMessage);
		dataModel.put("email", felhasznaloNev);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
