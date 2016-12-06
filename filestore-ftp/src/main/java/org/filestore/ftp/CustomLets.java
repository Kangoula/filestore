package org.filestore.ftp;

import org.apache.ftpserver.ftplet.*;
import org.filestore.api.FileService;
import org.filestore.api.FileServiceException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by geoffrey on 03/12/2016.
 */
public class CustomLets extends DefaultFtplet {

    private static final Logger LOGGER = Logger.getLogger(CustomLets.class.getName());


    private FileService service;

    public CustomLets(FileService service) {
        this.service = service;
    }

    @Override
    public FtpletResult onUploadEnd(FtpSession session, FtpRequest request)
            throws FtpException, IOException {


        String id = (String) session.getAttribute("id");


        String path = Configuration.getFolder()+ "/" + id + "/" + request.getArgument();
        Path filePath = Paths.get(path);

        LOGGER.log(Level.INFO, " File ended  <--------------------------------------------");
        LOGGER.log(Level.INFO, id);
        LOGGER.log(Level.INFO, filePath.toAbsolutePath().toString());

        new Thread(() -> {
            try {
                LOGGER.log(Level.INFO, "calling bean");
                service.completePendingFile(id, filePath.toAbsolutePath().toString());

                LOGGER.log(Level.INFO, "bean called");
            } catch (FileServiceException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }).start();

        LOGGER.log(Level.INFO, " Waiting a bit  <--------------------------------------------");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOGGER.log(Level.INFO, " Running default  <--------------------------------------------");

        return super.onDownloadEnd(session, request);
    }
}