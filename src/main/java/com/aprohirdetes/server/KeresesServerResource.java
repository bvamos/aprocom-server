package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticHtmlResource;
import com.aprohirdetes.model.Attributum;
import com.aprohirdetes.model.AttributumCache;
import com.aprohirdetes.model.AttributumSelectSingle;
import com.aprohirdetes.model.AttributumTipus;
import com.aprohirdetes.model.Helyseg;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.HirdetesKedvenc;
import com.aprohirdetes.model.HirdetesKep;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.model.SessionHelper;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Template;

public class KeresesServerResource extends ServerResource implements
		StaticHtmlResource {

	private String contextPath = "";
	private String hirdetesTipusString = "kinal";
	/**
	 * Hirdetes tipusa. 1=Keres, 2=Kinal, 3=Berel, 4=Kiad
	 */
	private int hirdetesTipus = HirdetesTipus.KINAL;
	/**
	 * A kivalasztott kategoriak URL neveit tartalmazo, + jellel elvalasztott lista. Az URL-bol jon.
	 */
	private String selectedKategoriaUrlNevListString = null;
	/**
	 * A kivalasztott Kategoria objektumokat tartalmazo lista
	 */
	private List<Kategoria> selectedKategoriaList = new LinkedList<Kategoria>();
	/**
	 * Kivalasztott kategoriak URL neveit tartalmazo, + jellel elvalasztott lista. Az URL-bol jon.
	 */
	private String selectedHelysegUrlNevListString = null;
	/**
	 * A kivalasztott Helyseg objektumokat tartalmazo lista
	 */
	private List<Helyseg> selectedHelysegList = new LinkedList<Helyseg>();
	
	private Session session;
	private String kulcsszo;
	private Integer arMin;
	private Integer arMax;
	private int page;
	private int pageSize = Integer.parseInt(AproConfig.APP_CONFIG.getProperty("SEARCH.DEFAULT_PAGESIZE", "20"));
	private int sorrend;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		this.selectedKategoriaUrlNevListString = (String) this.getRequestAttributes().get("kategoriaList");
		this.selectedKategoriaList = KategoriaCache.getKategoriaListByUrlNevList(this.selectedKategoriaUrlNevListString);
		
		this.selectedHelysegUrlNevListString = (String) this.getRequestAttributes().get("helysegList");
		if(this.selectedHelysegUrlNevListString == null || this.selectedHelysegUrlNevListString.isEmpty()) {
			// Alapertelmezett helyseg: Osszes helyseg
			this.selectedHelysegUrlNevListString = "osszes-helyseg";
		}
		this.selectedHelysegList = HelysegCache.getHelysegListByUrlNevList(this.selectedHelysegUrlNevListString);
		
		this.hirdetesTipusString = (String) this.getRequestAttributes().get("hirdetesTipus");
		this.hirdetesTipus = HirdetesTipus.getHirdetesTipus(this.hirdetesTipusString);
		
		this.kulcsszo = getQueryValue("q")==null ? "" : getQueryValue("q");
		
		try {
			this.arMin = getQueryValue("ar_min")==null ? null : Integer.parseInt(getQueryValue("ar_min"));
		} catch(NumberFormatException nfe) {
			getLogger().warning("Ervenytelen minimum ar: " + getQueryValue("ar_min"));
			this.arMin = null;
		}
		
		try {
			this.arMax = getQueryValue("ar_max")==null ? null : Integer.parseInt(getQueryValue("ar_max"));
		} catch(NumberFormatException nfe) {
			getLogger().warning("Ervenytelen maximum ar: " + getQueryValue("ar_max"));
			this.arMax = null;
		}
		
		// Set current page
		try {
			this.page = Math.max(0, Integer.parseInt(
					(getQueryValue("p") == null || getQueryValue("p").isEmpty()) ? "1"  : getQueryValue("p")
				));
		} catch(NumberFormatException nfe) {
			this.page = 1;
		}
		
		try {
			this.sorrend = Integer.parseInt(
				(getQueryValue("s") == null || getQueryValue("s").isEmpty()) ? "0"  : getQueryValue("s")
			);
		} catch(NumberFormatException nfe) {
			this.sorrend = 0;
		}
		
		this.session = SessionHelper.getSession(this);
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
	}

	public Representation representHtml() throws IOException {
		String hibaUzenet = null;
		/**
		 * Kivalasztott kategoriak Id-jait tartalmazo lista. A kereseshez kell.
		 */
		ArrayList<ObjectId> selectedKategoriaIdList = new ArrayList<ObjectId>();
		/**
		 * Kivalasztott kategoriak UrlNeveit tartalmazo lista. A legordulo menuhoz kell, hogy be tudjuk allitani a kivalasztott elemeket.
		 */
		ArrayList<String> selectedKategoriaUrlNevList = new ArrayList<String>();
		for(Kategoria kat : selectedKategoriaList) {
			selectedKategoriaIdList.add(kat.getId());
			selectedKategoriaUrlNevList.add(kat.getUrlNev());
		}
		
		/**
		 * Kivalasztott helysegek Id-jait tartalmazo lista. A kereseshez kell.
		 */
		ArrayList<ObjectId> selectedHelysegIdList = new ArrayList<ObjectId>();
		/**
		 * Kivalasztott helysegek UrlNeveit tartalmazo lista. A legordulo menuhoz kell, hogy be tudjuk allitani a kivalasztott elemeket.
		 */
		ArrayList<String> selectedHelysegUrlNevList = new ArrayList<String>();
		for(Helyseg helyseg : selectedHelysegList) {
			selectedHelysegIdList.add(helyseg.getId());
			selectedHelysegUrlNevList.add(helyseg.getUrlNev());
		}

		// Kereses
		// !!!!!! BARMI MODOSUL ITT, AZ RssServerResource-BAN IS MODOSITANI KELL !!!!!
		long hirdetesekSzama = 0;
		List<Hirdetes> hirdetesList = new ArrayList<Hirdetes>();
		
		try {
			// Kereses Morphiaval
			Datastore datastore = MongoUtils.getDatastore();
			Query<Hirdetes> query = datastore.createQuery(Hirdetes.class);
			
			query.criteria("statusz").equal(Hirdetes.Statusz.AKTIV.value());
			query.criteria("tipus").equal(this.hirdetesTipus);
			if(selectedHelysegIdList.size()>0) {
				query.criteria("helysegId").in(selectedHelysegIdList);
			}
			if(selectedKategoriaIdList.size()>0) {
				query.criteria("kategoriaId").in(selectedKategoriaIdList);
			}
			if(!this.kulcsszo.isEmpty()) {
				query.criteria("kulcsszavak").equal(kulcsszo);
			}
			if(this.arMin != null) {
				query.criteria("ar").greaterThanOrEq(arMin);
			}
			if(this.arMax != null) {
				query.criteria("ar").lessThanOrEq(arMax);
			}
			// Attributumok
			ArrayList<Attributum> attributumList = new ArrayList<Attributum>();
			ArrayList<Attributum> kategoriaAttributumList = AttributumCache.getAttributumListByKategoriaList(selectedKategoriaList);
			for(Parameter queryParameter : getQuery()) {
				String parameterNev = queryParameter.getName();
				String attributumNev = parameterNev;
				if(parameterNev.contains("_")) {
					attributumNev = parameterNev.substring(0, parameterNev.indexOf("_"));
				}
				//System.out.println(parameterNev + ", " + attributumNev);
				
				for(Attributum attributum : kategoriaAttributumList) {
					// Csak azok a parameterek vannak itt, amik a kivalasztott kategoriak attributumai
					if(attributum.getNev().equalsIgnoreCase(attributumNev) && !attributumList.contains(attributum)) {
						//System.out.println(attributum.getNev());
						// Belerakjuk a listaba, amiben a kivalasztott kategoriakhoz tartozo es URL-ben szereplo attributumok kerulnek
						attributumList.add(attributum);
					}
				}
			}
			
			for(Attributum attributum : attributumList) {
				String nev = attributum.getNev();
				// Szam tipusu attributum
				if(attributum.getTipus()==AttributumTipus.NUMBER) {
					if(getQuery().getNames().contains(nev+"_min")) {
						Integer ertek = Integer.parseInt(getQuery().getFirstValue(nev+"_min"));
						query.criteria("attributumok."+nev).greaterThanOrEq(ertek);
					}
					if(getQuery().getNames().contains(nev+"_max")) {
						Integer ertek = Integer.parseInt(getQuery().getFirstValue(nev+"_max"));
						query.criteria("attributumok."+nev).lessThanOrEq(ertek);
					}
				}
				
				// Lista
				if(attributum.getTipus()==AttributumTipus.SELECT_SINGLE || attributum.getTipus()==AttributumTipus.RADIO) {
					query.criteria("attributumok."+nev).in(Arrays.asList(getQuery().getValuesArray(nev)));
				}
				
				// Yes/No
				if(attributum.getTipus()==AttributumTipus.YESNO) {
					String ertek = getQuery().getFirstValue(nev);
					if("igen".equalsIgnoreCase(ertek)) {
						query.criteria("attributumok."+nev).equal(true);
					} else if("nem".equalsIgnoreCase(ertek)) {
						query.criteria("attributumok."+nev).equal(false);
					}
				}
			}
			
			
			query.offset(this.page * this.pageSize - this.pageSize);
			query.limit(Integer.parseInt(AproConfig.APP_CONFIG.getProperty("SEARCH.DEFAULT_PAGESIZE", "20")));
			
			// Rendezes
			if(this.sorrend==2) {
				// Feladas ideje szerint novekvo: Legregebbi elol
				query.order("id");
			} else if(this.sorrend==3) {
				// Ar szerint novekvo: Legolcsobb elol
				query.order("ar");
			} else if(this.sorrend==4) {
				// Ar szerint csokkeno: Legdragabb elol
				query.order("-ar");
			} else {
				// Utolso modositas ideje szerint csokkeno: Legutobb modositott elol
				query.order("-modositva");
			}
			
			System.out.println(query);
			
			// Kereses eredmenyeben levo Hirdetes objektumok feltoltese kepekkel, egyeb adatokkal a megjeleniteshez
			for(Hirdetes h : query) {
				h.getEgyebMezok().put("tipusNev", HirdetesTipus.getHirdetesTipusNev(h.getTipus()));
				
				Kategoria kat = KategoriaCache.getCacheById().get(h.getKategoriaId());
				h.getEgyebMezok().put("kategoriaNev", (kat!=null) ? KategoriaCache.getKategoriaNevChainAsLink(kat.getId()) : "");
				h.getEgyebMezok().put("kategoriaUrlNev", (kat!=null) ? kat.getUrlNev() : "");
				
				Helyseg hely = HelysegCache.getCacheById().get(h.getHelysegId());
				h.getEgyebMezok().put("helysegNev", (hely!=null) ? HelysegCache.getHelysegNevChain(hely.getId()) : "");
				h.getEgyebMezok().put("helysegUrlNev", (hely!=null) ? hely.getUrlNev() : "");
				
				h.getEgyebMezok().put("feladvaSzoveg", AproUtils.getHirdetesFeladvaSzoveg(h.getFeladasDatuma()));
				
				// Tagek
				h.getEgyebMezok().put("tag", "");
				
				if(h.getAr()==0) {
					String tag = h.getEgyebMezok().get("tag");
					if(!tag.isEmpty()) {
						tag += ";";
					}
					tag += "Ingyenes";
					h.getEgyebMezok().put("tag", tag);
				}
				// Friss tag: 3 napig
				if(h.getId().getTime()+3*24*3600*1000 > new Date().getTime()) {
					String tag = h.getEgyebMezok().get("tag");
					if(!tag.isEmpty()) {
						tag += ";";
					}
					tag += "Friss";
					h.getEgyebMezok().put("tag", tag);
				}
				// CSOK 10+10 ingatlan: Lakas vagy haz es Uj epitesu
				if(h.getAttributumok().containsKey("ingatlan-allapot")) {
					if("ujepitesu".equalsIgnoreCase((String) h.getAttributumok().get("ingatlan-allapot"))) {
						String tag = h.getEgyebMezok().get("tag");
						h.getEgyebMezok().put("tag", tag.isEmpty() ? "CSOK 10+10" : tag + ";CSOK 10+10");
					}
				}
	
				
				// Kepek
				Query<HirdetesKep> kepekQuery = datastore.createQuery(HirdetesKep.class);
				kepekQuery.criteria("hirdetesId").equal(h.getId());
				
				for(HirdetesKep kep : kepekQuery) {
					h.getKepek().add(kep);
				}
				
				// Kedvencek
				if(this.session!= null) {
					// Ha van session, akkor a feladoId letezik
					Query<HirdetesKedvenc> kedvencQuery = datastore.createQuery(HirdetesKedvenc.class);
					kedvencQuery.criteria("hirdetoId").equal(this.session.getHirdetoId());
					kedvencQuery.criteria("hirdetesId").equal(h.getId());
					
					System.out.println(kedvencQuery);
					HirdetesKedvenc kedvenc = kedvencQuery.get();
					if(kedvenc != null) {
						System.out.println("Kedvenc: " + kedvenc.getHirdetes().getCim());
						h.getEgyebMezok().put("kedvenc", "true");
					}
				}
				
				// Kategoria fuggo fontosabb attributumok a listahoz
				h.getEgyebMezok().put("attributumokString", getAttributumSzovegByKategoria(h));

				
				hirdetesList.add(h);
			}
			
			hirdetesekSzama = query.countAll();
		} catch(Exception e) {
			// Ha eddig nincs konkret hibauzenet, akkor kiirok egy altalanosat
			if(hibaUzenet==null) {
				hibaUzenet = "Sajnos hiba történt a keresés közben. Kérlek látogass vissza később, addigra javítani fogjuk!";
			}
			e.printStackTrace();
		}
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - " + KategoriaCache.getKategoriaNevekByUrlNevList(this.selectedKategoriaUrlNevListString) + " hirdetések " + HelysegCache.getHelysegNevekByUrlNevList(selectedHelysegUrlNevListString));
		appDataModel.put("description", "Sok-sok apróhirdetés " + KategoriaCache.getKategoriaNevekByUrlNevList(this.selectedKategoriaUrlNevListString) + " kategóriában. Helység, város: " + HelysegCache.getHelysegNevekByUrlNevList(selectedHelysegUrlNevListString));
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		String rssUrl = this.contextPath + "/rss/" + this.hirdetesTipusString + "/";
		rssUrl += (selectedHelysegUrlNevListString == null || selectedHelysegUrlNevListString.isEmpty()) ? "" : selectedHelysegUrlNevListString + "/";
		rssUrl += (selectedKategoriaUrlNevListString == null || selectedHelysegUrlNevListString.isEmpty()) ? "" : selectedKategoriaUrlNevListString + "/";
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", this.session);
		//dataModel.put("apikey", SessionHelper.getSession(this).getFelhasznaloApikey());
		dataModel.put("kategoriaList", KategoriaCache.getKategoriaListByParentId(null));
		dataModel.put("helysegList", HelysegCache.getHelysegListByParentId(null));
		dataModel.put("hirdetesList", hirdetesList);
		dataModel.put("hirdetesTipus", this.hirdetesTipus);
		dataModel.put("hirdetesKategoria", selectedKategoriaUrlNevList);
		dataModel.put("hirdetesHelyseg", selectedHelysegUrlNevList);
		dataModel.put("hirdetesKategoriaUrl", selectedKategoriaUrlNevListString);
		dataModel.put("hirdetesHelysegUrl", selectedHelysegUrlNevListString);
		dataModel.put("rssUrl", rssUrl);
		dataModel.put("hirdetesek_szama", hirdetesekSzama);
		dataModel.put("q", this.kulcsszo);
		dataModel.put("ar_min", this.arMin==null ? null : this.arMin.toString());
		dataModel.put("ar_max", this.arMax==null ? null : this.arMax.toString());
		dataModel.put("aktualisOldal", this.page);
		dataModel.put("osszesOldal", (hirdetesekSzama/this.pageSize)+1);
		dataModel.put("sorrend", this.sorrend);
		dataModel.put("hibaUzenet", hibaUzenet);
		if(selectedKategoriaUrlNevList.size()>0)
			dataModel.put("egyebAttributumokHtml", AproUtils.getAttributumSearchHtmlByKategoria(selectedKategoriaUrlNevList.get(0), getQuery()));
		
		// Kategoriak szerinti szinezes
		if(selectedKategoriaUrlNevList.contains("ingatlan") || 
				selectedKategoriaUrlNevList.contains("lakas") || 
				selectedKategoriaUrlNevList.contains("haz") || 
				selectedKategoriaUrlNevList.contains("epitesi-telek") || 
				selectedKategoriaUrlNevList.contains("alberlet") || 
				selectedKategoriaUrlNevList.contains("iroda-uzlethelyiseg") || 
				selectedKategoriaUrlNevList.contains("egyeb-berlemeny") || 
				selectedKategoriaUrlNevList.contains("nyaralo") || 
				selectedKategoriaUrlNevList.contains("garazs") || 
				selectedKategoriaUrlNevList.contains("szanto-kiskert") || 
				selectedKategoriaUrlNevList.contains("ingatlan-szolgaltatas")) {
			dataModel.put("ingatlanTabClass", "ingatlan-tab");
			dataModel.put("headerBgClass", "ingatlan-bg");
			dataModel.put("alkategoriaLinkek", " " + 
				getLink("lakas", "Lakás") + " | " + 
				getLink("haz", "Ház") + " | " + 
				getLink("epitesi-telek", "Építési telek") + " | " + 
				getLink("alberlet", "Albérlet") + " | " + 
				getLink("iroda-uzlethelyiseg", "Iroda, üzlethelyiség") + " | " + 
				getLink("egyeb-berlemeny", "Egyéb bérlemény") + " | " + 
				getLink("nyaralo", "Nyaraló") + " | " + 
				getLink("garazs", "Garázs") + " | " + 
				getLink("szanto-kiskert", "Szántó, kiskert") + " | " + 
				getLink("ingatlan-szolgaltatas", "Szolgáltatás"));
		} else if(selectedKategoriaUrlNevList.contains("jarmu") || 
				selectedKategoriaUrlNevList.contains("szemelyauto") || 
				selectedKategoriaUrlNevList.contains("kishaszon") || 
				selectedKategoriaUrlNevList.contains("haszonjarmu") || 
				selectedKategoriaUrlNevList.contains("potkocsi-utanfuto") || 
				selectedKategoriaUrlNevList.contains("motor") || 
				selectedKategoriaUrlNevList.contains("busz") || 
				selectedKategoriaUrlNevList.contains("lakokocsi") || 
				selectedKategoriaUrlNevList.contains("alkatresz") || 
				selectedKategoriaUrlNevList.contains("gumi-felni") || 
				selectedKategoriaUrlNevList.contains("hajo")) {
			dataModel.put("jarmuTabClass", "jarmu-tab");
			dataModel.put("headerBgClass", "jarmu-bg");
			dataModel.put("alkategoriaLinkek", " " + 
				getLink("szemelyauto", "Személyautó") + " | " + 
				getLink("kishaszon", "Kishaszon") + " | " + 
				getLink("haszonjarmu", "Haszonjármű") + " | " + 
				getLink("alkatresz", "Alkatrész") + " | " + 
				getLink("gumi-felni", "Gumi, felni") + " | " +
				getLink("potkocsi-utanfuto", "Pótkocsi, utánfutó") + " | " + 
				getLink("motor", "Motor") + " | " + 
				getLink("busz", "Busz") + " | " + 
				getLink("lakokocsi", "Lakókocsi") + " | " + 
				getLink("hajo", "Hajó"));
		} else if(selectedKategoriaUrlNevList.contains("allas") ||
				selectedKategoriaUrlNevList.contains("allas-ipari") ||
				selectedKategoriaUrlNevList.contains("kereskedelmi") ||
				selectedKategoriaUrlNevList.contains("szellemi") ||
				selectedKategoriaUrlNevList.contains("tavmunka") ||
				selectedKategoriaUrlNevList.contains("diakmunka") ||
				selectedKategoriaUrlNevList.contains("allas-egyeb") ||
				selectedKategoriaUrlNevList.contains("tanitas") ||
				selectedKategoriaUrlNevList.contains("allas-szolgaltatas") ||
				selectedKategoriaUrlNevList.contains("vallalkozas")) {
			dataModel.put("allasTabClass", "allas-tab");
			dataModel.put("headerBgClass", "allas-bg");
			dataModel.put("alkategoriaLinkek", " " + 
					getLink("allas-ipari", "Ipari") + " | " +
					getLink("kereskedelmi", "Kereskedelmi") + " | " +
					getLink("szellemi", "Szellemi") + " | " +
					getLink("tavmunka", "Távmunka") + " | " +
					getLink("diakmunka", "Diákmunka") + " | " +
					getLink("allas-egyeb", "Egyéb") + " | " +
					getLink("tanitas", "Tanítás") + " | " +
					getLink("allas-szolgaltatas", "Szolgáltatás") + " | " +
					getLink("vallalkozas", "Vállalkozás"));
		} else {
			dataModel.put("aproTabClass", "apro-tab");
			dataModel.put("headerBgClass", "apro-bg");
		}
		
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("kereses.ftl.html");
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

	/**
	 * 
	 * @param urlNev
	 * @param nev
	 * @return
	 */
	private String getLink(String urlNev, String nev) {
		return "<a href=\"" + contextPath +
				"/kereses/kinal/osszes-helyseg/" + urlNev + "/\">" + nev + "</a>";
	}
	
	/**
	 * Visszaadja a kategoria alapjan a hirdeteshez tartozo fobb atrributumok ertekeit.
	 * A listaban az ar elott jelenik meg.
	 * @param hirdetes
	 * @return
	 */
	private String getAttributumSzovegByKategoria(Hirdetes hirdetes) {
		String ret = "";
		
		String urlNev = KategoriaCache.getCacheById().get(hirdetes.getKategoriaId()).getUrlNev();
		HashMap<String, Object> a = hirdetes.getAttributumok();
		
		switch(urlNev) {
		case "lakas":
		case "haz":
		case "alberlet":
			ret += attr(urlNev, "ingatlan-alapterulet", a.get("ingatlan-alapterulet"), " m² | ");
			ret	+= attr(urlNev, "ingatlan-szobakszama", a.get("ingatlan-szobakszama"), " szoba | ");
			break;
		case "epitesi-telek":
			ret = attr(urlNev, "telek-alapterulet", a.get("telek-alapterulet"), " m² | ");
			break;
		case "szanto-kiskert":
		case "iroda-uzlethelyiseg":
		case "nyaralo":
		case "garazs":
			ret = attr(urlNev, "ingatlan-alapterulet", a.get("ingatlan-alapterulet"), " m² | ");
			break;
		case "gumi-felni":
			ret += attr(urlNev, "gumi-marka", a.get("gumi-marka"), " | ");
			ret += attr(urlNev, "gumi-szelesseg", a.get("gumi-szelesseg"), "/");
			ret += attr(urlNev, "gumi-per", a.get("gumi-per"), " ");
			ret += attr(urlNev, "gumi-atmero", a.get("gumi-atmero"), " | ");
			break;
		case "szemelyauto":
			ret += attr(urlNev, "szauto-marka", a.get("szauto-marka"), " | ");
			ret += attr(urlNev, "jarmu-evjarat", a.get("jarmu-evjarat"), " | ");
			ret += attr(urlNev, "jarmu-km", a.get("jarmu-km"), " km | ");
			break;
		case "kishaszon":
			ret += attr(urlNev, "kishaszon-marka", a.get("kishaszon-marka"), " | ");
			ret += attr(urlNev, "jarmu-evjarat", a.get("jarmu-evjarat"), " | ");
			ret += attr(urlNev, "jarmu-km", a.get("jarmu-km"), " km | ");
			break;
		case "haszonjarmu":
			ret += attr(urlNev, "haszonjarmu-marka", a.get("haszonjarmu-marka"), " | ");
			ret += attr(urlNev, "jarmu-evjarat", a.get("jarmu-evjarat"), " | ");
			ret += attr(urlNev, "jarmu-km", a.get("jarmu-km"), " km | ");
			break;
		case "busz":
			ret += attr(urlNev, "busz-marka", a.get("busz-marka"), " | ");
			ret += attr(urlNev, "jarmu-km", a.get("jarmu-km"), " km | ");
			ret += attr(urlNev, "busz-kapacitas", a.get("busz-kapacitas"), " fő | ");
			break;
		case "motor":
			ret += attr(urlNev, "motor-marka", a.get("motor-marka"), " | ");
			ret += attr(urlNev, "jarmu-evjarat", a.get("jarmu-evjarat"), " | ");
			ret += attr(urlNev, "jarmu-hengerurtartalom", a.get("jarmu-hengerurtartalom"), " cm³ | ");
			break;
		}
		
		return ret;
	}
	
	private String attr(String kategoriaUrlNev, String attributumNev, Object o, String postfix) {
		Attributum a = AttributumCache.getAttributum(kategoriaUrlNev, attributumNev);
		
		if(o==null) {
			return "";
		}
		
		if(a instanceof AttributumSelectSingle) {
			// Ha lista tipusu, akkor a nevet adjuk vissza, nem az erteket
			return a.getErtekMap().get(o.toString()) + postfix;
		} else {
			return o.toString() + postfix;
		}
	}
}
