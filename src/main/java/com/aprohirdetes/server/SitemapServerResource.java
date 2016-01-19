package com.aprohirdetes.server;

import java.util.ArrayList;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticXmlResource;
import com.aprohirdetes.model.KategoriaCache;

public class SitemapServerResource extends ServerResource implements StaticXmlResource {

	public Representation representXml() {
		String sitemap;
		
		String lastModDate = "2016-01-19";
		String siteMapHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" + 
				"<url>\n" + 
				"  <loc>https://www.aprohirdetes.com/</loc>\n" + 
				"  <lastmod>" + lastModDate + "</lastmod>\n" + 
				"  <changefreq>monthly</changefreq>\n" + 
				"  <priority>0.5</priority>\n" + 
				"</url>\n" + 
				"<url>\n" + 
				"  <loc>https://www.aprohirdetes.com/sugo</loc>\n" + 
				"  <lastmod>" + lastModDate + "</lastmod>\n" + 
				"  <changefreq>monthly</changefreq>\n" + 
				"  <priority>0.5</priority>\n" + 
				"</url>\n" + 
				"<url>\n" + 
				"  <loc>https://www.aprohirdetes.com/kapcsolat</loc>\n" + 
				"  <lastmod>" + lastModDate + "</lastmod>\n" + 
				"  <changefreq>yearly</changefreq>\n" + 
				"  <priority>0.4</priority>\n" + 
				"</url>\n" + 
				"<url>\n" + 
				"  <loc>https://www.aprohirdetes.com/rolunk</loc>\n" + 
				"  <lastmod>" + lastModDate + "</lastmod>\n" + 
				"  <changefreq>monthly</changefreq>\n" + 
				"  <priority>0.4</priority>\n" + 
				"</url>\n" + 
				"<url>\n" + 
				"  <loc>https://www.aprohirdetes.com/adatkezeles</loc>\n" + 
				"  <lastmod>" + lastModDate + "</lastmod>\n" + 
				"  <changefreq>yearly</changefreq>\n" + 
				"  <priority>0.4</priority>\n" + 
				"</url>\n" + 
				"<url>\n" + 
				"  <loc>https://www.aprohirdetes.com/feltetelek</loc>\n" + 
				"  <lastmod>" + lastModDate + "</lastmod>\n" + 
				"  <changefreq>yearly</changefreq>\n" + 
				"  <priority>0.4</priority>\n" + 
				"</url>\n" + 
				"<url>\n" + 
				"  <loc>https://www.aprohirdetes.com/belepes</loc>\n" + 
				"  <lastmod>" + lastModDate + "</lastmod>\n" + 
				"  <changefreq>yearly</changefreq>\n" + 
				"  <priority>0.4</priority>\n" + 
				"</url>\n" + 
				"<url>\n" + 
				"  <loc>https://www.aprohirdetes.com/regisztracio</loc>\n" + 
				"  <lastmod>" + lastModDate + "</lastmod>\n" + 
				"  <changefreq>yearly</changefreq>\n" + 
				"  <priority>0.4</priority>\n" + 
				"</url>\n" + 
				"<url>\n" + 
				"  <loc>https://www.aprohirdetes.com/ujjelszo</loc>\n" + 
				"  <lastmod>" + lastModDate + "</lastmod>\n" + 
				"  <changefreq>yearly</changefreq>\n" + 
				"  <priority>0.3</priority>\n" + 
				"</url>\n";
		String siteMapFooter = "</urlset>";
		
		ArrayList<String> hirdetesTipusList = new ArrayList<String>();
		hirdetesTipusList.add("keres");
		hirdetesTipusList.add("kinal");
		hirdetesTipusList.add("kiad");
		hirdetesTipusList.add("berel");
		
		StringBuilder sb = new StringBuilder();
		
		for(String hirdetesTipus : hirdetesTipusList) {
			/*
			for(String helyseg : HelysegCache.getCacheByUrlNev().keySet()) {
				for(String kategoria : KategoriaCache.getCacheByUrlNev().keySet()) {
					sb.append("<url>\n" + 
							"<loc>https://www.aprohirdetes.com/kereses/" + hirdetesTipus + "/" + helyseg + "/" + kategoria + "/</loc>\n" + 
							"<changefreq>always</changefreq>\n" + 
							"<priority>1</priority>\n" + 
							"</url>\n");
				}
			}*/
			// Helyseg nelkul is (Osszes helyseg)
			for(String kategoria : KategoriaCache.getCacheByUrlNev().keySet()) {
				sb.append("<url>\n" + 
						"<loc>https://www.aprohirdetes.com/kereses/" + hirdetesTipus + "/" + kategoria + "/</loc>\n" + 
						"<changefreq>always</changefreq>\n" + 
						"<priority>1</priority>\n" + 
						"</url>\n");
			}
		}
		
		sitemap = siteMapHeader + sb.toString() + siteMapFooter;
		
		return new StringRepresentation(sitemap, MediaType.APPLICATION_XML);
	}

}
