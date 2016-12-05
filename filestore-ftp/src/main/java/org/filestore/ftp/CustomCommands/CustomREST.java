package org.filestore.ftp.CustomCommands;

import org.apache.ftpserver.command.impl.REST;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;

import java.io.IOException;

/**
 * Created by geoffrey on 01/12/2016.
 */
public class CustomREST extends REST {
    @Override
    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException {
        //File previous = Utils.findFileByClearName(request.getArgument());
        super.execute(session, context, request);
    }
}
