package com.aprohirdetes.server.apiv1;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticHtmlResource;
import com.aprohirdetes.utils.AproUtils;

public class KategoriaAttributumServerResource extends ServerResource implements
		StaticHtmlResource {

	private String kategoriaUrlNev;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();

		kategoriaUrlNev = (String) this.getRequestAttributes().get("kategoriaUrlNev");
	}
	
	@Override
	public Representation representHtml() {
		return new StringRepresentation(AproUtils.getAttributumHtmlByKategoria(kategoriaUrlNev));
	}

}
