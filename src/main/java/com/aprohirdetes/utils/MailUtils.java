package com.aprohirdetes.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;

import com.aprohirdetes.model.Hirdetes;
import com.aprohirdetes.model.Hirdeto;
import com.aprohirdetes.model.KategoriaCache;
import com.aprohirdetes.model.Kereses;
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
			properties.put("mail.smtp.ssl.trust","*");
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
			Map<String, Object> dataModel = new HashMap<String, Object>();
			dataModel.put("content", messageBody);
			TemplateRepresentation rep = new TemplateRepresentation(AproApplication.TPL_CONFIG.getTemplate("mail_hirlevel.ftl.html"), dataModel, MediaType.TEXT_HTML);
			message.setContent(rep.getText(), "text/html; charset=utf-8");
			//message.setText(messageBody);

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
			String body = "<p>Kedves " + ho.getNev() + "!</p>\n\n"
					+ "<p>Köszönjük, hogy az Apróhirdetés.com-ot választottad!<br>\n"
					+ "A regisztrációd véglegesítéséhez kérjük kattints az alábbi linkre, vagy másold böngésződ címsorába!</p>\n\n"
					+ "<p>Aktiválás:<br>\n"
					+ "<a href=\"https://www.aprohirdetes.com/regisztracio/" + ho.getId() +"\">https://www.aprohirdetes.com/regisztracio/" + ho.getId() +"</a></p>\n\n"
					+ "<p>Üdvözlettel,<br>\n"
					+ "Apróhirdetés.com</p>";
			
			ret = MailUtils.sendMail(email, subject, body);
		}
		
		// Ertesites kuldese magamnak
		String subject = "Új felhasznalo: " + ho.getEmail();
		String body = "<p>" + ho.getNev() + "\n\n";
		
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
			String body = "<p>Kedves " + hi.getHirdeto().getNev() + "!</p>\n\n"
					+ "<p>Köszönjük, hogy az Apróhirdetés.com-ot választottad!\n";
			if(session == null) {
				// Nem belepett felhasznalokent adta fel
				body +=	"<p>Hirdetésedet sikeresen feladtad, de ahhoz, hogy megjelenjen az oldalunkon, "
					+ "kérjük kattints az alábbi linkre, vagy másold böngésződ címsorába!</p>\n\n"
					+ "<p>Aktiválás:<br>\n"
					+ "<a href=\"https://www.aprohirdetes.com/aktivalas/23afc87dd765476ad66c" + hi.getId() +"\">https://www.aprohirdetes.com/aktivalas/23afc87dd765476ad66c" + hi.getId() +"</a></p>\n\n"
					+ "<p>Tudtad, hogy ha regisztrált felhasználó vagy, akkor automatikusan aktiváljuk a hirdetést és rögtön megjelenik? \n"
					+ "Még nem késő, kattints a linkre és regisztrálj!</p>\n\n"
					+ "<p>Regisztráció:<br>\n"
					+ "<a href=\"https://www.aprohirdetes.com/regisztracio\">https://www.aprohirdetes.com/regisztracio</a></p>\n\n";
			} else {
				// Belepett felhasznalokent adta fel
				body +=	"<p>Hirdetésedet sikeresen feladtad, és ahhoz, hogy megjelenjen az oldalunkon, "
						+ "semmit sem kell tenned, mivel regisztrált felhasználónk vagy!</p>\n"
						+ "<p>Közvetlen link a hirdetéshez:<br>\n"
						+ "<a href=\"https://www.aprohirdetes.com/hirdetes/" + hi.getId() +"\">https://www.aprohirdetes.com/hirdetes/" + hi.getId() +"</a></p>\n\n";
			}
			body +=	"<p>Üdvözlettel,<br>\n"
					+ "Apróhirdetés.com";
			
			ret = MailUtils.sendMail(email, subject, body);
		}
		
		// Ertesites kuldese magamnak
		String subject = "Új hirdetés: " + hi.getCim();
		String body = "<p>" + hi.getSzoveg() + "</p>\n\n"
				+ "<ul><li>Kategória: " + KategoriaCache.getKategoriaNevChain(hi.getKategoriaId()) + "\n"
				+ "<li>Ár: " + hi.getAr() + "\n\n"
				+ "<li><a href=\"https://www.aprohirdetes.com/aktivalas/23afc87dd765476ad66c" + hi.getId() +"\">https://www.aprohirdetes.com/aktivalas/23afc87dd765476ad66c" + hi.getId() +"</a>\n\n"
				+ "<li><a href=\"https://www.aprohirdetes.com/hirdetes/" + hi.getId() +"\">https://www.aprohirdetes.com/hirdetes/" + hi.getId() +"</a>\n\n"
				+ "</ul>";
		
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
			String body = "<p>Kedves " + ho.getNev() + "!</p>\n\n"
					+ "<p>Kérésedre új jelszót generáltunk: " + jelszo + "</p>\n"
					+ "<p>Belépés: <a href=\"https://www.aprohirdetes.com/belepes\">https://www.aprohirdetes.com/belepes</a></p>\n\n"
					+ "<p>Üdvözlettel,<br>\n"
					+ "Apróhirdetés.com</p>";
			
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
			String body = "<p>Kedves Apróhirdetés.com!</p>\n\n"
					+ "<p>" + uzenet + "</p>\n\n"
					+ "<p>Üdvözlettel,<br>\n"
					+ fromNev + " (" + fromEmail + ")</p>\n";
			
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
			String body = "<p>Kedves " + hirdetes.getHirdeto().getNev() + "!</p>\n\n"
					+ "<p>" + uzenet + "</p>\n\n"
					+ "<p>Kapcsolódó hirdetés: \n"
					+ "  " + hirdetes.getCim() + "<br>\n"
					+ "  <a href=\"https://www.aprohirdetes.com/hirdetes/" + hirdetes.getId() + "\">https://www.aprohirdetes.com/hirdetes/" + hirdetes.getId() + "</a></p>\n\n"
					+ "<p>Üdvözlettel,<br>\n"
					+ fromNev + " (" + fromEmail + ")</p>\n";
			
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
		body.append("<p>Kedves " + h.getHirdeto().getNev() + "!</p>\n\n");
		body.append("<p>Az Apróhirdetés.com oldalon feladott '" + h.getCim() + "' hirdetésed 5 napon belül lejár!</p>\n\n");
		body.append("<p>A hirdetés meghosszabbításához kérjük kattints az alábbi linkre, vagy másold böngésződ címsorába! A linkre kattintva a hirdetésed automatikusan meghosszabbodik 30 nappal.<br>\n");
		body.append("<a href=\"https://www.aprohirdetes.com/hosszabbitas/" + h.getId().toString() + "23afc87dd765476ad66c\">https://www.aprohirdetes.com/hosszabbitas/" + h.getId().toString() + "23afc87dd765476ad66c</a></p>\n\n");
		body.append("<p>A hirdetés azonnali törléséhez kérjük kattints az alábbi linkre:<br>\n");
		body.append("<a href=\"https://www.aprohirdetes.com/hirdetes/" + h.getId().toString() + "/torol\">https://www.aprohirdetes.com/hirdetes/" + h.getId().toString() + "/torol</a></p>\n\n");
		body.append("<p>Ha csak meg szeretnéd tekinteni hirdetésed, kattints ide:<br>\n");
		body.append("<a href=\"https://www.aprohirdetes.com/hirdetes/" + h.getId().toString() + "\">https://www.aprohirdetes.com/hirdetes/" + h.getId().toString() + "</a></p>\n\n");
		body.append("<p>Üdvözlettel,<br>\nApróhirdetés.com</p>\n");
				
		ret = MailUtils.sendMail(email, subject, body.toString());
		
		return ret;
	}
	
	/**
	 * Ertesito level kuldese ha a hirdetes megjelenese eler egy bizonyos darabszamot
	 * 
	 * @param h Hirdetes objektum
	 * @return True, ha sikerult a levelet elkuldeni, kulonben False
	 */
	public static boolean sendMailHirdetesMegjelenes(Hirdetes h) {
		boolean ret = false;
		
		String email = h.getHirdeto().getEmail();
		String subject = "Hirdetés-emlékeztető";
		
		StringBuffer body = new StringBuffer();
		body.append("<p>Kedves " + h.getHirdeto().getNev() + "!</p>\n\n");
		body.append("<p><b>Gratulálunk! Az Apróhirdetés.com oldalon feladott '" + h.getCim() + "' hirdetésedet már " + h.getMegjelenes() + " alkalommal nézték meg!</b></p>\n\n");
		body.append("<p>Még több megjelenést szeretnél? Adunk néhány tippet:</p>\n");
		body.append("<ul><li>Oszd meg a közösségi oldalakon</li>\n");
		body.append("<li>Tölts fel annyi képet, amennyit csak tudsz</li></ul>\n");
		body.append("<p>Ha csak meg szeretnéd tekinteni hirdetésed, kattints ide:<br>\n");
		body.append("<a href=\"https://www.aprohirdetes.com/hirdetes/" + h.getId().toString() + "\">https://www.aprohirdetes.com/hirdetes/" + h.getId().toString() + "</a></p>\n\n");
		body.append("<p>Üdvözlettel,<br>\nApróhirdetés.com</p>\n");
				
		ret = MailUtils.sendMail(email, subject, body.toString());
		
		return ret;
	}
	
	/**
	 * Hirlevel kikuldese a megadott cimzettnek
	 * 
	 * @param h
	 * @param email
	 * @return
	 */
	public static boolean sendMailHirlevel(Hirdetes h, String email) {
		boolean ret = false;
		
		String subject = h.getCim();
		StringBuffer body = new StringBuffer();
		body.append("<h2>" + h.getCim() + "</h2>\n\n");
		body.append("<p>" + h.getSzoveg() + "</p>\n\n");
		body.append("<p>A hírlevél online verzióját ide kattintva olvashatod: <br>\n");
		body.append("<a href=\"https://www.aprohirdetes.com/hirdetes/" + h.getId().toString() + "\">https://www.aprohirdetes.com/hirdetes/" + h.getId().toString() + "</a></p>\n\n");
		body.append("<p>Üdvözlettel,<br>\nApróhirdetés.com</p>\n");
		
		ret = MailUtils.sendMail(email, subject, body.toString());
		
		return ret;
	}
	
	public static boolean sendMailKereses(Kereses kereses, List<Hirdetes> hirdetesList) {
		boolean ret = false;
		
		String subject = "Hirdetés figyelő: " + kereses.getNev();
		StringBuffer body = new StringBuffer();
		body.append("<p>Kedves Felhasználó!</p>\n\n");
		body.append("<p>A beállított hirdetésfigyelőd - melynek neve '" + kereses.getNev() + "' - feltételeinek az alábbi új hirdetések felelnek meg:</p>\n\n");
		body.append("<ul>\n");
		for(Hirdetes hirdetes : hirdetesList) {
			body.append("<li><a href=\"https://www.aprohirdetes.com/hirdetes/" + hirdetes.getId().toString() + "\">" + hirdetes.getCim() + "</a> - " + hirdetes.getAr() + " Ft</li>\n");
		}
		body.append("</ul>\n");
		body.append("<p>Üdvözlettel,<br>\nApróhirdetés.com</p>\n");
		
		ret = MailUtils.sendMail(kereses.getEmail(), subject, body.toString());
		
		return ret;
	}
}
