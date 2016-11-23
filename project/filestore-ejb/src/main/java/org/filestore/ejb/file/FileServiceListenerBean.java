package org.filestore.ejb.file;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.MessageListener;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.filestore.ejb.config.FileStoreConfig;

@MessageDriven(name = "FileServiceMailListenerMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/topic/Notification"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class FileServiceListenerBean implements MessageListener {

	private static final Logger LOGGER = Logger.getLogger(FileServiceListenerBean.class.getName());
	
	@Resource(name = "java:jboss/mail/Default")  
	private Session session;

	@Override
	public void onMessage(javax.jms.Message msg) {
		try {
			
			LOGGER.log(Level.INFO, "Message received, sending mails");
			String owner = msg.getStringProperty("owner");
			String id = msg.getStringProperty("id");
			String message = msg.getStringProperty("message");
			String receivers = msg.getStringProperty("receivers");
			this.notifyOwner(owner, id);
			for ( String receiver : receivers.split(",") ) {
				this.notifyReceiver(receiver, id, message);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "unable to send mail", e);
		}
	}
	
	private void notifyOwner(String owner, String id) {
		try {
			Message msg = new MimeMessage(session);  
			msg.setSubject("Your file has been received");  
			msg.setRecipient(RecipientType.TO,new InternetAddress(owner));  
			msg.setFrom(new InternetAddress("admin@filexchange.org","FileXChange"));  
			msg.setContent("Hi, this mail confirm the upload of your file. The file will be accessible at url : " 
					+ FileStoreConfig.getDownloadBaseUrl() + id + "/download", "text/html");
			Thread.sleep(5000);
			Transport.send(msg);
		} catch ( Exception e ) {
			LOGGER.log(Level.SEVERE, "unable to notify owner", e);
		}
	}
	
	private void notifyReceiver(String receiver, String id, String message) {
		try {
			Message msg = new MimeMessage(session);  
			msg.setSubject("Notification");
			msg.setRecipient(RecipientType.TO,new InternetAddress(receiver));
			msg.setFrom(new InternetAddress("admin@filexchange.org","FileXChange"));  
			msg.setContent("Hi, a file has been uploaded for you and is accessible at url : <br/><br/>" 
					+ FileStoreConfig.getDownloadBaseUrl() + id + "/download <br/><br/>" 
					+ "The sender lets you a message :<br/><br/>" + message, "text/html");
			Thread.sleep(5000);
			Transport.send(msg);  
		} catch ( Exception e ) {
			LOGGER.log(Level.SEVERE, "unable to notify owner", e);
		}
	}
}