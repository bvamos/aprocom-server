package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.FormResource;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesHelper;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class UserHirdetesServerResource extends ServerResource implements
		FormResource {

	private String contextPath = "";
	private Session session = null;
	private ObjectId hirdetesId = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		this.session = SessionHelper.getSession(this);
		
		try {
			this.hirdetesId = new ObjectId((String) this.getRequestAttributes().get("hirdetesId"));
		} catch(Exception e) {
			getLogger().severe("Hirdetes szerkesztese. Hibas az azonosito: " + (String) this.getRequestAttributes().get("hirdetesId"));
		}
	}

	@Override
	public Representation representHtml() throws IOException {
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("felhasznalo_hirdetes.ftl");
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", this.contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Hirdetés szerkesztése");
		appDataModel.put("description", "Regisztrált felhasználó hirdetéseinek szerkesztése");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		if(this.session == null) {
			ftl = AproApplication.TPL_CONFIG.getTemplate("forbidden.ftl.html");
		} else {
			dataModel.put("session", this.session);

			if(hirdetesId==null) {
				dataModel.put("hibaUzenet", "Hoppá, hibás a Hirdetés azonosítója");
			} else {
				// Hirdetes lekerdezese
				Datastore datastore = MongoUtils.getDatastore();
				Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
				
				query.criteria("id").equal(hirdetesId);
				query.criteria("statusz").notEqual(Hirdetes.Statusz.TOROLVE.value());
				query.criteria("hirdetoId").equal(this.session.getHirdetoId());
				
				Hirdetes hirdetes = query.get();
				if(hirdetes!=null) {
					dataModel.put("hirdetes", hirdetes);
					dataModel.put("helysegList", HelysegCache.getHelysegListByParentId(null));
				} else {
					dataModel.put("hibaUzenet", "Sajnos a megadott azonosítóval nincs Hirdetésed.");
				}
			}
		}
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

	@Override
	public Representation accept(Form form) throws IOException {
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("felhasznalo_hirdetes.ftl");
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", this.contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Hirdetés szerkesztése");
		appDataModel.put("description", "Regisztrált felhasználó hirdetéseinek szerkesztése");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);

		Hirdetes hirdetes = HirdetesHelper.load(form.getFirstValue("hirdetesId"));
		if(hirdetes==null) {
			dataModel.put("hibaUzenet", "Nincs Hirdetés a megadott azonosítóval!");
		} else {
			
			hirdetes.setCim(form.getFirstValue("hirdetesCim"));
			hirdetes.setHelysegId(HelysegCache.getCacheByUrlNev().get(form.getFirstValue("hirdetesHelyseg")).getId());
			hirdetes.setSzoveg(form.getFirstValue("hirdetesSzoveg"));
			try {
				hirdetes.setAr(Integer.parseInt(form.getFirstValue("hirdetesAr", "0")));
			} catch (NumberFormatException nfe) {
				hirdetes.setAr(0);
			}
			// Kulcsszavak kigyujtese
			hirdetes.tokenize();
			
			// Utolso modositas
			hirdetes.setModositva(null);
			
			// Hirdeto adatai
			hirdetes.getHirdeto().setNev(form.getFirstValue("hirdetoNev"));
			hirdetes.getHirdeto().setEmail(form.getFirstValue("hirdetoEmail"));
			hirdetes.getHirdeto().setTelefon(form.getFirstValue("hirdetoTelefon"));
			//hirdetes.getHirdeto().setOrszag(form.getFirstValue("hirdetoOrszag"));
			hirdetes.getHirdeto().setIranyitoSzam(form.getFirstValue("hirdetoIranyitoSzam"));
			hirdetes.getHirdeto().setTelepules(form.getFirstValue("hirdetoTelepules"));
			hirdetes.getHirdeto().setCim(form.getFirstValue("hirdetoCim"));
			
			Datastore datastore = MongoUtils.getDatastore();
			datastore.save(hirdetes);
			
			dataModel.put("hirdetes", hirdetes);
			dataModel.put("helysegList", HelysegCache.getHelysegListByParentId(null));
			dataModel.put("uzenet", "A Hirdetést módosítottuk");
		}
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

	
}
