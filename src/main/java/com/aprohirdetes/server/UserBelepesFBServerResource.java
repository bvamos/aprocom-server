package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.FormResource;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.HirdetoHelper;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.Utils;

import freemarker.template.Template;

public class UserBelepesFBServerResource extends ServerResource implements
		FormResource {

	private String contextPath = "";
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
	}

	@Override
	public Representation representHtml() throws IOException {
		String message = null;
		String errorMessage = null;
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("belepes.ftl.html");
		Session session = null;
		// Ha kell regisztralni, akkor true lesz. Ha true, akkor majd az adatlapra iranyitjuk belepes/regisztracio utan.
		boolean ujHirdeto = false;
		
		// 1st step: getting back code from FB Login
		String code = getQueryValue("code");
		if(code!=null && !code.isEmpty()){
			//System.out.println(code);
			
			try {
				// Access token lekerese FB APIval
				String responseText = Utils.httpGet("https://graph.facebook.com/v2.3/oauth/access_token?client_id=728081890591646&redirect_uri=" + AproConfig.APP_CONFIG.getProperty("URLBASE") + contextPath + "/belepes/fb&client_secret=2ad09d57151a1cf0b7b51ecc488e3546&code=" + code);
				//System.out.println(responseText);
				JSONObject jsonResponse = new JSONObject(responseText);
				//String fbAccessToken = jsonResponse.getString("access_token");
				//System.out.println(fbAccessToken);
				
				// Email es FB userId lekerese FB APIval
				responseText = Utils.httpGet("https://graph.facebook.com/me?access_token=" + jsonResponse.get("access_token"));
				//System.out.println(responseText);
				jsonResponse = new JSONObject(responseText);
				String fbEmail = jsonResponse.getString("email");
				String fbId = jsonResponse.getString("id");
				
				Hirdeto hirdeto = HirdetoHelper.loadByFbId(fbId);
				if(hirdeto!=null) {
					// Megvan a Hirdeto, majd beleptetjuk
					message = "A Facebook belépés sikeres volt, jó böngészést kívánunk!";
				} else {
					// Nincs Hirdeto ezzel a Facebook id-val. Megnezzuk az Email cimet.
					hirdeto = HirdetoHelper.loadByEmail(fbEmail);
					if(hirdeto!=null) {
						// Van ilyen hirdeto, elmentjuk a FB id-t es majd beleptetjuk
						HirdetoHelper.saveHirdetoField(hirdeto.getId(), "facebookId", fbId);
						message = "A belépés sikeres volt, sőt, össze is kötöttük Facebook fiókodat meglévő felhasználói fiókoddal!";
					} else {
						// Nincs ilyen regisztralt felhasznalonk, csinalunk egyet a FB adatai alapjan es majd beleptetjuk
						hirdeto = new Hirdeto();
						hirdeto.setEmail(fbEmail);
						hirdeto.setFacebookId(fbId);
						
						HirdetoHelper.save(hirdeto);
						ujHirdeto = true;
						message = "A belépés sikeres volt, de úgy tűnik, hogy még nem voltál regisztrált felhasználónk, "
								+ "ezért létrehoztunk Neked egy új fiókot. Most megadhatod a hiányzó adataid!";
					}
				}
				
				// Beleptetjuk a Hirdetot
				if(hirdeto!=null) {
					session = SessionHelper.login(hirdeto);
					if(session!=null) {
						SessionHelper.setSessionCookie(this, session.getSessionId());
						if(ujHirdeto) {
							ftl = AproApplication.TPL_CONFIG.getTemplate("felhasznalo_adatlap.ftl.html");
						}
					}
				}
				
			} catch(JSONException je) {
				je.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	        
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("urlBase", AproConfig.APP_CONFIG.getProperty("URLBASE"));
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Belépés");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("session", session);
		dataModel.put("app", appDataModel);
		dataModel.put("uzenet", message);
		dataModel.put("hibaUzenet", errorMessage);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

	@Override
	public Representation accept(Form form) throws IOException {
		return null;
	}
}
