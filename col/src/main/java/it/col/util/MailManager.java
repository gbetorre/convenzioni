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
import java.util.logging.Logger;

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

import it.col.Data;


/**
 * <p>This class contains methods for sending emails.</p> 
 * 
 * @author trrgnr59
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class MailManager {

    /**
     * All logging goes through this logger.
     */
    private static Logger log = Logger.getLogger(MailManager.class.getName());
      
    
    /**
     * Sends an HTML email via University SMTP server (no authentication).
     * 
     * <p><strong>Configuration (Production):</strong></p>
     * <ul>
     * <li><strong>SMTP Host:</strong> <code>smtp.univr.it:25</code> (no TLS/auth)</li>
     * <li><strong>To:</strong> Multiple university recipients (comma-separated)</li>
     * <li><strong>From:</strong> <code>convenzioniecentri@ateneo.univr.it</code> (official)</li>
     * <li><strong>Subject:</strong> <code>param</code></li>
     * </ul>
     * 
     * <p><strong>Recipients (hardcoded):</strong></p>
     * <pre>
     * lindamaria.frigo@univr.it
     * giovanni.olivieri@univr.it  
     * elisa.puddu@univr.it
     * francesca.limberto@univr.it
     * giovanroberto.torre@univr.it
     * </pre>
     * 
     * <p><strong>Usage:</strong> Production environment default (no credentials needed).
     * {@code MailManager.sendEmail(subject, body);}
     * 
     * <p><strong>Security Note:</strong> Port 25, no auth/TLS - internal university network only.</p>
     * 
     * @param subject the subject shown on the e-mail
     * @param body the HTML email content to send
     * @return success message "Email inviata" (Email sent)
     * @throws Exception wraps {@link MessagingException} with Italian error message
     */
    public static String sendEmail(String subject, 
                                   String body) 
                            throws Exception {
        String mailTo = "lindamaria.frigo@univr.it, giovanni.olivieri@univr.it, elisa.puddu@univr.it, francesca.limberto@univr.it, giovanroberto.torre@univr.it";
        //String mailTo = "giovanroberto.torre@univr.it";
        InternetAddress[] addresses = InternetAddress.parse(mailTo);
        String mailFrom = "convenzioniecentri@ateneo.univr.it";
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
     * Send an HTML email with the body content as the message and 
     * the username and password passed as parameters as the login credentials.
     * 
     * <p><strong>Configuration (Test):</strong></p>
     * <ul>
     * <li><strong>SMTP Host:</strong> <code>mail.smtp2go.com:587</code> (STARTTLS)</li>
     * <li><strong>To:</strong> (hardcoded)</li>
     * <li><strong>From:</strong> <code>giovanroberto.torre@univr.eu</code></li>
     * <li><strong>Subject:</strong> <code>"Richiesta"</code></li>
     * <li><strong>Content:</strong> HTML body with UTF-8 charset</li>
     * </ul>
     * 
     * <p><strong>Timeouts:</strong> 10 seconds for connection and socket operations.</p>
     * 
     * <p><strong>Usage in servlet:</strong></p>
     * {@code
     * MailManager.sendEmail(body, "smtp2go_username", "smtp2go_api_key");
     * }
     * 
     * @param body the HTML email content to send
     * @param username SMTP2GO username (email address)
     * @param password SMTP2GO password or API key
     * @return success message "Email inviata" (Email sent)
     * @throws Exception wraps {@link MessagingException} with Italian error message for servlet error handling
     */
    
    public static String sendEmail(String body, String username, String password) 
            throws Exception {
        //String mailTo = "albertomaria.arenaagostino@univr.it";
        //String mailTo = "lindamaria.frigo@univr.it, giovanni.olivieri@univr.it, elisa.puddu@univr.it, francesca.limberto@univr.it, giovanroberto.torre@univr.it";
        String mailTo = "giovanroberto.torre@univr.it";
        InternetAddress[] addresses = InternetAddress.parse(mailTo);
        String mailFrom = "giovanroberto.torre@univr.eu";
        String subject = "Riepilogo del " + Utils.format(Utils.getCurrentDate()) + " [TEST]";
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
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(username, password);
            }
        });
        // Make the message
        try {
            MimeMessage message = new MimeMessage(session); // Define message
            message.setFrom(new InternetAddress(mailFrom)); // Set the from address
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));    // Set the to address
            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject(subject, "UTF-8");
            message.setContent(mailContent, Constants.MIME_TYPE_HTML + "; charset=UTF-8");  // Set the content
            Transport.send(message);                    // Send message
        } catch (MessagingException mex) {
            String msg = "Si e\' verificato un problema nel processamento del messaggio.\n" + mex.getMessage();
            log.warning(msg);
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
    public static String sendEmail(int[] groupIds,
                                   String subject,
                                   String body) 
                            throws Exception {
        String mailTo = null;
        // TODO: there is a naming here, this gotta be optimized dinamically, after 
        if (groupIds.length == 1) {
            if (groupIds[0] == 1) {
                mailTo = "lindamaria.frigo@univr.it, giovanni.olivieri@univr.it, elisa.puddu@univr.it, francesca.limberto@univr.it, giovanroberto.torre@univr.it";
            } else if (groupIds[0] == 3) {
                mailTo = "mauro.recchia@univr.it, teresa.dalmaso@univr.it, giovanroberto.torre@univr.it";
            }
        } else if (groupIds.length > 1) {
            mailTo = "elisa.silvestri@univr.it, giovanroberto.torre@univr.it";
        }
        InternetAddress[] addresses = InternetAddress.parse(mailTo);
        String mailFrom = "convenzioniecentri@ateneo.univr.it";
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
    
}
