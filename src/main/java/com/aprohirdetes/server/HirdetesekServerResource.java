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

import com.aprohirdetes.common.HirdetesResource;
import com.aprohirdetes.model.HirdetesTipus;
import com.aprohirdetes.model.Session;
import com.aprohirdetes.utils.AproUtils;
import freemarker.template.Template;

public class HirdetesekServerResource extends ServerResource implements
		HirdetesResource {

	private String contextPath = "";
	private Session session = null;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		ServletContext sc = (ServletContext) getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
		contextPath = sc.getContextPath();
		
		this.session = AproUtils.getSession(this);
	}

	@Override
	public Representation representHtml() throws IOException {
		Template ftl = AproApplication.TPL_CONFIG.getTemplate("hirdetesek.ftl.html");
		
		// Adatmodell a Freemarker sablonhoz
		Map<String, Object> dataModel = new HashMap<String, Object>();
		
		Map<String, String> appDataModel = new HashMap<String, String>();
		appDataModel.put("contextRoot", this.contextPath);
		appDataModel.put("htmlTitle", getApplication().getName() + " - Hirdetéseim");
		appDataModel.put("datum", new SimpleDateFormat("yyyy. MMMM d. EEEE", new Locale("hu")).format(new Date()));
		
		dataModel.put("app", appDataModel);
		dataModel.put("hirdetesTipus", HirdetesTipus.KINAL);
		
		if(this.session == null) {
			ftl = AproApplication.TPL_CONFIG.getTemplate("forbidden.ftl.html");
		} else {
			dataModel.put("session", this.session);
		}
		
		return new TemplateRepresentation(ftl, dataModel, MediaType.TEXT_HTML);
	}

}
