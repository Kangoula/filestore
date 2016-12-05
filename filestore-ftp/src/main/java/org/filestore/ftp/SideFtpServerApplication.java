package org.filestore.ftp;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.command.CommandFactoryFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.filestore.api.FileService;
import org.filestore.api.FileServiceException;
import org.filestore.ftp.CustomCommands.CustomUSER;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by geoffrey on 02/12/2016.
 */

public class SideFtpServerApplication implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(SideFtpServerApplication.class.getName());

    @EJB
    private FileService service;


    private FtpServer server;

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        LOGGER.log(Level.INFO, "destroy FTP Context <--------------------------------------------");
        this.server.stop();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        LOGGER.log(Level.INFO, "Init FTP CONTEXT <--------------------------------------------");

        FtpServerFactory serverFactory = new FtpServerFactory();
        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(true);

        serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());

        HashMap<String, Ftplet> lets = new HashMap<String, Ftplet>();
        lets.put("*", new CustomLets(service));

        serverFactory.setUserManager(new WildcardUserManagerFactory().createUserManager());


        CommandFactoryFactory commandFactoryFactory = new CommandFactoryFactory();
        commandFactoryFactory.addCommand("USER", new CustomUSER(service));

        serverFactory.setFtplets(lets);
        serverFactory.setCommandFactory(commandFactoryFactory.createCommandFactory());


        ListenerFactory factory = new ListenerFactory();
        factory.setPort(2221);
        serverFactory.addListener("default", factory.createListener());
        FtpServer server = serverFactory.createServer();
        try {
            server.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }

        this.server = server;

    }



}
