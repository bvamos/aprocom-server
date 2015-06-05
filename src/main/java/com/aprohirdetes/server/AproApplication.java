package com.aprohirdetes.server;

import hu.u_szeged.magyarlanc.resource.ResourceHolder;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.CacheDirective;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.engine.application.Encoder;
import org.restlet.resource.Directory;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.restlet.service.TaskService;

import com.aprohirdetes.model.AttributumCache;
import com.aprohirdetes.model.HelysegCache;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.server.task.KategoriaCountTask;
import com.aprohirdetes.server.task.LejaratErtesitoTask;
import com.aprohirdetes.utils.MongoUtils;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

public class AproApplication extends Application {

	public static Configuration TPL_CONFIG;
	public static TaskService taskService = null;
	
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
		
		//router.attach("/teszt", TestServerResource.class);
		
		router.attach("/kereses/{hirdetesTipus}/", KeresesServerResource.class);
		router.attach("/kereses/{hirdetesTipus}/{helysegList}/", KeresesServerResource.class);
		router.attach("/kereses/{hirdetesTipus}/{helysegList}/{kategoriaList}/", KeresesServerResource.class);
		
		router.attach("/hirdetes/{hirdetesId}", HirdetesServerResource.class);
		router.attach("/hirdetes/{hirdetesId}/nyomtat", HirdetesNyomtatasServerResource.class);
		router.attach("/hirdetes/{hirdetesId}/torol", HirdetesTorlesServerResource.class);
		router.attach("/hirdetes/{hirdetesId}/{hirdetesCim}", HirdetesServerResource.class);
		
		router.attach("/feladas", HirdetesFeladasServerResource.class);
		router.attach("/aktivalas/{hirdetesId}", HirdetesAktivalasServerResource.class);
		router.attach("/hosszabbitas/{hirdetesId}", HirdetesHosszabbitasServerResource.class);
		
		// Felhasznaloi oldalak
		router.attach("/belepes", UserBelepesServerResource.class);
		router.attach("/kilepes", UserKilepesServerResource.class);
		router.attach("/regisztracio", UserRegisztracioServerResource.class);
		router.attach("/felhasznalo/adatlap", UserAdatlapServerResource.class);
		router.attach("/felhasznalo/hirdetesek", UserHirdeteseimServerResource.class);
		router.attach("/felhasznalo/beallitasok", UserBeallitasokServerResource.class);
		router.attach("/felhasznalo/uzenetek/{tipus}", UserUzenetekServerResource.class);
		router.attach("/felhasznalo/uzenet/{tipus}/{uzenetId}", UserUzenetServerResource.class);
		router.attach("/ujjelszo", UserUjJelszoServerResource.class);
		
		// Statikus oldalak
		router.attach("/adatkezeles", StaticAdatvedelemServerResource.class);
		router.attach("/feltetelek", StaticFelhasznalasServerResource.class);
		router.attach("/kapcsolat", StaticKapcsolatServerResource.class);
		router.attach("/sugo", StaticSugoServerResource.class);
		
		// RSS
		router.attach("/rss/{hirdetesTipus}/", RssServerResource.class);
		router.attach("/rss/{hirdetesTipus}/{helysegList}/", RssServerResource.class);
		router.attach("/rss/{hirdetesTipus}/{helysegList}/{kategoriaList}/", RssServerResource.class);
		
		// API
		router.attach("/api/v1/kepFeltoltes/{hirdetesId}", com.aprohirdetes.server.apiv1.KepFeltoltesServerResource.class);
		router.attach("/api/v1/session/belepes", com.aprohirdetes.server.apiv1.SessionBelepesServerResource.class);
		router.attach("/api/v1/session/kilepes", com.aprohirdetes.server.apiv1.SessionKilepesServerResource.class);
		router.attach("/api/v1/admin/retokenize", com.aprohirdetes.server.apiv1.AdminRetokenizeServerResource.class);
		router.attach("/api/v1/kategoriaAttributum/{kategoriaUrlNev}", com.aprohirdetes.server.apiv1.KategoriaAttributumServerResource.class);
		router.attach("/api/v1/hirdetes", com.aprohirdetes.server.apiv1.RestHirdetesekServerResource.class);
		router.attach("/api/v1/hirdetesUzenet", com.aprohirdetes.server.apiv1.HirdetesUzenetServerResource.class);
		router.attach("/api/v1/hirdeto/{hirdetoId}/apikeys", com.aprohirdetes.server.apiv1.RestHirdetoApiKeysServerResource.class);
		router.attach("/api/v1/kulcsszoLista", com.aprohirdetes.server.apiv1.RestKulcsszoListaServerResource.class);
		router.attach("/api/v1/kulcsszoLista/{prefix}", com.aprohirdetes.server.apiv1.RestKulcsszoListaServerResource.class);
		
