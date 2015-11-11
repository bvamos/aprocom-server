package com.aprohirdetes.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticHtmlResource;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.model.SessionHelper;
import freemarker.template.Template;

public class StaticRolunkServerResource extends ServerResource implements StaticHtmlResource {

	private String contextPath = "";
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
	}
	
	public Representation representHtml() {
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Rólunk");
		appDataModel.put("description", "Minden, amit az Apróhirdetés.com-ról tudni érdemes");
		appDataModel.put("keywords", "felhasználás, szolgáltatások, hogy müködik");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		appDataModel.put("version", AproConfig.PACKAGE_CONFIG.getProperty("version"));
		
		dataModel.put("app", appDataModel);
		dataModel.put("session", SessionHelper.getSession(this));
		dataModel.put("kategoriaList", KategoriaCache.getKategoriaListByParentId(null));
		dataModel.put("helysegList", HelysegCache.getHelysegListByParentId(null));
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		dataModel.put("hirdetesKategoria", "ingatlan");
		dataModel.put("hirdetesHelyseg", "magyarorszag");
		
		// Without global configuration object
		//Representation indexFtl = new ClientResource(LocalReference.createClapReference(getClass().getPackage())	+ "/templates/index.ftl.html").get();
		Template indexFtl = null;
		try {
			indexFtl = AproApplication.TPL_CONFIG.getTemplate("static_rolunk.ftl.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new TemplateRepresentation(indexFtl, dataModel, MediaType.TEXT_HTML);
	}

}
