package com.aprohirdetes.server;

import java.io.IOException;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.FormResource;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.HirdetoHelper;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.Utils;

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
		
		// 1st step: getting back code from FB Login
		String code = getQueryValue("code");
		if(code!=null && !code.isEmpty()){
			System.out.println(code);
			
			try {
				// Access token lekerese FB APIval
				String responseText = Utils.httpGet("https://graph.facebook.com/v2.3/oauth/access_token?client_id=728081890591646&redirect_uri=https://www.aprohirdetes.com" + contextPath + "/belepes/fb&client_secret=2ad09d57151a1cf0b7b51ecc488e3546&code=" + code);
				System.out.println(responseText);
				JSONObject jsonResponse = new JSONObject(responseText);
				String fbAccessToken = jsonResponse.getString("access_token");
				System.out.println(fbAccessToken);
				
				// Email es FB userId lekerese FB APIval
				responseText = Utils.httpGet("https://graph.facebook.com/me?access_token=" + jsonResponse.get("access_token"));
				System.out.println(responseText);
				jsonResponse = new JSONObject(responseText);
				String fbEmail = jsonResponse.getString("email");
				String fbId = jsonResponse.getString("id");
				
				Hirdeto hirdeto = HirdetoHelper.loadByFbId(fbId);
				if(hirdeto!=null) {
					// Megvan a Hirdeto, majd beleptetjuk
				} else {
					// Nincs Hirdeto ezzel a Facebook id-val. Megnezzuk az Email cimet.
					hirdeto = HirdetoHelper.loadByEmail(fbEmail);
					if(hirdeto!=null) {
						// Van ilyen hirdeto, elmentjuk a FB id-t es majd beleptetjuk
						HirdetoHelper.saveHirdetoField(hirdeto.getId(), "facebookId", fbId);
					} else {
						// Nincs ilyen regisztralt felhasznalonk, csinalunk egyet a FB adatai alapjan es majd beleptetjuk
						hirdeto = new Hirdeto();
						hirdeto.setEmail(fbEmail);
						hirdeto.setFacebookId(fbId);
						
						HirdetoHelper.save(hirdeto);
					}
				}
				
				// Beleptetjuk a Hirdetot
				if(hirdeto!=null) {
					Session session = SessionHelper.login(hirdeto);
					if(session!=null) {
						SessionHelper.setSessionCookie(this, session.getSessionId());
					}
				}
				
			} catch(JSONException je) {
				je.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	        
		}
		
		return null;
	}

	@Override
	public Representation accept(Form form) throws IOException {
		System.out.println("POST");
		return null;
	}
}
