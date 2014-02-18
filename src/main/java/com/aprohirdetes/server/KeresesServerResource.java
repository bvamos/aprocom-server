package com.aprohirdetes.server;

import java.util.LinkedList;
import java.util.List;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.KeresesResource;
import com.aprohirdetes.model.Helyseg;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.model.KategoriaCache;

public class KeresesServerResource extends ServerResource implements
		KeresesResource {

	private String kategoriaUrlNevList = null;
	private List<Kategoria> kategoriaList = new LinkedList<Kategoria>();
	private String helysegUrlNevList = null;
	private List<Helyseg> helysegList = new LinkedList<Helyseg>();
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		this.kategoriaUrlNevList = (String) this.getRequestAttributes().get("kategoriaList");
		System.out.println(kategoriaUrlNevList);
		this.kategoriaList = KategoriaCache.getKategoriaListByUrlNevList(this.kategoriaUrlNevList);
		
		this.helysegUrlNevList = (String) this.getRequestAttributes().get("helysegList");
		if(this.helysegUrlNevList == null || this.helysegUrlNevList.isEmpty()) {
			this.helysegUrlNevList = "magyarorszag";
		}
		this.helysegList = HelysegCache.getHelysegListByUrlNevList(this.helysegUrlNevList);
	}

	public Representation representHtml() {
		Representation rep = new StringRepresentation(kategoriaUrlNevList);
		
		for(Kategoria kat : kategoriaList) {
			System.out.println(kat.toString());
		}
		
		for(Helyseg kat : helysegList) {
			System.out.println(kat.toString());
		}
		
		
		return rep;
	}

}
