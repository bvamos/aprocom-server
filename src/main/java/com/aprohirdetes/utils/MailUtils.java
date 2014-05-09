package com.aprohirdetes.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
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

		// Setup mail server
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		
		// SMTP authentication
		if (AproApplication.APP_CONFIG.getProperty("MAIL.USER") != null) {
			properties.setProperty("mail.user", AproApplication.APP_CONFIG.getProperty("MAIL.USER"));
			properties.setProperty("mail.password", AproApplication.APP_CONFIG.getProperty("MAIL.PASSWORD")==null ? "" : AproApplication.APP_CONFIG.getProperty("MAIL.PASSWORD"));
		}

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					toAddress));

			// Set Subject: header field
			message.setSubject(messageSubject);

			// Send the actual HTML message, as big as you like
			message.setContent(messageBody, "text/html");

			// Send message
			Transport.send(message);
			System.out.println("Level elkuldve. Cimzett: " + toAddress);
			ret = true;
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
		
		return ret;
	}
	
	public static boolean sendMailHirdetesFeladva(Hirdetes hi) {
		boolean ret = false;
		
		String email = hi.getHirdeto().getEmail();
		if(email != null && !email.isEmpty()) {
			String subject = "Hirdetés feladva: " + hi.getCim();
			String body = "Hirdetését sikeresen feladta. Azonosító: " + hi.getId();
			
			ret = MailUtils.sendMail(email, subject, body);
		}
		return ret;
	}
	
	public static boolean sendMailUjJelszo(Hirdeto ho, String jelszo) {
		boolean ret = false;
		
		String email = ho.getEmail();
		if(email != null && !email.isEmpty()) {
			String subject = "Új jelszót generáltunk";
			String body = "Kérésére új jelszót generáltunk: " + jelszo;
			
			ret = MailUtils.sendMail(email, subject, body);
		}
		return ret;
	}
}
