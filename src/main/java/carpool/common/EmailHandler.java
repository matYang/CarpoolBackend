package carpool.common;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class EmailHandler{
    private static final String smtpServer = "smtp.live.com";
    private static final String sender = "huaixuesheng@hotmail.com";
    private static final String password = "password11";

    /**
     * @param receiver email address of the receiver
     * @param subject
     * @param body
     * Send an email
     **/
    public static void send(String receiver,String subject, String body) throws Exception{
        try{
            Properties props = System.getProperties();
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.user", sender);
            props.put("mail.smtp.password", password);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            //props.put("mail.debug", "true");
            Session session = Session.getDefaultInstance(props, null);
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(sender));
            msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(receiver, false));
            msg.setSubject(subject);
            msg.setText(body);
            msg.setHeader("X-Mailer", "LOTONtechEmail");
            msg.setSentDate(new Date());
            Transport transport = session.getTransport("smtp");
            transport.connect(smtpServer,sender, password);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }
}