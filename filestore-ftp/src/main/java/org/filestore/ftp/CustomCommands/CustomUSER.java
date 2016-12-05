package org.filestore.ftp.CustomCommands;

import org.filestore.api.FileService;
import org.filestore.api.FileServiceException;
import org.filestore.ftp.Configuration;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.ftpserver.command.impl.USER;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import javax.ejb.EJB;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by geoffrey on 01/12/2016.
 */
public class CustomUSER extends USER {


    private FileService service;

    public CustomUSER(FileService service) {
        this.service = service;
    }

    @Override
    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        String id = request.getArgument();

        try {
            if(!service.hasPendingFile(id)){
                session.write("\nYou must write your email address in place of the password and it should be a valid email.\n");
                return;
            }
            else {
                session.setAttribute("id",id);
                BaseUser user = new BaseUser();
                user.setName(id);
                String homeDir = Configuration.getFolder().getAbsolutePath()+"/"+id+"/";
                user.setHomeDirectory(homeDir);

                List<Authority> auth = new LinkedList<>();
                auth.add(new WritePermission());
                user.setAuthorities(auth);

                context.getUserManager().save(user);

                super.execute(session, context, request);
            }
        } catch (FileServiceException e) {
            e.printStackTrace();
        }
    }

    //private boolean isValid(String email){
    //    return EmailValidator.getInstance().isValid(email);
    //}
}
