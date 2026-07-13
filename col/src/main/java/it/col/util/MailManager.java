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


import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import it.col.Data;
import it.col.bean.CodeBean;


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
        Properties credentials = Data.getCredentials();
        props.put("mailTo", mailTo);
        props.put("mail.smtp.host", credentials.getProperty("host"));
        props.put("mail.smtp.port", credentials.getProperty("port"));
        props.put("mail.smtp.auth", credentials.getProperty("auth"));
        props.put("mail.smtp.starttls.enable", credentials.getProperty("ttls"));
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
            if (groupIds[0] == 4) {
                mailTo = "lindamaria.frigo@univr.it, francesca.limberto@univr.it, giovanroberto.torre@univr.it, mauro.recchia@univr.it, teresa.dalmaso@univr.it";
            }
        }
        InternetAddress[] addresses = InternetAddress.parse(mailTo);
        String mailFrom = "convenzioniecentri@ateneo.univr.it";
        String mailContent = new String(body); 
        Properties props = System.getProperties();  // Get system properties
        Properties credentials = Data.getCredentials();
        props.put("mailTo", mailTo);
        props.put("mail.smtp.host", credentials.getProperty("host"));
        props.put("mail.smtp.port", credentials.getProperty("port"));
        props.put("mail.smtp.auth", credentials.getProperty("auth"));
        props.put("mail.smtp.starttls.enable", credentials.getProperty("ttls"));
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
     * Send e-mail with attachments    
     * using the development environment.
     * The credentials are passed by a String array, in order
     * (username, password).
     */
    public static String sendEmail(String[] credentials) throws Exception {
        String username = credentials[0];
        String password = credentials[1];
        String mailTo = "giovanroberto.torre@univr.it, giovanroberto.torre@univr.eu";
        InternetAddress[] addresses = InternetAddress.parse(mailTo);
        String mailFrom = "giovanroberto.torre@univr.eu";
        String subject = "Attestato giornata della trasparenza 2026";
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
        try {
            MimeMessage message = new MimeMessage(session); // Define message
            message.setFrom(new InternetAddress(mailFrom)); // Set the from address
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));    // Set the to address
            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject(subject, "UTF-8");
            // --- Body part (HTML) ---
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(mailContent, "text/html; charset=UTF-8");
            //message.setContent(mailContent, "text/html");   // Set the content
            // --- Attachment part ---
            MimeBodyPart attachmentPart = new MimeBodyPart();
            File file = new File("/home/trrgnr59/Temp/Attestato.pdf"); // change path/name
            DataSource source = new FileDataSource(file);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(file.getName());
            attachmentPart.setDisposition(Part.ATTACHMENT);
            // --- Combine into multipart ---
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);
            multipart.addBodyPart(attachmentPart);
            // set content and send
            message.setContent(multipart);
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
     * Send e-mail with attachments    
     * using the production environment.
     */
    public static String sendEmail() throws Exception {
        String mailFrom = "trasparenza@ateneo.univr.it";
        String subject = "Attestato giornata della trasparenza 2026";
        StringBuffer body = new StringBuffer("TEST invio attestato");
        body.append("<br><br>")
            .append("Gentile")
            .append("<br><br>")
            .append("ecco in allegato attestato di partecipazione")
            .append("<br><br>")
            .append("cordiali saluti")
            .append("<br><br>")
            .append("Direttore Generale")
            .append("<br>")
            .append("");
        ArrayList<String> recipients = new ArrayList<>();
        recipients.add("giovanroberto.torre@univr.it");
        for (String mailTo : recipients) {
            InternetAddress[] addresses = InternetAddress.parse(mailTo);
            String mailContent = new String(body); 
            Properties props = System.getProperties();  // Get system properties
            Properties credentials = Data.getCredentials();
            props.put("mailTo", mailTo);
            props.put("mail.smtp.host", credentials.getProperty("host"));
            props.put("mail.smtp.port", credentials.getProperty("port"));
            props.put("mail.smtp.auth", credentials.getProperty("auth"));
            props.put("mail.smtp.starttls.enable", credentials.getProperty("ttls"));
            Session session = Session.getInstance(props);   // Get session
            try {
                Thread.sleep(1000); // 1 second
                MimeMessage message = new MimeMessage(session); // Define message
                message.setFrom(new InternetAddress(mailFrom)); // Set the from address
                //message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));    // Set the to address
                message.setRecipients(Message.RecipientType.TO, addresses);
                message.setSubject(subject, "UTF-8");
                // --- Set the content ---
                message.setContent(mailContent, Constants.MIME_TYPE_HTML + "; charset=UTF-8");
                // --- Body part (HTML) ---
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(mailContent, "text/html; charset=UTF-8");
                // --- Attachment part ---
                MimeBodyPart attachmentPart = new MimeBodyPart();
                File file = new File("/home/gtorre/Temp/Attestato.pdf"); // change path/name
                DataSource source = new FileDataSource(file);
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(file.getName());
                attachmentPart.setDisposition(Part.ATTACHMENT);
                // --- Combine into multipart ---
                MimeMultipart multipart = new MimeMultipart();
                multipart.addBodyPart(htmlPart);
                multipart.addBodyPart(attachmentPart);
                // --- Set content ---
                message.setContent(multipart);
                // --- Send message ---              
                Transport.send(message);
            } catch (MessagingException mex) {
                String msg = "Si e\' verificato un problema nel processamento del MimeMessage.\n" + mex.getMessage();
                throw new Exception(msg, mex);
            } catch (InterruptedException ie) {
                String msg = "Si e\' verificato un problema nel processamento del thread.\n" + ie.getMessage();
                log.warning(msg);
                Thread.currentThread().interrupt();
                break;    
            } catch (Exception ex) {
                String msg = "Si e\' verificato un problema generico.\n" + ex.getMessage();
                //log.warning(msg);
                throw new Exception(msg, ex);
            }
        }
        return "Email inviate";
    }
    
    
    /**
     * Send e-mail with attachments using the production environment.
     * <p>EN:
     * It takes as input an object containing a nameSurname and an email address, 
     * and uses this data to address the message and locate a specific path 
     * from which to retrieve a document to attach to the email itself.
     * <br>IT:
     * Riceve in input un oggetto contenente un nomeCognome e un'e-mail 
     * e usa questi dati per indirizzare il messaggio e trovare un percorso 
     * dedicato in cui prelevare un documento da allegare all'e-mail stessa.</p>
     * 
     * @param   recipient   the object containing the name and the email address
     * @return  a standard  string if successful
     * @throws  Exception   if any problems arise
     */
    public static String sendEmail(CodeBean recipient) 
                            throws Exception {
        String mailFrom = "trasparenza@ateneo.univr.it";
        String subject = "Attestato giornata della trasparenza 2026";
        StringBuffer body = new StringBuffer("TEST invio attestato");
        body.append("<br><br>")
            .append("Gentile")
            .append("<br><br>")
            .append("ecco in allegato attestato di partecipazione")
            .append("<br><br>")
            .append("cordiali saluti")
            .append("<br><br>")
            .append("Direttore Generale")
            .append("<br>")
            .append("");
        ArrayList<String> recipients = new ArrayList<>();
        recipients.add("trasparenza@ateneo.univr.it");
        recipients.add(recipient.getInformativa());
        for (String mailTo : recipients) {
            InternetAddress[] addresses = InternetAddress.parse(mailTo);
            String mailContent = new String(body); 
            Properties props = System.getProperties();  // Get system properties
            Properties credentials = Data.getCredentials();
            props.put("mailTo", mailTo);
            props.put("mail.smtp.host", credentials.getProperty("host"));
            props.put("mail.smtp.port", credentials.getProperty("port"));
            props.put("mail.smtp.auth", credentials.getProperty("auth"));
            props.put("mail.smtp.starttls.enable", credentials.getProperty("ttls"));
            Session session = Session.getInstance(props);   // Get session
            try {
                Thread.sleep(1000); // 1 second
                MimeMessage message = new MimeMessage(session); // Define message
                message.setFrom(new InternetAddress(mailFrom)); // Set the from address
                //message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));    // Set the to address
                message.setRecipients(Message.RecipientType.TO, addresses);
                message.setSubject(subject, "UTF-8");
                // --- Set the content ---
                message.setContent(mailContent, Constants.MIME_TYPE_HTML + "; charset=UTF-8");
                // --- Body part (HTML) ---
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(mailContent, "text/html; charset=UTF-8");
                // --- Attachment part ---
                MimeBodyPart attachmentPart = new MimeBodyPart();
                File file = new File("/home/gtorre/Temp/Attestati/output/" + recipient.getNome() + "/Attestato.pdf"); // change path/name
                DataSource source = new FileDataSource(file);
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(file.getName());
                attachmentPart.setDisposition(Part.ATTACHMENT);
                // --- Combine into multipart ---
                MimeMultipart multipart = new MimeMultipart();
                multipart.addBodyPart(htmlPart);
                multipart.addBodyPart(attachmentPart);
                // --- Set content ---
                message.setContent(multipart);
                // --- Send message ---              
                Transport.send(message);
            } catch (MessagingException mex) {
                String msg = "Si e\' verificato un problema nel processamento del MimeMessage.\n" + mex.getMessage();
                throw new Exception(msg, mex);
            } catch (InterruptedException ie) {
                String msg = "Si e\' verificato un problema nel processamento del thread.\n" + ie.getMessage();
                log.warning(msg);
                Thread.currentThread().interrupt();
                break;    
            } catch (Exception ex) {
                String msg = "Si e\' verificato un problema generico.\n" + ex.getMessage();
                //log.warning(msg);
                throw new Exception(msg, ex);
            }
        }
        return "Email inviate";
    }
    
    
    /**
     * Send e-mail with attachments using the production environment.
     * <p>EN:
     * It takes as input an object containing a nameSurname and an email address, 
     * and uses this data to address the message and locate a specific path 
     * from which to retrieve a document to attach to the email itself.
     * <br>IT:
     * Riceve in input un oggetto contenente un nomeCognome e un'e-mail 
     * e usa questi dati per indirizzare il messaggio e trovare un percorso 
     * dedicato in cui prelevare un documento da allegare all'e-mail stessa.</p>
     * 
     * @param   recipient   the object containing the name and the email address
     * @return  a standard  string if successful
     * @throws  Exception   if any problems arise
     */
    public static String sendEmail(ArrayList<CodeBean> recipients) 
                            throws Exception {
        String mailFrom = "trasparenza@ateneo.univr.it";
        String subject = "Attestato di partecipazione e registrazione dell’evento – Giornata della Trasparenza delle Università del Veneto 2026";
        StringBuffer body = new StringBuffer("Gentile Partecipante,");
        body.append("<br><br>")
            .append("desideriamo ringraziarLa per aver preso parte alla ")
            .append("<strong>Giornata della Trasparenza delle Università del Veneto 2026</strong>.")
            .append("<br><br>")
            .append("In allegato alla presente trova il ")
            .append("<strong>Suo attestato di partecipazione</strong>.")
            .append("<br><br>")
            .append("&Egrave; inoltre possibile rivedere l’evento attraverso la registrazione disponibile al seguente link:")
            .append("<br>")
            .append("<a href='https://youtu.be/mQkLMz7r9fc?si=uLZSMbWKQd6ifn1q'>https://youtu.be/mQkLMz7r9fc?si=uLZSMbWKQd6ifn1q</a>")
            .append("<br><br>")
            .append("Ci auguriamo che i contenuti proposti siano stati di interesse e possano rappresentare un utile spunto di approfondimento e confronto per le attività future.")
            .append("<br><br>")
            .append("RingraziandoLa nuovamente per la partecipazione e l’attenzione dedicata all’iniziativa, porgiamo i nostri pi&uacute; cordiali saluti.")
            .append("<br><br>")
            .append("Servizio Anticorruzione, Trasparenza e Accesso agli atti")
            .append("<br>")
            .append("Ufficio di Staff Area Legale, Trasparenza e Privacy")
            .append("<br>")
            .append("Direzione Generale")
            .append("<br>")
            .append("Universit&agrave; degli Studi di Verona")
            .append("<br>");
        ArrayList<String> emails = new ArrayList<>();
        emails.add("trasparenza@ateneo.univr.it");
        for (CodeBean recipient : recipients) {
            //String mailTo = "trasparenza@ateneo.univr.it, " + recipient.getInformativa();
            String mailTo = recipient.getInformativa();
            //String mailCc  = "cc1@example.com, cc2@example.com";
            String mailBcc = "trasparenza@ateneo.univr.it";
            InternetAddress[] addresses = InternetAddress.parse(mailTo);
            //InternetAddress[] ccAddresses  = InternetAddress.parse(mailCc);
            InternetAddress[] bccAddresses = InternetAddress.parse(mailBcc);
            String mailContent = new String(body); 
            Properties props = System.getProperties();  // Get system properties
            Properties credentials = Data.getCredentials();
            props.put("mailTo", mailTo);
            props.put("mail.smtp.host", credentials.getProperty("host"));
            props.put("mail.smtp.port", credentials.getProperty("port"));
            props.put("mail.smtp.auth", credentials.getProperty("auth"));
            props.put("mail.smtp.starttls.enable", credentials.getProperty("ttls"));
            Session session = Session.getInstance(props);   // Get session
            try {
                Thread.sleep(1000); // 1 second
                MimeMessage message = new MimeMessage(session); // Define message
                message.setFrom(new InternetAddress(mailFrom)); // Set the from address
                //message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));    // Set the to address
                message.setRecipients(Message.RecipientType.TO, addresses);
                //message.setRecipients(Message.RecipientType.CC, ccAddresses);
                message.setRecipients(Message.RecipientType.BCC, bccAddresses);
                message.setSubject(subject, "UTF-8");
                // --- Set the content ---
                message.setContent(mailContent, Constants.MIME_TYPE_HTML + "; charset=UTF-8");
                // --- Body part (HTML) ---
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(mailContent, "text/html; charset=UTF-8");
                // --- Attachment part ---
                MimeBodyPart attachmentPart = new MimeBodyPart();
                File file = new File("/home/gtorre/Temp/Progetti/Attestati/output/" + recipient.getNome() + "/Attestato.pdf"); // change path/name
                DataSource source = new FileDataSource(file);
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(file.getName());
                attachmentPart.setDisposition(Part.ATTACHMENT);
                // --- Combine into multipart ---
                MimeMultipart multipart = new MimeMultipart();
                multipart.addBodyPart(htmlPart);
                multipart.addBodyPart(attachmentPart);
                // --- Set content ---
                message.setContent(multipart);
                // --- Send message ---              
                Transport.send(message);
            } catch (MessagingException mex) {
                String msg = "Si e\' verificato un problema nel processamento del MimeMessage.\n" + mex.getMessage();
                throw new Exception(msg, mex);
            } catch (InterruptedException ie) {
                String msg = "Si e\' verificato un problema nel processamento del thread.\n" + ie.getMessage();
                log.warning(msg);
                Thread.currentThread().interrupt();
                break;    
            } catch (Exception ex) {
                String msg = "Si e\' verificato un problema generico.\n" + ex.getMessage();
                //log.warning(msg);
                throw new Exception(msg, ex);
            }
        }
        return "Email inviate";
    }
    
}
