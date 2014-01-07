package carpool.asyncTask.relayTask;

import java.util.Date;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.BasicConfigurator;

import carpool.common.DebugLog;
import carpool.constants.CarpoolConfig;
import carpool.constants.Constants.EmailEvent;
import carpool.interfaces.PseudoAsyncTask;


public class SESRelayTask implements PseudoAsyncTask{

	private static final String smtpServer = "smtp.live.com";
	private static final String sender = "huaixuesheng@hotmail.com";
	private static final String password = "password11";
	
	private String receiver;
	private String subject;
	private String body;

	
	public SESRelayTask(String receiver, EmailEvent event, String payload){
		this.receiver = receiver;
		Entry<String, String> entry = CarpoolConfig.emailEventMap.get(event);
		if (entry == null){
			DebugLog.d("SESRelay Fatal: null entry from emailEventMap with given evt");
			throw new RuntimeException();
		}
		this.subject = entry.getKey();
		this.body = entry.getValue().replaceAll(CarpoolConfig.htmlTemplateTarget, payload);
	}

	public boolean execute(){
		return send();
	}


	/**
	 * @param receiver email address of the receiver
	 * @param subject
	 * @param body
	 * Send an email
	 **/
	public boolean send(){
		

		try{
			// Create a Properties object to contain connection configuration information.
			Properties props = System.getProperties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.port", CarpoolConfig.SMTP_PORT); 

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
			msg.setFrom(new InternetAddress(CarpoolConfig.SMTP_FROM));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(this.receiver));
			msg.setSubject(this.subject);
			msg.setContent(this.body,"text/plain");

			// Create a transport.        
			Transport transport = session.getTransport();

			// Send the message.
			try
			{
				System.out.println("Attempting to send an email through the Amazon SES SMTP interface...");

				// Connect to Amazon SES using the SMTP username and password you specified above.
				transport.connect(CarpoolConfig.SMTP_HOST, CarpoolConfig.SMTP_USERNAME, CarpoolConfig.SMTP_PASSWORD);

				// Send the email.
				transport.sendMessage(msg, msg.getAllRecipients());
				System.out.println("Email sent!");
			}
			catch (Exception e) {
				e.printStackTrace();
				DebugLog.d(e);
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
		
		return true;
	}

}
