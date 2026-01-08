/*
 *   Convenzioni On Line (COL-GeCo). 
 *   Applicazione web: 
 *   - per la visualizzazione delle convenzioni attivate dall'ateneo, 
 *   - per la gestione delle convenzioni della pubblica amministrazione, 
 *   - per ottenere notifiche riguardo le scadenze ed i rinnovi
 *   - e per effettuare il monitoraggio delle attività legate alle convenzioni.
 *
 *   Agreements Mapping and Management Software (COL-GeCo).
 *   Web application: 
 *   - for viewing conventions activated by the university,
 *   - for the management of public administration conventions,
 *   - to obtain notifications regarding deadlines and renewals
 *   - and to carry out monitoring of activities related to agreements..
 *   
 *   Copyright (C) 2025-2026 Giovanroberto Torre
 *   all right reserved
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, see <https://www.gnu.org/licenses/>.
 *
 *   Giovanroberto Torre <gianroberto.torre@gmail.com>
 *   Universita' degli Studi di Verona
 *   Via Dell'Artigliere, 8
 *   37129 Verona (Italy)
 */

package it.col.util;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * <p>This class contains methods for sending emails.</p> 
 * 
 * @author trrgnr59
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class MailManager {

    
    /**
     * <p>Sends an email message using the provided JavaMail Session.
     * This method creates a MimeMessage using the given session, sets the recipient, subject,
     * and body content, then sends the email via Transport.send(). 
     * It assumes the session is properly configured 
     * for SMTP authentication and connection.</p>
     * 
     * @param session the JavaMail Session configured with SMTP server details and authentication
     * @param toEmail the recipient email address to which the message will be sent
     * @param subject the subject line of the email message
     * @param body the main content or body text of the email message
    */
    public static void sendEmail(Session session, 
                                 String toEmail, 
                                 String subject, 
                                 String body) {
        try {
            MimeMessage msg = new MimeMessage(session);
            // Set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("gianroberto.torre@gmail.com", "Team Leader"));

            msg.setReplyTo(InternetAddress.parse("no_reply@example.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            System.out.println("Message is ready");
            Transport.send(msg);

            System.out.println("EMail Sent Successfully!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**

     */
    public static String sendEmail() throws Exception {
        //String mailTo = "albertomaria.arenaagostino@univr.it";
        //String mailTo = "lindamaria.frigo@univr.it, giovanni.olivieri@univr.it, elisa.puddu@univr.it, francesca.limberto@univr.it";
        String mailTo = "giovanroberto.torre@univr.it";
        InternetAddress[] addresses = InternetAddress.parse(mailTo);
        String mailFrom = "giovanroberto.torre@univr.it";
        String subject = "Richiesta";
        StringBuffer content = new StringBuffer("TEST riepilogo scadenze");
        content.append("<br><br>")
               .append("prova")
               .append("<br><br>")
               .append("prova")
               .append("<br><br>")
               .append("prova")
               .append("<br><br>")
               .append("prova")
               .append("<br>")
               .append("firma");
        String mailContent = new String(content); 
        Properties props = System.getProperties();  // Get system properties
        props.put("mailTo", mailTo);
        props.put("mail.smtp.host", "smtp.univr.it");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");
        Session session = Session.getInstance(props);   // Get session
        try {
            MimeMessage message = new MimeMessage(session); // Define message
            message.setFrom(new InternetAddress(mailFrom)); // Set the from address
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));    // Set the to address
            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject(subject);
            message.setContent(mailContent, "text/html");   // Set the content
            Transport.send(message);                    // Send message
        } catch (MessagingException mex) {
            String msg = "Si e\' verificato un problema nel processamento del MimeMessage.\n" + mex.getMessage();
            //log.warning(msg);
            throw new Exception(msg, mex);
        } catch (Exception ex) {
            String msg = "Si e\' verificato un problema generico.\n" + ex.getMessage();
            //log.warning(msg);
            throw new Exception(msg, ex);
        }

        return "Email inviata";
    }

    
    /**

     */
    public static String sendEmail(String body) throws Exception {
        String mailTo = "lindamaria.frigo@univr.it, giovanni.olivieri@univr.it, elisa.puddu@univr.it, francesca.limberto@univr.it, giovanroberto.torre@univr.it";
        //String mailTo = "giovanroberto.torre@univr.it";
        InternetAddress[] addresses = InternetAddress.parse(mailTo);
        String mailFrom = "convenzioniecentri@ateneo.univr.it";
        String subject = "Richiesta";
        String mailContent = new String(body); 
        Properties props = System.getProperties();  // Get system properties
        props.put("mailTo", mailTo);
        props.put("mail.smtp.host", "smtp.univr.it");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");
        Session session = Session.getInstance(props);   // Get session
        try {
            MimeMessage message = new MimeMessage(session); // Define message
            message.setFrom(new InternetAddress(mailFrom)); // Set the from address
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));    // Set the to address
            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject(subject, "UTF-8");
            //message.setContent(mailContent, "text/html");   // Set the content
            message.setContent(mailContent, Constants.MIME_TYPE_HTML + "; charset=UTF-8");
            Transport.send(message);                    // Send message
        } catch (MessagingException mex) {
            String msg = "Si e\' verificato un problema nel processamento del messaggio.\n" + mex.getMessage();
            //log.warning(msg);
            throw new Exception(msg, mex);
        } catch (Exception ex) {
            String msg = "Si e\' verificato un problema generico.\n" + ex.getMessage();
            //log.warning(msg);
            throw new Exception(msg, ex);
        }
        return "Email inviata";
    }
    
    
    /**

     */
    public static String sendEmail(String body, String username, String password) 
            throws Exception {
        //String mailTo = "albertomaria.arenaagostino@univr.it";
        //String mailTo = "lindamaria.frigo@univr.it, giovanni.olivieri@univr.it, elisa.puddu@univr.it, francesca.limberto@univr.it, giovanroberto.torre@univr.it";
        String mailTo = "giovanroberto.torre@univr.it";
        InternetAddress[] addresses = InternetAddress.parse(mailTo);
        String mailFrom = "giovanroberto.torre@univr.eu";
        String subject = "Richiesta";
        String mailContent = new String(body); 
        Properties props = System.getProperties();  // Get system properties
        props.put("mailTo", mailTo);
        props.put("mail.smtp.host", "mail.smtp2go.com");
        props.put("mail.smtp.port", "587"); // TLS port recommended
        props.put("mail.smtp.auth", "true"); // SMTP2GO requires authentication
        props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS for encryption
        props.put("mail.smtp.connectiontimeout", "10000"); // 10 seconds
        props.put("mail.smtp.timeout", "10000"); // 10 seconds
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(username, password);
            }
        });
        
        try {
            MimeMessage message = new MimeMessage(session); // Define message
            message.setFrom(new InternetAddress(mailFrom)); // Set the from address
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));    // Set the to address
            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject(subject, "UTF-8");
            //message.setContent(mailContent, "text/html");   // Set the content
            message.setContent(mailContent, Constants.MIME_TYPE_HTML + "; charset=UTF-8");
            Transport.send(message);                    // Send message
        } catch (MessagingException mex) {
            String msg = "Si e\' verificato un problema nel processamento del messaggio.\n" + mex.getMessage();
            //log.warning(msg);
            throw new Exception(msg, mex);
        } catch (Exception ex) {
            String msg = "Si e\' verificato un problema generico.\n" + ex.getMessage();
            //log.warning(msg);
            throw new Exception(msg, ex);
        }

        return "Email inviata";
    }
    
}
