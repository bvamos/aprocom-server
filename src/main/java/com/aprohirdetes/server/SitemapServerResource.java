package com.aprohirdetes.server;

import java.util.ArrayList;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;

import com.aprohirdetes.common.StaticXmlResource;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.KategoriaCache;

public class SitemapServerResource extends ServerResource implements StaticXmlResource {

	public Representation representHtml() {
		String sitemap = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" + 
				"   <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/</loc>\n" + 
				"      <lastmod>2014-05-26</lastmod>\n" + 
				"      <changefreq>monthly</changefreq>\n" + 
				"      <priority>0.5</priority>\n" + 
				"   </url>\n" + 
				"   <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/sugo</loc>\n" + 
				"      <lastmod>2014-05-26</lastmod>\n" + 
				"      <changefreq>monthly</changefreq>\n" + 
				"      <priority>0.5</priority>\n" + 
				"   </url>\n" + 
				"   <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kapcsolat</loc>\n" + 
				"      <lastmod>2014-05-26</lastmod>\n" + 
				"      <changefreq>yearly</changefreq>\n" + 
				"      <priority>0.4</priority>\n" + 
				"   </url>\n" + 
				"   <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/adatkezeles</loc>\n" + 
				"      <lastmod>2014-05-26</lastmod>\n" + 
				"      <changefreq>yearly</changefreq>\n" + 
				"      <priority>0.4</priority>\n" + 
				"   </url>\n" + 
				"   <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/belepes</loc>\n" + 
				"      <lastmod>2014-05-26</lastmod>\n" + 
				"      <changefreq>yearly</changefreq>\n" + 
				"      <priority>0.4</priority>\n" + 
				"   </url>\n" + 
				"   <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/regisztracio</loc>\n" + 
				"      <lastmod>2014-05-26</lastmod>\n" + 
				"      <changefreq>yearly</changefreq>\n" + 
				"      <priority>0.4</priority>\n" + 
				"   </url>\n" + 
				"   <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/ujjelszo</loc>\n" + 
				"      <lastmod>2014-05-26</lastmod>\n" + 
				"      <changefreq>yearly</changefreq>\n" + 
				"      <priority>0.3</priority>\n" + 
				"   </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/bazar/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/gepek/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/egyeb-berlemeny/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/nyaralo/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/lakberendezes/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/mutargy/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/szellemi/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/tavmunka/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/audio/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/baba-mama/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/motor/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/ingatlan-szolgaltatas/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/ingatlan/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/lakas/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/szamitastechnika/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/allas/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/jarmu/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/elekronika/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/allat-noveny/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/szabadido/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/haz/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/garazs/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/vegyes/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/video/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/tablet/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/ideiglenes/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/allas-ipari/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/oktatas/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/alberlet/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/iroda-uzlethelyiseg/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/telek-szanto-kiskert/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/konyv-cd-dvd/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/epotianyag/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/ingyen-elviheto/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/tanitas/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/diakmunka/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/kereskedelmi/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/vallalkozas/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/allas-egyeb/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/szoftver/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/internet/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/munkagep/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/haziallat/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/haszonallat/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/hangszer/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/fenykepezes/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/haszonjarmu/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/oktatas-egyeb/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/nyaralas/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/tanfolyam/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/sportszer/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/telefon/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/alkatresz/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/haztartasi-gep/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/szemelyauto/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/alkatresz/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/noveny/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/eletmod/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/ado-1-szazaleka/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/valentin-nap/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/farsang/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/karacsony/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/szilveszter/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/allas-szolgaltatas/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/korrepetalas/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/szamitastechnika-szolgaltatas/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/jatekkonzol/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/szerszam/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"    <url>\n" + 
				"      <loc>https://www.aprohirdetes.com/kereses/kinal/magyarorszag/kerekpar/</loc>\n" + 
				"      <changefreq>always</changefreq>\n" + 
				"      <priority>1</priority>\n" + 
				"    </url>\n" + 
				"</urlset>";
		
		String lastModDate = "2014-06-05";
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
				"  <loc>https://www.aprohirdetes.com/adatkezeles</loc>\n" + 
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
		
		StringBuilder sb = new StringBuilder();
		
		for(String hirdetesTipus : hirdetesTipusList) {
			for(String helyseg : HelysegCache.getCacheByUrlNev().keySet()) {
				for(String kategoria : KategoriaCache.getCacheByUrlNev().keySet()) {
					sb.append("<url>\n" + 
							"<loc>https://www.aprohirdetes.com/kereses/" + hirdetesTipus + "/" + helyseg + "/" + kategoria + "/</loc>\n" + 
							"<changefreq>always</changefreq>\n" + 
							"<priority>1</priority>\n" + 
							"</url>\n");
				}
			}
		}
		
		sitemap = siteMapHeader + sb.toString() + siteMapFooter;
		
		return new StringRepresentation(sitemap, MediaType.APPLICATION_XML);
	}

}
