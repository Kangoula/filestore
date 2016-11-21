package org.filestore.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.filestore.api.FileService;
import org.filestore.api.FileServiceException;

public class FileStoreClient {

	private static final Logger LOGGER = Logger.getLogger(FileStoreClient.class.getName());

	@Resource(lookup = "java:comp/InAppClientContainer")
	private static boolean isInAppclient;
	@EJB
	private FileService service;
	private String host;

	public FileStoreClient(String host) {
		this.host = host;
	}

	public FileService getFileServiceRemote() throws NamingException {
		if (!Boolean.TRUE.equals(isInAppclient) && service == null) {
			LOGGER.log(Level.INFO, "getting FileSerive using remote-naming");
			final Properties env = new Properties();
			env.put(Context.INITIAL_CONTEXT_FACTORY,"org.jboss.naming.remote.client.InitialContextFactory");
			env.put(Context.PROVIDER_URL, "http-remoting://" + host + ":8080");
			InitialContext context = new InitialContext(env);
			service = (FileService) context.lookup("filestore-ear/filestore-ejb/fileservice!org.filestore.ejb.file.FileService");
			context.close();
		}
		return service;
	}
	
	public FileService getFileServiceEJB() throws NamingException {
		if (!Boolean.TRUE.equals(isInAppclient) && service == null) {
			LOGGER.log(Level.INFO, "getting FileSerive using ejb client");
			final Properties env = new Properties();
			env.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			env.put("jboss.naming.client.ejb.context",true);
			InitialContext context = new InitialContext(env);
			service = (FileService) context.lookup("ejb:filestore-ear/filestore-ejb/fileservice!org.filestore.ejb.file.FileService");
			context.close();
		}
		return service;
	}

	public void postFile(String owner, List<String> receivers, String message,
			String filename, Path file) throws FileServiceException,
			IOException, NamingException {
		if ( Boolean.TRUE.equals(isInAppclient) ) {
			LOGGER.log(Level.INFO, "We ARE in a client container");
		}
		byte[] content = Files.readAllBytes(file);
		//getFileServiceEJB().postFile(owner, receivers, message, filename, content);
		getFileServiceRemote().postFile(owner, receivers, message, filename, content);
	}

	public static void main(String args[]) throws FileServiceException,
			IOException, NamingException, ParseException {
		Options options = new Options();
		options.addOption("s", "sender", true, "sender email adresse");
		Option r = new Option("r", "receivers", true, "receivers email adresses (coma separated)");
		r.setRequired(true);
		r.setValueSeparator(',');
		options.addOption(r);
		options.addOption("m", "message", true, "message for receivers");
		Option p = new Option("p", "path", true, "file path to send");
		p.setRequired(true);
		options.addOption(p);
		options.addOption("h", "host", true, "server hostname (default to localhost)");
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse( options, args);
		
		String sender = cmd.getOptionValue("s", "root@localhost");
		String message = cmd.getOptionValue("m", "I have a file for you...");
		String host = cmd.getOptionValue("h", "localhost");
		String[] receivers = cmd.getOptionValues("r");
		Path path = Paths.get(cmd.getOptionValue("p"));
		
		
		FileStoreClient client = new FileStoreClient(host);
		client.postFile(sender, Arrays.asList(receivers), message,path.getFileName().toString(), path);
	}

}
