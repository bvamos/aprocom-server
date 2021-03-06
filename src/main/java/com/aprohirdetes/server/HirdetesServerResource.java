package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.bson.types.ObjectId;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticHtmlResource;
import com.aprohirdetes.model.Attributum;
import com.aprohirdetes.model.AttributumCache;
import com.aprohirdetes.model.EsemenyHelper;
import com.aprohirdetes.model.Helyseg;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesHelper;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.AproUtils;

import freemarker.template.Template;

public class HirdetesServerResource extends ServerResource implements
		StaticHtmlResource {

	private String contextPath = "";
	/**
	 * Hirdetes azonositoja
	 */
	private ObjectId hirdetesId;
	
	private Hirdetes hirdetes = null;
	
	private Session session;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		try {
			this.hirdetesId = new ObjectId((String) this.getRequestAttributes().get("hirdetesId"));
			this.hirdetes = HirdetesHelper.load(hirdetesId);
		} catch(IllegalArgumentException iae) {
			getLogger().severe("Hirdetes megjelenitese: nem jo az id formatuma: " + (String) this.getRequestAttributes().get("hirdetesId"));
		}
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		this.contextPath = sc.getContextPath();
		
		this.session = SessionHelper.getSession(this);
	}

	@SuppressWarnings("unchecked")
	public Representation representHtml() throws IOException {
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName());
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", this.session);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		// Hirdetes adatai
		if(hirdetes != null) {
			if(hirdetes.getStatusz()==Hirdetes.Statusz.AKTIV.value()) {
				// HTML Title modositasa
				((HashMap<String, Object>) dataModel.get("app")).put("htmlTitle", getApplication().getName() + " - " + hirdetes.getCim());
				((HashMap<String, Object>) dataModel.get("app")).put("description", "Apróhirdetés: " + hirdetes.getCim());
				
				// Megjelenes szamanak novelese
				HirdetesHelper.increaseMegjelenes(hirdetes);
			    
			    // Megjelenes esemeny logolasa
			    EsemenyHelper.addHirdetesMegjelenesInfo(hirdetes.getId(), (session==null) ? null : session.getHirdetoId());
				
				// Kereses eredmenyeben levo Hirdetes objektum feltoltese kepekkel, egyeb adatokkal a megjeleniteshez
				hirdetes.getEgyebMezok().put("tipusNev", (hirdetes.getTipus()==HirdetesTipus.KINAL) ? "Kínál" : "Keres");
				
				Kategoria kat = KategoriaCache.getCacheById().get(hirdetes.getKategoriaId());
				hirdetes.getEgyebMezok().put("kategoriaNev", (kat!=null) ? KategoriaCache.getKategoriaNevChain(hirdetes.getKategoriaId()) : "");
				hirdetes.getEgyebMezok().put("kategoriaUrlNev", (kat!=null) ? kat.getUrlNev() : "");
				
				Helyseg hely = HelysegCache.getCacheById().get(hirdetes.getHelysegId());
				hirdetes.getEgyebMezok().put("helysegNev", (hely!=null) ? HelysegCache.getHelysegNevChain(hirdetes.getHelysegId()) : "");
				hirdetes.getEgyebMezok().put("helysegUrlNev", (hely!=null) ? hely.getUrlNev() : "");
				
				hirdetes.getEgyebMezok().put("feladvaSzoveg", AproUtils.getHirdetesFeladvaSzoveg(hirdetes.getFeladasDatuma()));
				hirdetes.getEgyebMezok().put("lejaratSzoveg", AproUtils.getHirdetesLejaratSzoveg(hirdetes.getLejaratDatuma()));
				hirdetes.getEgyebMezok().put("modositvaSzoveg", AproUtils.getHirdetesFeladvaSzoveg(hirdetes.getModositvaDatumAsLong()));
				
				// Attributumok cimenek, ertekenek (ha kell) atirasa
				try {
					LinkedList<Attributum> attributumList = AttributumCache.getKATEGORIA_ATTRIBUTUM().get(kat.getUrlNev());
					// Attributumnev->ertek formazva
					HashMap<String, Object> attributumok = new HashMap<String, Object>();
					
					for(String key : hirdetes.getAttributumok().keySet()) {
						for(Attributum attr : attributumList) {
							if(attr.getNev().equalsIgnoreCase(key)) {
								Object o = hirdetes.getAttributumok().get(key);
								attributumok.put(attr.getCim(), attr.toString(o));
								break;
							}
						}
					}
					hirdetes.setAttributumok(attributumok);
				} catch(NullPointerException npe) {
					getLogger().severe("Nincs ilyen kategoria");
				}
				
				dataModel.put("hirdetes", hirdetes);
				
			} else if(hirdetes.getStatusz()==Hirdetes.Statusz.HITELESITVE.value()) {
				// Letezik a hirdetes, de nincs hitelesitve a felado altal
				dataModel.put("hibaUzenet", "A megadott hirdetés ugyan létezik, de a feladó még nem hitelesítette az emailben kiküldött linkre kattintva.");
			} else if(hirdetes.getStatusz()==Hirdetes.Statusz.INAKTIV_ELADVA.value() 
					|| hirdetes.getStatusz()==Hirdetes.Statusz.INAKTIV_LEJART.value()
					|| hirdetes.getStatusz()==Hirdetes.Statusz.INAKTIV.value()){
				// Letezik a hirdetes, de mar lejart es/vagy torolve lett
				dataModel.put("hibaUzenet", "A megadott hirdetés már lejárt, és feladója nem hosszabbította meg.");
			}
		} else {
			// Nincs ilyen hirdetes
			getLogger().severe("Nincs meg a megadott hirdetes: " + (String) this.getRequestAttributes().get("hirdetesId"));
			dataModel.put("hibaUzenet", "A megadott hirdetés nem található. Valószinűleg oldalunk segítségével már vevőre lelt a meghirdetett termék vagy szolgáltatás.");
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		}
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("hirdetes.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
