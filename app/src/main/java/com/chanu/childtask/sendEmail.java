package com.chanu.childtask;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class sendEmail {

    boolean isSent = false;

    public sendEmail(String stringReceiverEmail, String title, String body) {
        try {
            String stringSenderEmail = "noreply.childmanagement@gmail.com";
            //String stringReceiverEmail = "tchanu210@gmail.com";
            String stringPasswordSenderEmail = "rlrvorhmedssehmr";

            String stringHost = "smtp.gmail.com";

            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail); //super.getPasswordAuthentication();
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);


            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(stringReceiverEmail));

            mimeMessage.setSubject(title);//"TEST Mail"
            mimeMessage.setText(body);//"Hello all\n\n Thank you."

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            this.isSent = true;
            thread.start();


        } catch (AddressException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    boolean getIsSent(){
        return isSent;
    }
}
