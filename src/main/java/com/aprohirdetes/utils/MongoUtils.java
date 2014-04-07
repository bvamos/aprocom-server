package com.aprohirdetes.utils;

import java.net.UnknownHostException;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.aprohirdetes.server.AproApplication;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoUtils {

	private static MongoClient MONGO_CLIENT = null;

	/**
	 * Get MongoClient Singleton
	 * @return
	 */
	public static MongoClient getMongo() {
		if (MONGO_CLIENT == null) {

			int port = 27017;
			try {
				port = Integer.parseInt(AproApplication.APP_CONFIG
						.getProperty("DB.MONGO.PORT"));
			} catch (NumberFormatException nfe) {
			}

			try {
				MONGO_CLIENT = new MongoClient(
						AproApplication.APP_CONFIG.getProperty("DB.MONGO.HOST"),
						port);
			} catch (UnknownHostException uhe) {
				uhe.printStackTrace();
				return null;
			} catch (NullPointerException npe) {
				npe.printStackTrace();
				return null;
			}

		}
		
		return MONGO_CLIENT;
	}
	
	/**
	 * Get Mongo DB object from the pool
	 * @return DB Mongo database object
	 */
	public static DB getMongoDB() {
		DB db = null;

		if (MONGO_CLIENT == null) {

			int port = 27017;
			try {
				port = Integer.parseInt(AproApplication.APP_CONFIG
						.getProperty("DB.MONGO.PORT"));
			} catch (NumberFormatException nfe) {
			}

			try {
				MONGO_CLIENT = new MongoClient(
						AproApplication.APP_CONFIG.getProperty("DB.MONGO.HOST"),
						port);
			} catch (UnknownHostException uhe) {
				uhe.printStackTrace();
				return db;
			} catch (NullPointerException npe) {
				npe.printStackTrace();
				return db;
			}

		}

		db = MONGO_CLIENT.getDB(AproApplication.APP_CONFIG
				.getProperty("DB.MONGO.DB"));

		return db;
	}

	public static void closeDB() {
		if (MONGO_CLIENT != null) {
			MONGO_CLIENT.close();
			MONGO_CLIENT = null;
		}
	}
	
	public static Datastore getDatastore() {
		return new Morphia().createDatastore(MongoUtils.getMongo(), AproApplication.APP_CONFIG.getProperty("DB.MONGO.DB"));
	}
}
