package com.agneya.util;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Mailer {
    private static Mailer instance = null;
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String SMTP = "smtp";
    private static String DEFAULT_FROM = "info@supergameasia.com";

    public static String MS_SMTP = "localhost";
    public static String MS_SMTP_HOST = "localhost";
    public static String MS_SMTP_USER = "";
    public static String MS_SMTP_PASSWORD = "";

    public Mailer() {
        super();
        loadProperties();
    }

    public boolean loadProperties() {
        boolean flag = false;

        String host = "localhost";
        DEFAULT_FROM = "info@supergameasia.com";
        MS_SMTP = host;
        MS_SMTP_HOST = host;
        MS_SMTP_USER = "";
        MS_SMTP_PASSWORD = "";

        flag = true;

        return flag;
    }

    public static Mailer getInstance() {
        if (instance == null) {
            synchronized (Mailer.class) {
                if (instance == null) {
                    instance = new Mailer();
                }
            }
        }
        return instance;
    }

    public static void sendTextEmail(String subject, String body, String from, 
                                     Vector toList) {
        synchronized (Mailer.class) {
            if (!toList.firstElement().equals("")) {
                sendEmail(subject, body, from, toList);
            }
        }
    }

    public static void sendEmail(String subject, String body, String from, 
                                 Vector toList) {
        System.out.println("Entered In the method");
        Properties props = System.getProperties();
        if (from == null || from.trim().length() == 0)
            from = DEFAULT_FROM;

        props.put(MS_SMTP, MS_SMTP_HOST);
        props.put("mail.smtp.host", MS_SMTP);
        props.put(MAIL_SMTP_AUTH, "false");

        Session session = Session.getInstance(props);
        session.setDebug(false);

        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            for (Enumeration e = toList.elements(); e.hasMoreElements(); ) {
                message.addRecipient(Message.RecipientType.TO, 
                                     new InternetAddress((String)e.nextElement()));
            }

            message.setSubject(subject);
            message.setContent(body, "text/html");

            Transport transport = session.getTransport(SMTP);
            transport.connect(MS_SMTP_HOST, MS_SMTP_USER, MS_SMTP_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Mail Sent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //
    public static void sendEmail(String subject, String body, String from, 
                                 String toEmail) {
        System.out.println("Entered In the method");
        Properties props = System.getProperties();
        if (from == null || from.trim().length() == 0)
            from = DEFAULT_FROM;

        props.put(MS_SMTP, MS_SMTP_HOST);
        props.put("mail.smtp.host", MS_SMTP);
        props.put(MAIL_SMTP_AUTH, "false");

        Session session = Session.getInstance(props);
        session.setDebug(false);

        MimeMessage message = new MimeMessage(session);
        try {
            System.out.println("To=" + toEmail + ", From=" + from + ", Msg=" + Message.RecipientType.TO);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, 
                                 new InternetAddress(toEmail));

            message.setSubject(subject);
            message.setContent(body, "text/html");

            Transport transport = session.getTransport(SMTP);
            transport.connect(MS_SMTP_HOST, MS_SMTP_USER, MS_SMTP_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Mail Sent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    
    public static void main(String args[]){
    	StringBuilder message=new StringBuilder();
           message.append("Hi ").append("Test").append(",")
          .append("<br/><br/>Your Friend, <b>").append("Test")
          .append("</b>, has challenged you on  our gaming website http://www.supergameasia.com.")
          .append("<br/>To view the challenge you should login and goto Monitor Challenges tab.")
          .append("<br/></br>If you have any questions or suggestions, please do not hesitate to contact me personally at any time.")
          .append("<br/><br/>Good Luck !!")
          .append("<br/>Customer Care Manager. <br/><u>(info@supergameasia.com)</u>")
	   .append("<br/><img src='http://www.supergameasia.com/img/supergame_logo.png' alt='http://www.supergameasia.com'/>");
        try {
           Mailer.sendEmail("SuperGame, Challenge from your friend !", message.toString(), "info@supergameasia.com", "abhi@agneya.com");
        }
        catch (Exception e){
           e.printStackTrace();
        }
    }
    
}

