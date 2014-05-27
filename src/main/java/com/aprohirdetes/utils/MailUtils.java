package com.aprohirdetes.utils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.server.AproApplication;

public class MailUtils {

	/**
	 * Levelkuldes megadott cimzettnek
	 * 
	 * @param toAddress
	 * @param messageSubject
	 * @param messageBody
	 * @return
	 */
	public static boolean sendMail(String toAddress, String messageSubject, String messageBody) {
		boolean ret = false;
		
		String host = AproApplication.APP_CONFIG.getProperty("MAIL.SMTP.HOST");
		String port = AproApplication.APP_CONFIG.getProperty("MAIL.SMTP.PORT");
		String from = AproApplication.APP_CONFIG.getProperty("MAIL.FROM");
		final String user = AproApplication.APP_CONFIG.getProperty("MAIL.USER");
		final String pass = AproApplication.APP_CONFIG.getProperty("MAIL.PASSWORD");
		
		// From
		if(from == null || from.isEmpty()) {
			from =  "info@aprohirdetes.com";
		}

		// Host:Port
		if(host == null || host.isEmpty()) {
			host = "localhost";
		}
		if(port == null || port.isEmpty()) {
			port = "25";
		}

		// Setup mail server
		Session session = null;
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.port", port);
		
		// SMTP authentication
		if (user != null && !user.isEmpty()) {
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable","true");
			properties.put("mail.user", user);
			properties.put("mail.password", pass);
			
			session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, pass);
				}
			});
		} else {
			properties.put("mail.smtp.auth", "false");
			session = Session.getDefaultInstance(properties);
		}

		session.setDebug(true);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from, "Apróhirdetés.com", "utf-8"));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					toAddress));

			// Set Subject: header field
			message.setSubject(messageSubject);

			// Send the actual HTML message, as big as you like
			//message.setContent(messageBody, "text/html");
			message.setText(messageBody);

			// Send message
			Transport.send(message);
			ret = true;
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * Visszaigazolo level kuldese aktivalo linkkel a hirdetesben megadott email cimre.
	 * Plusz ertesito level nekunk.
	 * 
	 * @param hi Feladott Hirdetes
	 * @return True, ha sikerult a levelet elkuldeni, kulonben False
	 */
	public static boolean sendMailHirdetesFeladva(Hirdetes hi) {
		boolean ret = false;
		
		// Aktivalo level kuldese a megadott email cimre
		String email = hi.getHirdeto().getEmail();
		if(email != null && !email.isEmpty()) {
			String subject = "Hirdetés feladva: " + hi.getCim();
			String body = "Kedves " + hi.getHirdeto().getNev() + "!\n\n"
					+ "Köszönjük, hogy az Apróhirdetés.com-ot választotta!\n"
					+ "Hirdetését sikeresen feladta, de ahhoz, hogy megjelenjen az oldalunkon,"
					+ "kérjük kattintson az alábbi linkre, vagy másolja böngészője címsorába!\n"
					+ "Aktiválás:\n"
					+ "https://www.aprohirdetes.com/aktivalas/23afc87dd765476ad66c" + hi.getId() +"\n\n"
					+ "Üdvözlettel,\n"
					+ "Apróhirdetés.com";
			
			ret = MailUtils.sendMail(email, subject, body);
		}
		
		// Ertesites kuldese magamnak
		String subject = "Uj hirdetes: " + hi.getCim();
		String body = "https://www.aprohirdetes.com/aktivalas/23afc87dd765476ad66c" + hi.getId() +"\n\n"
				+ "https://www.aprohirdetes.com/hirdetes/" + hi.getId() +"/TEST\n\n";
		MailUtils.sendMail(AproApplication.APP_CONFIG.getProperty("MAIL.FROM"), subject, body);
		
		return ret;
	}
	
	public static boolean sendMailUjJelszo(Hirdeto ho, String jelszo) {
		boolean ret = false;
		
		String email = ho.getEmail();
		if(email != null && !email.isEmpty()) {
			String subject = "Új jelszót generáltunk";
			String body = "Kedves " + ho.getNev() + "!\n\n"
					+ "Kérésére új jelszót generáltunk: " + jelszo + "\n"
					+ "Belépés: https://www.aprohirdetes.com/belepes\n\n"
					+ "Üdvözlettel,\n"
					+ "Apróhirdetés.com";
			
			ret = MailUtils.sendMail(email, subject, body);
		}
		return ret;
	}
}
