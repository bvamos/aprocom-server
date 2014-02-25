package com.aprohirdetes.server;

import java.io.StringReader;
import java.util.Properties;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

public class AproApplication extends Application {

	public static Properties APP_CONFIG = new Properties();
	public static Configuration TPL_CONFIG;
	
	public AproApplication() {
		getLogger().info("Initializing application...");

		setName("Apróhirdetés.com");
		setDescription("Apróhirdetés.com - Ingyenes apróhirdető portál");
		setOwner("Vámos Balázs");
		setAuthor("Vámos Balázs");
	}

	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());

		router.attach("/", RootServerResource.class);
		
		router.attach("/kereses/{hirdetesTipus}/{kategoriaList}/", KeresesServerResource.class);
		router.attach("/kereses/{hirdetesTipus}/{helysegList}/{kategoriaList}/", KeresesServerResource.class);
		
		String cssUri = "war:///css";
		Directory cssDirectory = new Directory(getContext(), cssUri);
		cssDirectory.setListingAllowed(true);
		router.attach("/css", cssDirectory);
		
		String jsUri = "war:///js";
		Directory jsDirectory = new Directory(getContext(), jsUri);
		jsDirectory.setListingAllowed(true);
		router.attach("/js", jsDirectory);
		
		String fontsUri = "war:///fonts";
		Directory fontsDirectory = new Directory(getContext(), fontsUri);
		fontsDirectory.setListingAllowed(true);
		router.attach("/fonts", fontsDirectory);
		
		String imagesUri = "war:///images";
		Directory imagesDirectory = new Directory(getContext(), imagesUri);
		imagesDirectory.setListingAllowed(true);
		router.attach("/images", imagesDirectory);

		return router;
	}

	@Override
	public synchronized void start() throws Exception {
		getLogger().info("Starting application...");

		super.start();
		
		// Loading application configuration
		final String configFile = "/WEB-INF/apro.properties";
	
		getLogger().info("Loading configuration from " + configFile);
		Restlet c = getContext().getClientDispatcher();
		Request request = new Request(Method.GET, "war://" + configFile);
		request.setProtocol(Protocol.WAR);
		Response response = c.handle(request);
		
		if(response.getStatus() != Status.SUCCESS_OK) {
			getLogger().severe("ERROR: Config file not found: " + configFile);
			stop();
			return;
		}
		
		APP_CONFIG.load(new StringReader(response.getEntityAsText()));
		
		// Check application configuration
		try {
			Integer.parseInt(APP_CONFIG.getProperty("SEARCH_DEFAULT_PAGESIZE"));
		} catch(NumberFormatException nfe) {
			APP_CONFIG.setProperty("SEARCH_DEFAULT_PAGESIZE", "10");
		}
		System.out.println(APP_CONFIG);
		
		// Loading Template (Freemarker) configuration
		Configuration cfg = new Configuration();
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setClassForTemplateLoading(getClass(), "templates/");
		//cfg.setDirectoryForTemplateLoading(new File("C:\\Users\\bvamos\\Documents\\GitHub\\aprocom-server\\src\\main\\java\\com\\aprohirdetes\\server\\templates\\"));
		cfg.setDefaultEncoding("UTF-8");
		TPL_CONFIG = cfg;
		
		// Loading Kategoriak into memory cache
		KategoriaCache.loadCache();
		
		// Loading Helysegek into memory cache
		HelysegCache.loadCache();
	}

	@Override
	public synchronized void stop() throws Exception {
		getLogger().info("Stopping application...");

		getLogger().info("Closing MongoDB database...");
		MongoUtils.closeDB();
		
		super.stop();
	}
}
