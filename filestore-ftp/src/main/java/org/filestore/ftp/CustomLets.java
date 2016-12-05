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


        String path = Configuration.getFolder() + request.getArgument();
        Path filePath = Paths.get(path);

        LOGGER.log(Level.INFO, " File ended  <--------------------------------------------");
        LOGGER.log(Level.INFO, id);
        LOGGER.log(Level.INFO, filePath.toAbsolutePath().toString());

        try {
            service.completePendingFile(id, filePath);
        } catch (FileServiceException e) {
            e.printStackTrace();
        }


        return super.onDownloadEnd(session, request);
    }
}
