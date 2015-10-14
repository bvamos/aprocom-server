package com.aprohirdetes.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Utils {

	private Utils() {
	}

	/**
	 * HTTP Get hivas. Visszadja a response body-t
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	public static String httpGet(String urlString) throws Exception {
		URL url = new URL(urlString);
		HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		String responseText = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String input;
		while ((input = br.readLine()) != null){
			responseText += input;
		}
		br.close();
		
		return responseText;
	}
}
