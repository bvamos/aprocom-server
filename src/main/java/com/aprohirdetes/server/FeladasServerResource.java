package com.aprohirdetes.server;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.FeladasResource;
import com.aprohirdetes.model.Helyseg;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesKep;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class FeladasServerResource extends ServerResource implements
		FeladasResource {

	private ObjectId hirdetesId = null;
	private String contextPath = "";
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		// Cookie-ban taroljuk a hirdetes azonositojat az elso latogataskor es beolvassuk.
		// A hirdetes mentesekor a cookie-t toroljuk.
		// Ha a cookie regebbi, mint 1 ora, akkor ujat generalunk
		Cookie sessionCookie = getRequest().getCookies().getFirst("AproFeladasSession");
		if(sessionCookie != null) {
			hirdetesId = new ObjectId(sessionCookie.getValue());
			System.out.println("Feladas Session cookie letezik: " + sessionCookie.getValue());
			long currentDate = new Date().getTime();
			if(hirdetesId.getTime()<currentDate-3600000) {
				System.out.println("Feladas Session cookie tul regi: " + sessionCookie.getValue());
				hirdetesId = new ObjectId();
			}
		}
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
	}

	@Override
	public Representation representHtml() throws IOException {
		
		// Legordulokhoz adatok feltoltese
		ArrayList<Kategoria> kategoriaList = KategoriaCache.getKategoriaListByParentId(null);
		for(Kategoria o : kategoriaList) {
			ArrayList<Kategoria> alkategoriak = KategoriaCache.getKategoriaListByParentId(o.getIdAsString());
			o.setAlkategoriaList(alkategoriak);
		}
		
		ArrayList<Helyseg> helysegList = HelysegCache.getHelysegListByParentId(null);
		
		// Kepek
		Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
		Query<HirdetesKep> query = datastore.createQuery(HirdetesKep.class);
		
		query.criteria("hirdetesId").equal(hirdetesId);
		HashMap<Integer, String> hirdetesKepMap = new HashMap<Integer, String>();
		for(HirdetesKep hk : query) {
			hirdetesKepMap.put(hk.getSorszam(), getRequest().getRootRef().toString() + "/static/images/" + hk.getFileNev());
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		// TODO: Servlet context
		appDataModel.put("contextRoot", getRequest().getRootRef().toString());
		appDataModel.put("htmlTitle", getApplication().getName() + " - Hirdetés feladása");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", AproUtils.getSession(this));
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		dataModel.put("kepMap", hirdetesKepMap);
		
		if(hirdetesId == null) {
			System.out.println("Uj Feladas Session cookie generalasa");
			hirdetesId = new ObjectId();
			
			CookieSetting cookieSetting = new CookieSetting("AproFeladasSession", hirdetesId.toString());
			cookieSetting.setVersion(0);
			cookieSetting.setAccessRestricted(true);
			cookieSetting.setPath(contextPath + "/feladas");
			cookieSetting.setComment("Session Id");
			cookieSetting.setMaxAge(3600*24);
			getResponse().getCookieSettings().add(cookieSetting);
		}
		
		dataModel.put("feladasSession", hirdetesId.toString());
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("feladas.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

	@Override
	public Representation accept(Form form) throws IOException {
		String message = "";
		
		Hirdetes hi = new Hirdetes();
		hi.setId(hirdetesId);
		try {
			hi.setTipus(Integer.parseInt(form.getFirstValue("hirdetesTipus", "2")));
		} catch (NumberFormatException nfe) {
			hi.setTipus(2);
		}
		hi.setCim(form.getFirstValue("hirdetesCim"));
		hi.setKategoriaId(KategoriaCache.getCacheByUrlNev().get(form.getFirstValue("hirdetesKategoria")).getId());
		hi.setHelysegId(HelysegCache.getCacheByUrlNev().get(form.getFirstValue("hirdetesHelyseg")).getId());
		hi.setSzoveg(form.getFirstValue("hirdetesSzoveg"));
		hi.setEgyebInfo(form.getFirstValue("hirdetesEgyebInfo"));
		try {
			hi.setAr(Integer.parseInt(form.getFirstValue("hirdetesAr", "0")));
		} catch (NumberFormatException nfe) {
			hi.setAr(0);
		}
		
		Hirdeto ho = new Hirdeto();
		ho.setNev(form.getFirstValue("hirdetoNev"));
		ho.setEmail(form.getFirstValue("hirdetoEmail"));
		ho.setTelefon(form.getFirstValue("hirdetoTelefon"));
		ho.setOrszag(form.getFirstValue("hirdetoOrszag"));
		ho.setIranyitoSzam(form.getFirstValue("hirdetoIranyitoSzam"));
		ho.setTelepules(form.getFirstValue("hirdetoTelepules"));
		ho.setCim(form.getFirstValue("hirdetoCim"));
		
		hi.setHirdeto(ho);
		
		Datastore datastore = new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
		Key<Hirdetes> id = datastore.save(hi);
		
		// Kepek atmasolasa a vegleges helyükre
		Query<HirdetesKep> query = datastore.createQuery(HirdetesKep.class);
		query.criteria("hirdetesId").equal(hirdetesId);
		
		for(HirdetesKep hk : query) {
			// Normal meretu kep
			String fileNamePath = AproApplication.APP_CONFIG
					.getProperty("WORKDIR")
					+ "/"
					+ "images_upload" + "/" + hk.getFileNev();
			String destFileNamePath = AproApplication.APP_CONFIG
					.getProperty("WORKDIR")
					+ "/"
					+ "images" + "/" + hk.getFileNev();
			File kepFile = new File(fileNamePath);
			if(kepFile.renameTo(new File(destFileNamePath))) {
				kepFile.delete();
			}
			
			// Thumbnail
			String thumbFileNamePath = AproApplication.APP_CONFIG
					.getProperty("WORKDIR")
					+ "/"
					+ "images_upload" + "/" + hk.getThumbFileNev();
			String destThumbFileNamePath = AproApplication.APP_CONFIG
					.getProperty("WORKDIR")
					+ "/"
					+ "images" + "/" + hk.getThumbFileNev();
			kepFile = new File(thumbFileNamePath);
			if(kepFile.renameTo(new File(destThumbFileNamePath))) {
				kepFile.delete();
			}
		}

		
		message = "A Hirdetés feladása sikeresen megtörtént: " + id.toString();
		
		// Cookie torlese
		try {
			CookieSetting cookieSetting = new CookieSetting("AproFeladasSession", hirdetesId.toString());
			cookieSetting.setVersion(0);
			cookieSetting.setAccessRestricted(true);
			cookieSetting.setPath(contextPath + "/feladas");
			cookieSetting.setComment("Session Id");
			cookieSetting.setMaxAge(0);
			getResponse().getCookieSettings().add(cookieSetting);
		} catch(NullPointerException npe) {
			
		}
		
		// Adatmodell a Freemarker sablonhoz
		ArrayList<Kategoria> kategoriaList = KategoriaCache.getKategoriaListByParentId(null);
		for(Kategoria o : kategoriaList) {
			ArrayList<Kategoria> alkategoriak = KategoriaCache.getKategoriaListByParentId(o.getIdAsString());
			o.setAlkategoriaList(alkategoriak);
		}
		
		ArrayList<Helyseg> helysegList = HelysegCache.getHelysegListByParentId(null);

		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", getRequest().getRootRef().toString());
		appDataModel.put("htmlTitle", getApplication().getName());
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", AproUtils.getSession(this));
		dataModel.put("uzenet", message);
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("hirdetesKategoria", "ingatlan");
		dataModel.put("hirdetesHelyseg", "magyarorszag");
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("feladas_eredmeny.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
