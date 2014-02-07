package com.aprohirdetes.server;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.RootResource;

public class RootServerResource extends ServerResource implements RootResource {

	public String representText() {
		return getApplication().getName();
	}

	public Representation representHtml() {
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("content", getApplication().getName());
		Representation mailFtl = new ClientResource(LocalReference.createClapReference(getClass().getPackage())	+ "/templates/index.ftl.html").get();
		
		return new TemplateRepresentation(mailFtl, dataModel, MediaType.TEXT_HTML);
	}

}
