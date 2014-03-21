package carpool.asyncTask.relayTask;

import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import carpool.common.DebugLog;
import carpool.configurations.EmailConfig;
import carpool.configurations.EnumConfig.EmailEvent;
import carpool.interfaces.PseudoAsyncTask;


public class SESRelayTask implements PseudoAsyncTask{
	
	private String receiver;
	private String subject;
	private String body;

	
	public SESRelayTask(String receiver, EmailEvent event, String payload){
		this.receiver = receiver;
		Entry<String, String> entry = EmailConfig.emailEventMap.get(event);
		if (entry == null){
			DebugLog.d("SESRelay Fatal: null entry from emailEventMap with given evt");
			throw new RuntimeException();
		}
		this.subject = entry.getKey();
		this.body = entry.getValue().replaceAll(EmailConfig.htmlTemplateURLTarget, payload);
	}

	public boolean execute(){
		return send();
	}


	public boolean send(){

		try{
			Properties props = System.getProperties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.port", EmailConfig.SMTP_PORT); 
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");

			// Create a Session object to represent a mail session with the specified properties. 
			Session session = Session.getDefaultInstance(props);

			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(EmailConfig.SMTP_FROM));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(this.receiver));
			msg.setSubject(this.subject);
			msg.setContent(this.body,"text/html");
			
			Transport transport = session.getTransport();

			try{
				transport.connect(EmailConfig.SMTP_HOST, EmailConfig.SMTP_USERNAME, EmailConfig.SMTP_PASSWORD);

				transport.sendMessage(msg, msg.getAllRecipients());
			} catch (Exception e) {
				e.printStackTrace();
				DebugLog.d(e);
			} finally{
				transport.close();        	
			}

		}catch (Exception e) {
			e.printStackTrace();
			DebugLog.d(e);
		}
		
		return true;
	}

}
