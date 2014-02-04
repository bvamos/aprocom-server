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
import org.restlet.routing.Router;

import com.aprohirdetes.model.Kategoria;
import com.aprohirdetes.utils.MongoUtils;

public class AproApplication extends Application {

	public static Properties CONFIG = new Properties();
	
	public AproApplication() {
		getLogger().info("Initializing application...");

		setName("Aprohirdetes.com");
		setDescription("Aprohirdetes.com - Ingyenes apróhirdető portál");
		setOwner("Vámos Balázs");
		setAuthor("Vámos Balázs");
	}

	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());

		router.attach("/", RootServerResource.class);

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
		
		CONFIG.load(new StringReader(response.getEntityAsText()));
		System.out.println(CONFIG);
		
		// Loading Kategoriak into memory cache
		Kategoria.loadCache();
	}

	@Override
	public synchronized void stop() throws Exception {
		getLogger().info("Stopping application...");

		getLogger().info("Closing MongoDB database...");
		MongoUtils.closeDB();
		
		super.stop();
	}
}
