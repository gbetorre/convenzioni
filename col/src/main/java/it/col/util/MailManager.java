/**
 * 
 */
package it.col.util;


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
 * @author trrgnr59
 *
 */
public class MailManager {


    /**
     * Utility method to send simple HTML email
     * @param session
     * @param toEmail
     * @param subject
     * @param body
     */
    public static void sendEmail(Session session, String toEmail, String subject, String body) {
        try {
          MimeMessage msg = new MimeMessage(session);
          //set message headers
          msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
          msg.addHeader("format", "flowed");
          msg.addHeader("Content-Transfer-Encoding", "8bit");

          msg.setFrom(new InternetAddress("gianroberto.torre@gmail.com", "Direttore Generale"));

          msg.setReplyTo(InternetAddress.parse("no_reply@example.com", false));

          msg.setSubject(subject, "UTF-8");

          msg.setText(body, "UTF-8");

          msg.setSentDate(new Date());

          msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
          System.out.println("Message is ready");
          Transport.send(msg);  

          System.out.println("EMail Sent Successfully!!");
        }
        catch (Exception e) {
          e.printStackTrace();
        }
    }
    
    
    /**

     */
    public static String sendEmail() throws Exception {
        //String mailTo = "albertomaria.arenaagostino@univr.it";
        String mailTo = "giovanroberto.torre@univr.it";
        String mailFrom = "albertomaria.arenaagostino@univr.it";
        String subject = "Richiesta";
        StringBuffer content = new StringBuffer("Egregio Dr. Ing., certe volte parlo con me stesso.");
        content.append("<br><br>")
               .append("ma sono cos&iacute; intelligente")
               .append("<br><br>")
               .append("che non mi capisco")
               .append("<br><br>")
               .append("Oscar Wilde")
               .append("<br><br>")
               .append("Distinti saluti")
               .append("<br>")
               .append("dirgt");
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
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));    // Set the to address
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

     *
    public static boolean sendEmail() throws Exception {
        boolean sent = false;
        //String mailTo = "albertomaria.arenaagostino@univr.it";
        String mailTo = "giovanroberto.torre@univr.it";
        String mailFrom = "giovanroberto.torre@univr.eu";
        String subject = "Richiesta";
        StringBuffer content = new StringBuffer("Egregio Dr., certe volte parlo con me stesso.");
        content.append("<br><br>")
               .append("ma sono così intelligente")
               .append("<br><br>")
               .append("che non mi capisco")
               .append("<br><br>")
               .append("Oscar Wilde")
               .append("<br><br>")
               .append("Distinti saluti")
               .append("<br>")
               .append("drg");
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
            InternetAddress[] recipients = InternetAddress.parse(mailTo);
            for (InternetAddress recipient : recipients) {
              recipient.validate();
            }
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));    // Set the to address
            message.setRecipients(Message.RecipientType.TO, recipients);
            message.setSubject(subject);
            message.setContent(mailContent, "text/html");   // Set the content
            Transport.send(message);                    // Send message
            sent = true;
        } catch (SendFailedException sfe) {
            Address[] invalidAddresses = sfe.getInvalidAddresses();
            if (invalidAddresses != null) {
                String msg = "Invalid address: ";
                for (Address addr : invalidAddresses) {
                    msg += addr.toString() + " ";
                }
                throw new Exception(msg, sfe);
            }
            Address[] validUnsent = sfe.getValidUnsentAddresses();
            if (validUnsent != null) {
                String msg = "Valid unsent addresses:";
                for (Address addr : validUnsent) {
                    msg += addr.toString();
                }
                throw new Exception(msg, sfe);
            }
            Address[] validSent = sfe.getValidSentAddresses();
            if (validSent != null) {
                String msg = "Valid sent addresses:";
                for (Address addr : validSent) {
                    msg += addr.toString();
                }
                throw new Exception(msg, sfe);
            }
            throw new Exception(sfe);
        } catch (MessagingException mex) {
            String msg = "Si e\' verificato un problema nel processamento del MimeMessage.\n" + mex.getMessage();
            //log.warning(msg);
            throw new Exception(msg, mex);
        } catch (Exception ex) {
            String msg = "Si e\' verificato un problema generico.\n" + ex.getMessage();
            //log.warning(msg);
            throw new Exception(msg, ex);
        }

        return sent;
    }*/

    
}
