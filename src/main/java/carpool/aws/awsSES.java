package carpool.aws;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import carpool.common.DebugLog;
import carpool.constants.CarpoolConfig;


public class awsSES {	

	public static void sendEmail(String FROM, String TO,String BODY,String SUBJECT,String kind) {

		try{
			// Create a Properties object to contain connection configuration information.
			Properties props = System.getProperties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.port", CarpoolConfig.PORT); 

			// Set properties indicating that we want to use STARTTLS to encrypt the connection.
			// The SMTP session will begin on an unencrypted connection, and then the client
			// will issue a STARTTLS command to upgrade to an encrypted connection.
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");

			// Create a Session object to represent a mail session with the specified properties. 
			Session session = Session.getDefaultInstance(props);

			// Create a message with the specified information. 
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(FROM));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(TO));
			msg.setSubject(SUBJECT);
			msg.setContent(BODY,"text/"+kind);
			
			// Create a transport.        
			Transport transport = session.getTransport();

			// Send the message.
			try
			{
				System.out.println("Attempting to send an email through the Amazon SES SMTP interface...");

				// Connect to Amazon SES using the SMTP username and password you specified above.
				transport.connect(CarpoolConfig.HOST, CarpoolConfig.SMTP_USERNAME, CarpoolConfig.SMTP_PASSWORD);

				// Send the email.
				transport.sendMessage(msg, msg.getAllRecipients());
				System.out.println("Email sent!");
			}
			catch (Exception ex) {
				ex.printStackTrace();
				DebugLog.d(ex);
			}
			finally
			{
				// Close and terminate the connection.
				transport.close();        	
			}

		}catch (Exception ex) {
			ex.printStackTrace();
			DebugLog.d(ex);
		}
	}
	
}
