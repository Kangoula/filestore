package org.filestore.ws.client;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.ws.soap.MTOMFeature;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class FileStoreWSClient {

	public static void main(String args[]) throws ParseException, FileServiceException_Exception, IOException {
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
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		String sender = cmd.getOptionValue("s", "root@localhost");
		String message = cmd.getOptionValue("m", "I have a file for you...");
		String[] receivers = cmd.getOptionValues("r");
		Path path = Paths.get(cmd.getOptionValue("p"));

		FileData content = new FileData();
		SerializableInputStream data = new SerializableInputStream(new FileDataSource(path.toFile()));
		content.setData(data);
		StringArray sareceivers = new StringArray();
		sareceivers.item = Arrays.asList(receivers);
		new FileServiceBeanService().getFileServiceBeanPort(new MTOMFeature(true, 100000)).postfile2(sender, sareceivers, message, path.getFileName().toString(), content);
	}

}