		// ADMIN
		router.attach("/ilyennincs/", com.aprohirdetes.server.admin.FooldalServerResource.class);
		
		// Statikus konyvtarak
		String cssUri = "war:///css";
		Directory cssDirectory = new Directory(getContext(), cssUri);
		cssDirectory.setListingAllowed(true);

		// Add cache headers to the webjars so we're not swamped by requests, set things to expire in a year.
		Filter cacheCss = new Filter(getContext(), cssDirectory){
		    protected void afterHandle(Request request, Response response) {
		        super.afterHandle(request, response);
		        if (response!= null && response.getEntity() != null) {
		            if (response.getStatus().equals(Status.SUCCESS_OK)){
		                final Calendar calendar = Calendar.getInstance();
		                calendar.add(Calendar.MONTH, 1);
		                response.getEntity().setExpirationDate(calendar.getTime());
		                response.getEntity().setModificationDate(null);
		                response.setCacheDirectives(new ArrayList<CacheDirective>());
		                response.getCacheDirectives().add(CacheDirective.maxAge(2592000));
		            }
		        }
		    }
		};
		router.attach("/css", cacheCss);
		
		String jsUri = "war:///js";
		Directory jsDirectory = new Directory(getContext(), jsUri);
		jsDirectory.setListingAllowed(true);
		
		Filter cacheJs = new Filter(getContext(), jsDirectory){
		    protected void afterHandle(Request request, Response response) {
		        super.afterHandle(request, response);
		        if (response!= null && response.getEntity() != null) {
		            if (response.getStatus().equals(Status.SUCCESS_OK)){
		                final Calendar calendar = Calendar.getInstance();
		                calendar.add(Calendar.MONTH, 1);
		                response.getEntity().setExpirationDate(calendar.getTime());
		                response.getEntity().setModificationDate(null);
		                response.setCacheDirectives(new ArrayList<CacheDirective>());
		                response.getCacheDirectives().add(CacheDirective.maxAge(2592000));
		            }
		        }
		    }
		};
		router.attach("/js", cacheJs);
		
		String fontsUri = "war:///fonts";
		Directory fontsDirectory = new Directory(getContext(), fontsUri);
		fontsDirectory.setListingAllowed(true);
		router.attach("/fonts", fontsDirectory);
		
		String imagesUri = "war:///images";
		Directory imagesDirectory = new Directory(getContext(), imagesUri);
		imagesDirectory.setListingAllowed(true);
		
		Filter cacheImages = new Filter(getContext(), imagesDirectory){
		    protected void afterHandle(Request request, Response response) {
		        super.afterHandle(request, response);
		        if (response!= null && response.getEntity() != null) {
		            if (response.getStatus().equals(Status.SUCCESS_OK)){
		                final Calendar calendar = Calendar.getInstance();
		                calendar.add(Calendar.MONTH, 1);
		                response.getEntity().setExpirationDate(calendar.getTime());
		                response.getEntity().setModificationDate(null);
		                response.setCacheDirectives(new ArrayList<CacheDirective>());
		                response.getCacheDirectives().add(CacheDirective.maxAge(2592000));
		            }
		        }
		    }
		};
		router.attach("/images", cacheImages);
		
		String staticImagesUploadUri = "file://" + AproConfig.APP_CONFIG.getProperty("WORKDIR") + "/images_upload";
		Directory staticImagesUploadDirectory = new Directory(getContext(), staticImagesUploadUri);
		staticImagesUploadDirectory.setListingAllowed(true);
		router.attach("/static/images_upload", staticImagesUploadDirectory);
		
		String staticImagesUri = "file://" + AproConfig.APP_CONFIG.getProperty("WORKDIR") + "/images";
		Directory staticImagesDirectory = new Directory(getContext(), staticImagesUri);
		staticImagesDirectory.setListingAllowed(true);
		router.attach("/static/images", staticImagesDirectory);
		
		// Statikus file-ok
		router.attach("/sitemap.xml", SitemapServerResource.class);
		router.attach("/robots.txt", StaticRobotsServerResource.class);

