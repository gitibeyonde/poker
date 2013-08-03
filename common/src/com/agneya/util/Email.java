package com.agneya.util;


import com.golconda.db.DBPlayerUtil;

import java.net.ConnectException;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import javax.activation.FileDataSource;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import javax.mail.internet.MimeMultipart;


public class Email {
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String SMTP = "smtp";
    private static String DEFAULT_FROM = "info@blueacepoker.com";

    public static String MS_SMTP = "localhost";
    public static String MS_SMTP_HOST = "localhost";
    public static String MS_SMTP_USER = "";
    public static String MS_SMTP_PASSWORD = "";
    
    String subject, from_email, message, displayName;
    
    
    public void setSubject(String s){
        System.out.println("sub=" + s);
        subject = s;
    }
    
    public String getSubject(){
        return subject;
    }
    
    public void setFromEmail(String s){
        System.out.println("from_email=" + s);
        from_email = s;
    }
    
    public String getFromEmail(){
        return from_email;
    }
    
    public void setMessage(String s){
        System.out.println("message=" + s);
        message = s;
    }
    
    public String getMessage(){
        return message;
    }
    
    
    public void setDisplayName(String s){
        System.out.println("DisplayName=" + s);
        displayName = s;
    }
    
    public String getDisplayName(){
        return displayName;
    }
    
    
    
    public Email(){
        super();
        loadProperties();
    }

    public boolean loadProperties() {
        boolean flag = false;

        DEFAULT_FROM = "info@blueacepoker.com";
        MS_SMTP = "localhost";
        MS_SMTP_HOST = "localhost";
        MS_SMTP_USER = "";
        MS_SMTP_PASSWORD = "";

        flag = true;

        return flag;
    }


    //
    public void sendEmail(String subject, String body, String toEmail) {
      if (toEmail!= null && toEmail.endsWith("test.com"))return;
        Properties props = System.getProperties();
        System.out.println("Entered In the method ");
        props.put("localhost", "localhost");
        props.put("mail.smtp.host", "localhost");
        props.put(MAIL_SMTP_AUTH, "false");

        Session session = Session.getInstance(props);
        session.setDebug(false);

        MimeMessage message = new MimeMessage(session);
        try {
            System.out.println("To=" + toEmail + ", Sub=" + subject + ", Msg length=" + body.length());
            message.setFrom(new InternetAddress(DEFAULT_FROM));
            message.addRecipient(Message.RecipientType.TO, 
                                 new InternetAddress(toEmail));

            message.setSubject(subject);
            message.setContent(body, "text/html");

            Transport transport = session.getTransport(SMTP);
            transport.connect(MS_SMTP_HOST, MS_SMTP_USER, MS_SMTP_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Mail Sent");
        } catch (MessagingException ex) {
            ex.printStackTrace();
            System.out.println("Unable to connect to SMTP Host " + ex.getMessage());
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
    
        // use "tag" to attach picture in html
        public void sendEmailWithAttach(String subject, String body, String toEmail, String filename) {
          if (toEmail!= null && toEmail.endsWith("test.com"))return;
            Properties props = System.getProperties();
            System.out.println("Entered In the method ");
            props.put("localhost", "localhost");
            props.put("mail.smtp.host", "localhost");
            props.put(MAIL_SMTP_AUTH, "false");
    
            Session session = Session.getInstance(props);
            session.setDebug(false);
    
            MimeMessage message = new MimeMessage(session);
            try {
                System.out.println("To=" + toEmail + ", Sub=" + subject + ", Msg length=" + body.length());
                message.setFrom(new InternetAddress(DEFAULT_FROM));
                message.addRecipient(Message.RecipientType.TO, 
                                     new InternetAddress(toEmail));
                message.setSubject(subject);
                //Create the holder for our message. 
                Multipart multipart = new MimeMultipart("related");
                
                // Create the message part 
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(body, "text/html");
                multipart.addBodyPart(messageBodyPart);

                // Part two is imaget
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filename);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setDisposition(Part.ATTACHMENT);
                messageBodyPart.setHeader("Content-ID","<image>");
                multipart.addBodyPart(messageBodyPart);

                // Put parts in message
                message.setContent(multipart);
    
                Transport transport = session.getTransport(SMTP);
                transport.connect(MS_SMTP_HOST, MS_SMTP_USER, MS_SMTP_PASSWORD);
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();
                System.out.println("Mail Sent");
            } catch (MessagingException ex) {
                ex.printStackTrace();
                System.out.println("Unable to connect to SMTP Host " + ex.getMessage());
            }
            catch(Exception e){
                e.printStackTrace();
            }
    
        }
    
    public static void main(String args[]){
    	StringBuilder message=new StringBuilder();
           message.append("Hi ").append("Test").append(",")
          .append("Your Friend, ").append("Test")
          .append(", has challenged you on  our gaming website <a href='http://www.supergameasia.com'>SuperGameAsia.com</a>.")
          .append("To view the challenge you should login and goto Monitor Challenges tab.")
          .append("If you have any questions or suggestions, please do not hesitate to contact me personally at any time.")
          .append("Good Luck !!")
          .append("Customer Care Manager. (info@supergameasia.com)");
        try {
           new Email().sendEmail("Challenge from your friend !", message.toString(), "abhi@agneya.com");
        }
        catch (Exception e){
           e.printStackTrace();
        }
    }
    
}
