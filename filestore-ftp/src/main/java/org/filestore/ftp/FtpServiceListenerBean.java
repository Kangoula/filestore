package org.filestore.ftp;



import org.apache.commons.logging.Log;
import org.apache.ftpserver.FtpServer;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.MessageListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.StubNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(name = "FtpServiceListenerMDB",
		activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/topic/Ftp"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class FtpServiceListenerBean implements MessageListener {

	private static final Logger LOGGER = Logger.getLogger(FtpServiceListenerBean.class.getName());
	
	//@Resource(name = "java:jboss/mail/Default")
	//private Session session;

	@Override
	public void onMessage(javax.jms.Message msg) {
		try {
			
			LOGGER.log(Level.INFO, "Message received in FTP");
			String key = msg.getStringProperty("key");
            Object content = msg.getObjectProperty("content");


            LOGGER.log(Level.INFO, "============ key : " + key);
            switch (key) {
                case "finalize":
                    final String id = (String) content;
                    LOGGER.log(Level.INFO, "============ id : " + id);
                    final String folder =  Configuration.getFolder() + "/" + id;
                    final String movingFolder = folder + ".moving";
                    Files.move(Paths.get(folder), Paths.get(movingFolder));
                    File f = new File(movingFolder).listFiles()[0];
                    Files.move(
                            Paths.get(f.getAbsolutePath().toString()),
                            Paths.get(folder));
                    Files.delete(Paths.get(movingFolder));
                break;
                default:
                    throw new Exception("Unknown command name :" + key);
            }

		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "unable to treat ftp message", e);
		}
	}

}