package com.aprohirdetes.utils;

import java.net.UnknownHostException;
import com.aprohirdetes.server.AproApplication;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoUtils {

	private static MongoClient MONGO_CLIENT = null;

	public static DB getMongoDB() {
		DB db = null;

		if (MONGO_CLIENT == null) {

			int port = 27017;
			try {
				port = Integer.parseInt(AproApplication.CONFIG
						.getProperty("DB.MONGO.PORT"));
			} catch (NumberFormatException nfe) {
			}

			try {
				MONGO_CLIENT = new MongoClient(
						AproApplication.CONFIG.getProperty("DB.MONGO.HOST"),
						port);
			} catch (UnknownHostException uhe) {
				uhe.printStackTrace();
				return db;
			} catch (NullPointerException npe) {
				npe.printStackTrace();
				return db;
			}

		}

		db = MONGO_CLIENT.getDB(AproApplication.CONFIG
				.getProperty("DB.MONGO.DB"));

		return db;
	}

	public static void closeDB() {
		if (MONGO_CLIENT != null) {
			MONGO_CLIENT.close();
			MONGO_CLIENT = null;
		}
	}
}
