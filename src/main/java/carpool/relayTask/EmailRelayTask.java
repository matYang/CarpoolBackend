package carpool.relayTask;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import carpool.common.DebugLog;
import carpool.interfaces.PesudoRelayTask;


public class EmailRelayTask implements PesudoRelayTask{

	private static final String smtpServer = "smtp.live.com";
	private static final String sender = "huaixuesheng@hotmail.com";
	private static final String password = "password11";
	
	private String receiver;
	private String subject;
	private String body;
	
	public EmailRelayTask(String receiver,String subject, String body){
		this.receiver = receiver;
		this.subject = subject;
		this.body = body;
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
			try {
				msg.setFrom(new InternetAddress(sender));
				msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(receiver, false));
				msg.setSubject(subject);
				msg.setText(body);
				msg.setHeader("X-Mailer", "LOTONtechEmail");
				msg.setSentDate(new Date());
				Transport transport;
				transport = session.getTransport("smtp");
				transport.connect(smtpServer,sender, password);
				transport.sendMessage(msg, msg.getAllRecipients());
				transport.close();
			} catch (AddressException e) {
				e.printStackTrace();
				DebugLog.d("EmailRelayTask::send encoutered Exception:" + e.toString());
				return false;
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
				DebugLog.d("EmailRelayTask::send encoutered Exception:" + e.toString());
				return false;
			} catch (MessagingException e) {
				e.printStackTrace();
				DebugLog.d("EmailRelayTask::send encoutered Exception:" + e.toString());
				return false;
			} 
			
			return true;
	}

}
