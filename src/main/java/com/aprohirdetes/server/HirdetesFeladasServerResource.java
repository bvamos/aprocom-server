package com.aprohirdetes.server;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.FormResource;
import com.aprohirdetes.model.Attributum;
import com.aprohirdetes.model.AttributumCache;
import com.aprohirdetes.model.AttributumTipus;
import com.aprohirdetes.model.Helyseg;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesKep;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.HirdetoHelper;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MailUtils;
import com.aprohirdetes.utils.MongoUtils;
import com.aprohirdetes.utils.PasswordHash;

import freemarker.template.Template;

public class HirdetesFeladasServerResource extends ServerResource implements
		FormResource {

	private ObjectId hirdetesId = null;
	private String contextPath = "";
	private Session session;
	
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
		
		session = AproUtils.getSession(this);
		
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
		Datastore datastore = MongoUtils.getDatastore();
		Query<HirdetesKep> query = datastore.createQuery(HirdetesKep.class);
		
		query.criteria("hirdetesId").equal(hirdetesId);
		HashMap<Integer, String> hirdetesKepMap = new HashMap<Integer, String>();
		for(HirdetesKep hk : query) {
			hirdetesKepMap.put(hk.getSorszam(), contextPath + "/static/images/" + hk.getFileNev());
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		// TODO: Servlet context
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Ingyenes apróhirdetés feladása");
		appDataModel.put("description", "Új, ingyenes apróhirdetés feladása képekkel, akár regisztráció nélkül!");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproApplication.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		dataModel.put("kepMap", hirdetesKepMap);
		
		// Felhasznalo adatainak kitoltese az ures formon
		if(this.session != null) {
			Hirdetes hi = new Hirdetes();
			hi.setHirdeto(HirdetoHelper.load(session.getHirdetoId()));
			
			dataModel.put("session", this.session);
			dataModel.put("hirdetes", hi);
		}

		// Cookie a feladashoz, ez tarolja a session id-t, amivel a kepek feltolteset megoldjuk
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
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("feladas_eredmeny.ftl.html");
		String uzenet = "";
		String hibaUzenet = "";
		boolean validated = true;
		
		// Validacio
		
		if(this.session == null) {
			// Csak akkor foglalkozunk a jelszoval, ha nincs belepett felhasznalo 
			if(!form.getFirstValue("hirdetoJelszo").equals(form.getFirstValue("hirdetoJelszo2"))) {
				hibaUzenet = "A két jelszó nem egyforma";
				validated = false;
			}
		}
		
		if(!"true".equals(form.getFirstValue("feltetelek"))) {
			hibaUzenet = "Hirdetés feladásához el kell fogadnod a Felhasználási feltételeinket!";
			validated = false;
		}
		
		// Model
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
		hi.setLejar(30);
		
		// Kulcsszavak kigyujtese
		hi.tokenize();
		
		Hirdeto ho = new Hirdeto();
		if(this.session != null) {
			// Van belepett felhasznalo, az ID-t elmentjuk
			hi.setHirdetoId(this.session.getHirdetoId());
			// Regisztralt felhasznalonak nem kell hitelesites emailben
			hi.setHitelesitve(true);
		}
		ho.setNev(form.getFirstValue("hirdetoNev"));
		ho.setEmail(form.getFirstValue("hirdetoEmail"));
		ho.setTelefon(form.getFirstValue("hirdetoTelefon"));
		ho.setOrszag(form.getFirstValue("hirdetoOrszag"));
		ho.setIranyitoSzam(form.getFirstValue("hirdetoIranyitoSzam"));
		ho.setTelepules(form.getFirstValue("hirdetoTelepules"));
		ho.setCim(form.getFirstValue("hirdetoCim"));
		
		if(this.session == null) {
			try {
				ho.setJelszo(PasswordHash.createHash(form.getFirstValue("hirdetoJelszo")));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		hi.setHirdeto(ho);
		
		// Attributumok
		LinkedList<Attributum> attributumList = AttributumCache.getKATEGORIA_ATTRIBUTUM().get(form.getFirstValue("hirdetesKategoria"));
		if(attributumList != null) {
			for(Attributum attr : attributumList) {
				//System.out.println(attr.getNev() + "=" + form.getFirstValue(attr.getNev()));
				if(form.getFirstValue(attr.getNev()) != null && !form.getFirstValue(attr.getNev()).isEmpty()) {
					if(attr.getTipus() == AttributumTipus.YESNO) {
						hi.getAttributumok().put(attr.getNev(), Boolean.parseBoolean(form.getFirstValue(attr.getNev())));
					} else if(attr.getTipus() == AttributumTipus.NUMBER) {
						try {
							hi.getAttributumok().put(attr.getNev(), Integer.parseInt(form.getFirstValue(attr.getNev())));
						} catch (NumberFormatException nfe) {
							getLogger().warning("Nem sikerult a szamot atkonvertalni. " + attr.getNev() + "=" + form.getFirstValue(attr.getNev()));
						}
					} else {
						hi.getAttributumok().put(attr.getNev(), form.getFirstValue(attr.getNev()));
					}
				}
			}
		}
		
		// Hirdetes mentese
		if(validated) {
			Datastore datastore = MongoUtils.getDatastore();
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
	
			// Level kikuldese
			if(this.session == null) {
				uzenet = "A Hirdetés mentése sikeresen megtörtént. Ahhoz, hogy megjelenjen, a megadott email címre egy aktiváló linket küldtünk. "
					+ "Kérjük, kattints a levélben lévő linkre, és hirdetésed megjelenik oldalunkon!<br>" +
					"Tudtad, hogy ha regisztrált felhasználó vagy, akkor automatikusan aktiváljuk a hirdetést és rögtön megjelenik? " +
					"Még nem késő, <a href=\"${app.contextRoot}/regisztracio\">kattints ide és regisztrálj</a>!";
			} else {
				uzenet = "A Hirdetés mentése sikeresen megtörtént. Mivel regisztrált felhasználó vagy, a hirdetésedet automatikusan aktiváltuk, nincs más teendőd.";
			}
			getLogger().info("Sikeres hirdetesfeladas. " + id.toString());
			if(!MailUtils.sendMailHirdetesFeladva(hi, session)) {
				getLogger().severe("Hiba a hirdetes feladva level kikuldese kozben. ID: " + hi.getId());
			}
			
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
		} else {
			ftl = AproApplication.TPL_CONFIG.getTemplate("feladas.ftl.html");
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
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + "Hirdetés feladása");
		appDataModel.put("description", "Ingyenes hirdetésfeladás, fényképpel. Cégeknek, magánszemélyeknek egyaránt ingyenes!");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproApplication.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", this.session);
		dataModel.put("uzenet", uzenet);
		dataModel.put("hibaUzenet", hibaUzenet);
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("hirdetesKategoria", "ingatlan");
		dataModel.put("hirdetesHelyseg", "magyarorszag");
		dataModel.put("hirdetes", hi);
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
