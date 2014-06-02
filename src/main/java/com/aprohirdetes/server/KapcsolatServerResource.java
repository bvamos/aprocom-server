package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
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
import com.aprohirdetes.model.Helyseg;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.HirdetoHelper;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.utils.AproUtils;
import com.aprohirdetes.utils.MailUtils;

import freemarker.template.Template;

public class KapcsolatServerResource extends ServerResource implements FormResource {

	private String contextPath = "";
	private Session session;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		session = AproUtils.getSession(this);
	}
	
	public Representation representHtml() {
		ArrayList<Kategoria> kategoriaList = KategoriaCache.getKategoriaListByParentId(null);
		for(Kategoria o : kategoriaList) {
			ArrayList<Kategoria> alkategoriak = KategoriaCache.getKategoriaListByParentId(o.getIdAsString());
			o.setAlkategoriaList(alkategoriak);
		}
		
		ArrayList<Helyseg> helysegList = HelysegCache.getHelysegListByParentId(null);
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Kapcsolat");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproApplication.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", AproUtils.getSession(this));
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("hirdetesKategoria", "ingatlan");
		dataModel.put("hirdetesHelyseg", "magyarorszag");
		
		if(this.session != null) {
			dataModel.put("hirdeto", HirdetoHelper.load(session.getHirdetoId()));
		}
		
		// Without global configuration object
		//Representation indexFtl = new ClientResource(LocalReference.createClapReference(getClass().getPackage())	+ "/templates/index.ftl.html").get();
		Template indexFtl = null;
		try {
			indexFtl = AproApplication.TPL_CONFIG.getTemplate("kapcsolat.ftl.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new TemplateRepresentation(indexFtl, dataModel, MediaType.TEXT_HTML);
	}

	public Representation accept(Form form) throws IOException {
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("kapcsolat.ftl.html");
		String uzenet = "Köszönjük az üzeneted, hamarosan válaszolni fogunk rá!";
		String hibaUzenet = null;
		
		String feladoNev = form.getFirstValue("hirdetoNev");
		String feladoEmail = form.getFirstValue("hirdetoEmail");
		String feladoUzenet = form.getFirstValue("uzenet");
		
		if(!MailUtils.sendMailKapcsolat(feladoNev, feladoEmail, feladoUzenet)) {
			getLogger().severe("Hiba a Kapcsolat level kikuldese kozben.");
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
		appDataModel.put("htmlTitle", getApplication().getName() + " - Kapcsolat");
		appDataModel.put("description", "Bármilyen problémával ill. kérdéssel fordulj nyugodtan ügyfélszolgálatunkhoz!");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproApplication.PACKAGE_CONFIG.getProperty("version"));
		
		if(this.session != null) {
			dataModel.put("hirdeto", HirdetoHelper.load(session.getHirdetoId()));
		}
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", this.session);
		dataModel.put("uzenet", uzenet);
		dataModel.put("hibaUzenet", hibaUzenet);
		dataModel.put("kategoriaList", kategoriaList);
		dataModel.put("helysegList", helysegList);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("hirdetesKategoria", "ingatlan");
		dataModel.put("hirdetesHelyseg", "magyarorszag");
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}
}
