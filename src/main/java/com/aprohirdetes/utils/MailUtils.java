package com.aprohirdetes.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.aprohirdetes.model.Hirdetes;

public class MailUtils {

	public static boolean sendMail(String toAddress, String messageSubject, String messageBody) {
		boolean ret = false;
		
		// Sender's email ID needs to be mentioned
		String from = "info@aprohirdetes.com";

		// Assuming you are sending email from localhost
		String host = "localhost";

		// Setup mail server
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);

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
}
