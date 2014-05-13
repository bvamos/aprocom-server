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

	public static boolean sendMail(String toAddress, String messageSubject, String messageBody) {
		boolean ret = false;
		
		// Sender's email ID needs to be mentioned
		String from = AproApplication.APP_CONFIG.getProperty("MAIL.FROM") != null ? AproApplication.APP_CONFIG.getProperty("MAIL.FROM") : "info@aprohirdetes.com";

		// Assuming you are sending email from localhost
		String host = AproApplication.APP_CONFIG.getProperty("MAIL.SMTP.HOST") != null ? AproApplication.APP_CONFIG.getProperty("MAIL.SMTP.HOST") : "localhost";
		String port = AproApplication.APP_CONFIG.getProperty("MAIL.SMTP.PORT") != null ? AproApplication.APP_CONFIG.getProperty("MAIL.SMTP.PORT") : "25";

		// Setup mail server
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.port", port);
		
		// SMTP authentication
		if (AproApplication.APP_CONFIG.getProperty("MAIL.USER") != null) {
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable","true");
			properties.put("mail.user", AproApplication.APP_CONFIG.getProperty("MAIL.USER"));
			properties.put("mail.password", AproApplication.APP_CONFIG.getProperty("MAIL.PASSWORD")==null ? "" : AproApplication.APP_CONFIG.getProperty("MAIL.PASSWORD"));
		}

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(AproApplication.APP_CONFIG.getProperty("MAIL.USER"), AproApplication.APP_CONFIG.getProperty("MAIL.PASSWORD"));
			}
		});
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
			System.out.println("Level elkuldve. Cimzett: " + toAddress);
			ret = true;
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static boolean sendMailHirdetesFeladva(Hirdetes hi) {
		boolean ret = false;
		
		String email = hi.getHirdeto().getEmail();
		if(email != null && !email.isEmpty()) {
			String subject = "Hirdetés feladva: " + hi.getCim();
			String body = "Kedves " + hi.getHirdeto().getNev() + "!\n\n"
					+ "Köszönjük, hogy az Apróhirdetés.com-ot választotta!\n"
					+ "Hirdetését sikeresen feladta, de ahhoz, hogy megjelenjen az oldalunkon,"
					+ "kérjük kattintson az alábbi linkre, vagy másolja böngészője címsorába!\n"
					+ "Aktiválás:\n"
					+ "http://www.aprohirdetes.com/aktivalas/23afc87dd765476ad66c" + hi.getId() +"\n\n"
					+ "Üdvözlettel,\n"
					+ "Apróhirdetés.com";
			
			ret = MailUtils.sendMail(email, subject, body);
		}
		return ret;
	}
	
	public static boolean sendMailUjJelszo(Hirdeto ho, String jelszo) {
		boolean ret = false;
		
		String email = ho.getEmail();
		if(email != null && !email.isEmpty()) {
			String subject = "Új jelszót generáltunk";
			String body = "Kedves " + ho.getNev() + "!\n\n"
					+ "Kérésére új jelszót generáltunk: " + jelszo + "\n"
					+ "Belépés: http://www.aprohirdetes.com/belepes\n\n"
					+ "Üdvözlettel,\n"
					+ "Apróhirdetés.com";
			
			ret = MailUtils.sendMail(email, subject, body);
		}
		return ret;
	}
}
