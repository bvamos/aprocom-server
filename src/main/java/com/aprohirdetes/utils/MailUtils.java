package com.aprohirdetes.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.server.AproApplication;
import com.aprohirdetes.server.AproConfig;

public class MailUtils {

	/**
	 * Ez egy utility class, ezert nem engedem a peldanyositast
	 */
	private MailUtils() {};
	
	// TODO: Exception tovabb dobasa
	/**
	 * Levelkuldes megadott cimzettnek
	 * 
	 * @param toAddress
	 * @param messageSubject
	 * @param messageBody
	 * @return
	 */
	public static boolean sendMail(String toAddress, String messageSubject, String messageBody, String fromAddress) {
		boolean ret = false;
		
		if("true".equalsIgnoreCase(AproConfig.APP_CONFIG.getProperty("MAIL.DISABLED"))) {
			AproApplication.getCurrent().getLogger().warning("Levelkuldes kikapcsolva");
			return ret;
		}
		
		String host = AproConfig.APP_CONFIG.getProperty("MAIL.SMTP.HOST");
		String port = AproConfig.APP_CONFIG.getProperty("MAIL.SMTP.PORT");
		String from = (fromAddress!=null) ? fromAddress : AproConfig.APP_CONFIG.getProperty("MAIL.FROM");
		final String user = AproConfig.APP_CONFIG.getProperty("MAIL.USER");
		final String pass = AproConfig.APP_CONFIG.getProperty("MAIL.PASSWORD");
		final boolean debug = Boolean.parseBoolean(AproConfig.APP_CONFIG.getProperty("MAIL.DEBUG"));
		
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

		session.setDebug(debug);

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
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static boolean sendMail(String toAddress, String messageSubject, String messageBody) {
		return sendMail(toAddress, messageSubject, messageBody, null);
	}
	
	/**
	 * Visszaigazolo level kuldese aktivalo linkkel a regisztracioban megadott email cimre.
	 * Plusz ertesito level nekunk.
	 * 
	 * @param hi Feladott Hirdetes
	 * @return True, ha sikerult a levelet elkuldeni, kulonben False
	 */
	public static boolean sendMailRegisztracio(Hirdeto ho) {
		boolean ret = false;
		
		// Aktivalo level kuldese a megadott email cimre
		String email = ho.getEmail();
		if(email != null && !email.isEmpty()) {
			String subject = "Regisztráció aktiválása";
			String body = "Kedves " + ho.getNev() + "!\n\n"
					+ "Köszönjük, hogy az Apróhirdetés.com-ot választottad!\n";
				body +=	"A regisztrációd véglegesítéséhez "
					+ "kérjük kattints az alábbi linkre, vagy másold böngésződ címsorába!\n\n"
					+ "Aktiválás:\n"
					+ "https://www.aprohirdetes.com/regisztracio/" + ho.getId() +"\n\n"
					;
			body +=	"Üdvözlettel,\n"
					+ "Apróhirdetés.com";
			
			ret = MailUtils.sendMail(email, subject, body);
		}
		
		// Ertesites kuldese magamnak
		String subject = "Új felhasznalo: " + ho.getEmail();
		String body = ho.getNev() + "\n\n";
		MailUtils.sendMail(AproConfig.APP_CONFIG.getProperty("MAIL.FROM"), subject, body);
		
		return ret;
	}
	
	/**
	 * Visszaigazolo level kuldese aktivalo linkkel a hirdetesben megadott email cimre.
	 * Plusz ertesito level nekunk.
	 * 
	 * @param hi Feladott Hirdetes
	 * @return True, ha sikerult a levelet elkuldeni, kulonben False
	 */
	public static boolean sendMailHirdetesFeladva(Hirdetes hi, com.aprohirdetes.model.Session session) {
		boolean ret = false;
		
		// Aktivalo level kuldese a megadott email cimre
		String email = hi.getHirdeto().getEmail();
		if(email != null && !email.isEmpty()) {
			String subject = "Hirdetés feladva: " + hi.getCim();
			String body = "Kedves " + hi.getHirdeto().getNev() + "!\n\n"
					+ "Köszönjük, hogy az Apróhirdetés.com-ot választottad!\n";
			if(session == null) {
				// Nem belepett felhasznalokent adta fel
				body +=	"Hirdetésedet sikeresen feladtad, de ahhoz, hogy megjelenjen az oldalunkon, "
					+ "kérjük kattints az alábbi linkre, vagy másold böngésződ címsorába!\n\n"
					+ "Aktiválás:\n"
					+ "https://www.aprohirdetes.com/aktivalas/23afc87dd765476ad66c" + hi.getId() +"\n\n"
					+ "Tudtad, hogy ha regisztrált felhasználó vagy, akkor automatikusan aktiváljuk a hirdetést és rögtön megjelenik? \n"
					+ "Még nem késő, kattints a linkre és regisztrálj!\n\n"
					+ "Regisztráció:\n"
					+ "https://www.aprohirdetes.com/regisztracio" + hi.getId() +"\n\n";
			} else {
				// Belepett felhasznalokent adta fel
				body +=	"Hirdetésedet sikeresen feladtad, és ahhoz, hogy megjelenjen az oldalunkon, "
						+ "semmit sem kell tenned, mivel regisztrált felhasználónk vagy!\n"
						+ "Közvetlen link a hirdetéshez:\n"
						+ "https://www.aprohirdetes.com/hirdetes/" + hi.getId() +"\n\n";
			}
			body +=	"Üdvözlettel,\n"
					+ "Apróhirdetés.com";
			
			ret = MailUtils.sendMail(email, subject, body);
		}
		
		// Ertesites kuldese magamnak
		String subject = "Új hirdetés: " + hi.getCim();
		String body = hi.getSzoveg() + "\n\n"
				+ "Kategória: " + KategoriaCache.getKategoriaNevChain(hi.getKategoriaId()) + "\n"
				+ "Ár: " + hi.getAr() + "\n\n"
				+ "https://www.aprohirdetes.com/aktivalas/23afc87dd765476ad66c" + hi.getId() +"\n\n"
				+ "https://www.aprohirdetes.com/hirdetes/" + hi.getId() +"\n\n";
		MailUtils.sendMail(AproConfig.APP_CONFIG.getProperty("MAIL.FROM"), subject, body);
		
		return ret;
	}
	
	/**
	 * Elfelejtett jelszo kikuldese emailben
	 * @param ho Hirdeto
	 * @param jelszo
	 * @return
	 */
	public static boolean sendMailUjJelszo(Hirdeto ho, String jelszo) {
		boolean ret = false;
		
		String email = ho.getEmail();
		if(email != null && !email.isEmpty()) {
			String subject = "Új jelszót generáltunk";
			String body = "Kedves " + ho.getNev() + "!\n\n"
					+ "Kérésedre új jelszót generáltunk: " + jelszo + "\n"
					+ "Belépés: https://www.aprohirdetes.com/belepes\n\n"
					+ "Üdvözlettel,\n"
					+ "Apróhirdetés.com";
			
			ret = MailUtils.sendMail(email, subject, body);
		}
		return ret;
	}
	
	/**
	 * Level kuldese a kapcsolat formrol
	 * @param fromNev
	 * @param fromEmail
	 * @param uzenet
	 * @return
	 */
	public static boolean sendMailKapcsolat(String fromNev, String fromEmail, String uzenet) {
		boolean ret = false;
		
		String to = AproConfig.APP_CONFIG.getProperty("MAIL.FROM");
		
		if(fromEmail != null && !fromEmail.isEmpty()) {
			String subject = "Üzenet a weboldalról";
			String body = "Kedves Apróhirdetés.com!\n\n"
					+ uzenet + "\n\n"
					+ "Üdvözlettel,\n"
					+ fromNev + " (" + fromEmail + ")\n";
			
			ret = MailUtils.sendMail(to, subject, body, fromEmail);
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param hirdetes
	 * @param fromNev
	 * @param fromEmail
	 * @param uzenet
	 * @return
	 */
	public static boolean sendMailHirdeto(Hirdetes hirdetes, String fromNev, String fromEmail, String uzenet) {
		boolean ret = false;
		
		String to = hirdetes.getHirdeto().getEmail();
		
		if(fromEmail != null && !fromEmail.isEmpty()) {
			String subject = "Üzenet: " + hirdetes.getCim();
			String body = "Kedves " + hirdetes.getHirdeto().getNev() + "!\n\n"
					+ uzenet + "\n\n"
					+ "Kapcsolódó hirdetés: \n"
					+ "  " + hirdetes.getCim() + "\n"
					+ "  https://www.aprohirdetes.com/hirdetes/" + hirdetes.getId() + "\n\n"
					+ "Üdvözlettel,\n"
					+ fromNev + " (" + fromEmail + ")\n";
			
			ret = MailUtils.sendMail(to, subject, body, fromEmail);
		}
		
		return ret;
	}
	
	/**
	 * Ertesito level kuldese a hirdetes feladojanak, ha a hirdetes 5 napon belul lejar
	 * 
	 * @param h Hirdetes objektum
	 * @return True, ha sikerult a levelet elkuldeni, kulonben False
	 */
	public static boolean sendMailHirdetesLejar(Hirdetes h) {
		boolean ret = false;
		
		String email = h.getHirdeto().getEmail();
		String subject = "Értesítés lejáró hirdetésről";
		
		StringBuffer body = new StringBuffer();
		body.append("Kedves " + h.getHirdeto().getNev() + "!\n\n");
		body.append("Az Apróhirdetés.com oldalon feladott '" + h.getCim() + "' hirdetésed 5 napon belül lejár!\n\n");
		body.append("A hirdetés meghosszabbításához kérjük kattints az alábbi linkre, vagy másold böngésződ címsorába! A linkre kattintva a hirdetésed automatikusan meghosszabbodik 30 nappal.\n");
		body.append("https://www.aprohirdetes.com/hosszabbitas/" + h.getId().toString() + "23afc87dd765476ad66c\n\n");
		body.append("Ha csak meg szeretnéd tekinteni hirdetésed, kattints ide:\n");
		body.append("https://www.aprohirdetes.com/hirdetes/" + h.getId().toString() + "\n\n");
		body.append("Üdvözlettel,\nApróhirdetés.com\n");
				
		ret = MailUtils.sendMail(email, subject, body.toString());
		
		return ret;
	}
}
