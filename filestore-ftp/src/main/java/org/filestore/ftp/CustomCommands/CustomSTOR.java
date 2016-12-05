package org.filestore.ftp.CustomCommands;

import org.apache.ftpserver.command.impl.STOR;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;

import java.io.IOException;

/**
 * Created by geoffrey on 01/12/2016.
 */
public class CustomSTOR extends STOR {
    @Override
    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        /*
        String filename = request.getArgument().replace("STOR ", "").trim();
        String name = Base64.getEncoder().encodeToString(filename.getBytes());
        String email = (String) session.getAttribute("email");
        String uuid = UUID.randomUUID().toString();

        String newName = uuid + "|" + email + "|" + name;

        DefaultFtpRequest req = new DefaultFtpRequest("STOR " + newName);
        */

        super.execute(session, context, request);
    }
}
