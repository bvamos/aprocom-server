package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticHtmlResource;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class UserHirdeteseimServerResource extends ServerResource implements
		StaticHtmlResource {

	private String contextPath = "";
	private Session session = null;
	private boolean hirdetesAktiv = true;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		this.session = SessionHelper.getSession(this);
		
		this.hirdetesAktiv = "aktiv".equalsIgnoreCase((String) this.getRequestAttributes().get("tipus"));
	}

	@Override
	public Representation representHtml() throws IOException {
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("felhasznalo_hirdetesek.ftl");
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", this.contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Hirdetéseim");
		appDataModel.put("description", "Regisztrált felhasználó hirdetéseinek szerkesztése");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("hirdetesAktiv", this.hirdetesAktiv);
		
		if(this.session == null) {
			ftl = AproApplication.TPL_CONFIG.getTemplate("forbidden.ftl.html");
		} else {
			dataModel.put("session", this.session);

			// Hirdetesek lekerdezese
			Datastore datastore = MongoUtils.getDatastore();
			Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
			
			query.criteria("hirdetoId").equal(this.session.getHirdetoId());
			
			//System.out.println(query);
			ArrayList<Hirdetes> hirdetesList = new ArrayList<Hirdetes>();
			int countHirdetesAktiv = 0;
			int countHirdetesInaktiv = 0;
			for(Hirdetes h : query) {
				// Darabszamok megszamolasa
				if(h.getStatusz()==Hirdetes.Statusz.AKTIV.value()) {
					countHirdetesAktiv++;
				} else if(h.getStatusz()!=Hirdetes.Statusz.TOROLVE.value()) {
					countHirdetesInaktiv++;
				}
				
				// Szures megjelenitendo tipus alapjan
				if(this.hirdetesAktiv && h.getStatusz()!=Hirdetes.Statusz.AKTIV.value()) {
					// Nem aktiv, pedig nekunk csak azok kellenek
					continue;
				}
				if(!this.hirdetesAktiv && (h.getStatusz()==Hirdetes.Statusz.AKTIV.value() || h.getStatusz()==Hirdetes.Statusz.TOROLVE.value())) {
					// Aktiv, pedig nekunk nem azok kellenek
					continue;
				}
				
				// Kategoria lanc hozzaadasa
				h.getEgyebMezok().put("kategoria", KategoriaCache.getKategoriaNevChain(h.getKategoriaId()));
				
				// Hany napja adtak fel a hirdetest
				h.getEgyebMezok().put("feladvaSzoveg", AproUtils.getHirdetesFeladvaSzoveg(h.getFeladasDatuma()));
				
				// Hany nap mulva jar le a hirdetes
				h.getEgyebMezok().put("lejarSzoveg", AproUtils.getHirdetesLejaratSzoveg(h.getLejaratDatuma()));
				
				// Hirdetes mentese a listaba
				hirdetesList.add(h);
			}
			
			dataModel.put("hirdetesList", hirdetesList);
			dataModel.put("countHirdetesAktiv", countHirdetesAktiv);
			dataModel.put("countHirdetesInaktiv", countHirdetesInaktiv);
		}
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