		Encoder encoder = new Encoder(getContext(), false, true, getEncoderService());
		encoder.setNext(router);
		return encoder;
	}

	@Override
	public synchronized void start() throws Exception {
		getLogger().info("Starting application...");

		// Loading application configuration
		final String configFile = "/WEB-INF/apro.properties";
		//final String configFile = System.getProperty("configFile");
	
		getLogger().info("Loading configuration from " + configFile);
		Restlet c = getContext().getClientDispatcher();
		Request request = new Request(Method.GET, "war://" + configFile);
		request.setProtocol(Protocol.WAR);
		Response response = c.handle(request);
		
		if(response.getStatus() != Status.SUCCESS_OK) {
			getLogger().severe("ERROR: Config file not found: " + configFile);
		} else {
			try {
				AproConfig.APP_CONFIG.load(new StringReader(response.getEntityAsText()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Check application configuration
		if(AproConfig.APP_CONFIG.isEmpty()) {
			getLogger().severe("Configuration is empty. Exiting.");
			stop();
			return;
		} else {
			getLogger().info("Configuration: " + AproConfig.APP_CONFIG);
		}
		
		// Loading package configuration (Version number)
		final String packageConfigFile = "/WEB-INF/classes/package.properties";
	
		getLogger().info("Loading package configuration from " + packageConfigFile);
		Request packageRequest = new Request(Method.GET, "war://" + packageConfigFile);
		packageRequest.setProtocol(Protocol.WAR);
		response = c.handle(packageRequest);
		
		if(response.getStatus() != Status.SUCCESS_OK) {
			getLogger().severe("ERROR: Package Config file not found: " + packageConfigFile);
		} else {
			try {
				AproConfig.PACKAGE_CONFIG.load(new StringReader(response.getEntityAsText()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Create work directory structure
		createWorkDir(AproConfig.APP_CONFIG.getProperty("WORKDIR"));

		// Default configuration
		try {
			Integer.parseInt(AproConfig.APP_CONFIG.getProperty("SEARCH_DEFAULT_PAGESIZE"));
		} catch(NumberFormatException nfe) {
			AproConfig.APP_CONFIG.setProperty("SEARCH_DEFAULT_PAGESIZE", "10");
		}
		
		// Start application
		super.start();
		
		// Loading Template (Freemarker) configuration
		Configuration cfg = new Configuration();
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setTemplateLoader(new ClassTemplateLoader(getClass(), "templates/"));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setURLEscapingCharset("UTF-8");
		cfg.setBooleanFormat("Igen,Nem");
		TPL_CONFIG = cfg;
		
		// Loading Kategoriak into memory cache
		KategoriaCache.loadCache();
		
		// Loading Helysegek into memory cache
		HelysegCache.loadCache();
		
		// Betoltjuk a Kategoriakhoz tartozo Attributumokat
		getLogger().info("Loading AttributumCache");
		AttributumCache.loadAttributumCache();
		
		if(taskService == null) taskService = new TaskService(true, 2);
		
		// Hirdetesek szamanak szamolasa idozitve a hatterben
		taskService.scheduleWithFixedDelay(new KategoriaCountTask(getLogger()), 5, 600, TimeUnit.SECONDS);
		
		// Lejaro hirdetesek feladoinak ertesitese naponta egyszer
		taskService.scheduleWithFixedDelay(new LejaratErtesitoTask(getLogger()), 10, 3600, TimeUnit.SECONDS);
		
		// Tokenizer inicializacio
		ResourceHolder.initHunSplitter();
		ResourceHolder.initCorpus();
		ResourceHolder.initMSDReducer();
		ResourceHolder.initCorrDic();
	}

	@Override
	public synchronized void stop() throws Exception {
		getLogger().info("Stopping application...");

		getLogger().info("Closing MongoDB database...");
		MongoUtils.closeDB();
		
		super.stop();
	}
	
	/**
	 * Ellenorzi, hogy a munkakonyvtar letezik-e, ha nem, letrehozza
	 * 
	 * @param workDirPath Munkakonyvtar teljes eleresi utja
	 */
	private synchronized void createWorkDir(String workDirPath) {
		// Munkakonyvtar
		File workDirFile = new File(workDirPath);
		if(!workDirFile.exists() || !workDirFile.isDirectory()) {
			getLogger().warning("Work directory does not exist, create it: " + workDirPath);
			if(!workDirFile.mkdirs()) {
				getLogger().severe("ERROR while creating Work directory: " + workDirPath);
			}
		}
		
		// Konyvtar a kepeknek
		String imagesDirPath = workDirPath + File.separator + "images";
		File imagesDirFile = new File(imagesDirPath);
		if(!imagesDirFile.exists() || !imagesDirFile.isDirectory()) {
			getLogger().warning("Directory for images does not exist, create it: " + imagesDirPath);
			if(!imagesDirFile.mkdirs()) {
				getLogger().severe("ERROR while creating directory for images: " + imagesDirPath);
			}
		}
		
		// Ideiglenes konyvtar a kepek feltoltesehez
		String imagesTempDirPath = workDirPath + File.separator + "images_upload";
		File imagesTempDirFile = new File(imagesTempDirPath);
		if(!imagesTempDirFile.exists() || !imagesTempDirFile.isDirectory()) {
			getLogger().warning("Temporary directory for uploading images does not exist, create it: " + imagesTempDirPath);
			if(!imagesTempDirFile.mkdirs()) {
				getLogger().severe("ERROR while creating temporary directory for uploading images: " + imagesTempDirPath);
			}
		}
				
	}
}
