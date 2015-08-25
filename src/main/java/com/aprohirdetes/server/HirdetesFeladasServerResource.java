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
import com.aprohirdetes.exception.HirdetesValidationException;
import com.aprohirdetes.model.Attributum;
import com.aprohirdetes.model.AttributumCache;
import com.aprohirdetes.model.AttributumTipus;
import com.aprohirdetes.model.EsemenyHelper;
import com.aprohirdetes.model.Helyseg;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesHelper;
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
			getLogger().info("Feladas Session cookie letezik: " + hirdetesId + "; " + new Date(hirdetesId.getTime()).toString());
			long currentDate = new Date().getTime();
			if(hirdetesId.getTime()<currentDate-3600000) {
				getLogger().warning("Feladas Session cookie tul regi: " + hirdetesId + "; " + hirdetesId.getTime());
				hirdetesId = new ObjectId();
			}
		}
		
		session = AproUtils.getSession(this);
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
	}

	@Override
	public Representation representHtml() throws IOException {
		
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
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Ingyenes apróhirdetés feladása");
		appDataModel.put("description", "Új, ingyenes apróhirdetés feladása képekkel, akár regisztráció nélkül!");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("kategoriaList", KategoriaCache.getKategoriaListByParentId(null));
		dataModel.put("helysegList", HelysegCache.getHelysegListByParentId(null));
		dataModel.put("kepMap", hirdetesKepMap);
		dataModel.put("hirdetesHelyseg", "magyarorszag");
		dataModel.put("hirdetesKategoria", "bazar");
		
		// Felhasznalo adatainak kitoltese az ures formon
		if(this.session != null) {
			Hirdetes hi = new Hirdetes();
			hi.setHirdeto(HirdetoHelper.load(session.getHirdetoId()));
			
			dataModel.put("session", this.session);
			dataModel.put("hirdetes", hi);
			dataModel.put("hirdetoTipusMsz", (hi.getHirdeto().getTipus()==2) ? "" : "checked");
			dataModel.put("hirdetoTipusCeg", (hi.getHirdeto().getTipus()==2) ? "checked" : "");
		} else {
			dataModel.put("hirdetoTipusMsz", "checked");
			dataModel.put("hirdetoTipusCeg", "");
		}

		// Cookie a feladashoz, ez tarolja a session id-t, amivel a kepek feltolteset megoldjuk
		if(hirdetesId == null) {
			getLogger().info("Uj Feladas Session cookie generalasa");
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
		String hirdetesKategoria = form.getFirstValue("hirdetesKategoria");
		String hirdetesHelyseg = form.getFirstValue("hirdetesHelyseg");
		String egyebAttributumokHtml = AproUtils.getAttributumHtmlByKategoria(hirdetesKategoria);
		
		Hirdetes hi = new Hirdetes();
		
		// Automatikusan hitelesitjuk a Hirdetest, ha a szerver ugy van beallitva (fejlesztes kozben hasznos)
		if("1".equalsIgnoreCase(AproConfig.APP_CONFIG.getProperty("AUTO_VALIDATE", "0"))) {
			hi.setHitelesitve(true);
		}
		
		try {
			
			// Model
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
			ho.setTipus(HirdetoHelper.getHirdetoTipus(form.getFirstValue("hirdetoTipus")));
			ho.setNev(form.getFirstValue("hirdetoNev"));
			if(ho.getTipus()==2) {
				// Ceg
				ho.setCegNev(form.getFirstValue("hirdetoCegNev"));
			}
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
			
			// Validacio
			if(this.session == null) {
				// Csak akkor foglalkozunk a jelszoval, ha nincs belepett felhasznalo 
				if(!form.getFirstValue("hirdetoJelszo").equals(form.getFirstValue("hirdetoJelszo2"))) {
					throw new HirdetesValidationException(1011, "A két jelszó nem egyforma");
				}
			}
			
			if(!"true".equals(form.getFirstValue("feltetelek"))) {
				throw new HirdetesValidationException(1012, "Hirdetés feladásához el kell fogadnod a Felhasználási feltételeinket!");
			}
			
			hi.validate();
			HirdetesHelper.validate(hi);
			
			// Hirdetes mentese
			Datastore datastore = MongoUtils.getDatastore();
			Key<Hirdetes> id = datastore.save(hi);
			
			// Kepek atmasolasa a vegleges helyükre
			Query<HirdetesKep> query = datastore.createQuery(HirdetesKep.class);
			query.criteria("hirdetesId").equal(hirdetesId);
			
			for(HirdetesKep hk : query) {
				// Normal meretu kep
				String fileNamePath = AproConfig.APP_CONFIG
						.getProperty("WORKDIR")
						+ "/"
						+ "images_upload" + "/" + hk.getFileNev();
				String destFileNamePath = AproConfig.APP_CONFIG
						.getProperty("WORKDIR")
						+ "/"
						+ "images" + "/" + hk.getFileNev();
				File kepFile = new File(fileNamePath);
				if(kepFile.renameTo(new File(destFileNamePath))) {
					kepFile.delete();
				}
				
				// Thumbnail
				String thumbFileNamePath = AproConfig.APP_CONFIG
						.getProperty("WORKDIR")
						+ "/"
						+ "images_upload" + "/" + hk.getThumbFileNev();
				String destThumbFileNamePath = AproConfig.APP_CONFIG
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
				getLogger().severe("Hiba az AproFeladasSession cookie torlese kozben: " + npe.getLocalizedMessage());
			}
			
			// Esemeny lementese
			EsemenyHelper.addHirdetesFeladasInfo(hirdetesId, null);
		} catch(HirdetesValidationException hve) {
			hibaUzenet = hve.getMessage();
			getLogger().severe("Hiba a hirdetes feladas kozben. ID: " + hi.getId() + ". Hiba: " + hibaUzenet);
			ftl = AproApplication.TPL_CONFIG.getTemplate("feladas.ftl.html");
			
			// Esemeny lementese
			EsemenyHelper.addHirdetesFeladasError(hve.getEsemenyId(), hibaUzenet);
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
		appDataModel.put("htmlTitle", getApplication().getName() + " - Hirdetés feladása");
		appDataModel.put("description", "Ingyenes hirdetésfeladás, fényképpel. Cégeknek, magánszemélyeknek egyaránt ingyenes!");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", this.session);
		dataModel.put("uzenet", uzenet);
		dataModel.put("hibaUzenet", hibaUzenet);
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("hirdetesKategoria", hirdetesKategoria);
		dataModel.put("hirdetesHelyseg", hirdetesHelyseg);
		dataModel.put("egyebAttributumok", egyebAttributumokHtml);
		dataModel.put("hirdetes", hi);
		dataModel.put("hirdetoTipusMsz", (hi.getHirdeto().getTipus()==2) ? "" : "checked");
		dataModel.put("hirdetoTipusCeg", (hi.getHirdeto().getTipus()==2) ? "checked" : "");
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
