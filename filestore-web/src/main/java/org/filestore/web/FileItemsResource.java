package org.filestore.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.filestore.api.*;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/files")
@RequestScoped
@Produces({ MediaType.APPLICATION_JSON })
public class FileItemsResource {
	
	private static final Logger LOGGER = Logger.getLogger(FileItemsResource.class.getName());
	
	@EJB
	private FileService fileService;
	@EJB
	private FileServiceLocal fileServiceLocal;
	@EJB
	private FileServiceAdmin fileServiceAdmin;

	
	public FileItemsResource () {
	}

	@GET
	public List<FileItem> listFiles() throws FileServiceException {
		LOGGER.log(Level.INFO, "GET /files");
		List<FileItem> files = fileServiceAdmin.listAllFiles();
		if ( files == null ) {
			return Collections.emptyList();
		} else {
			return files;
		}
	}

	@Path("/prepare")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response prepareFile(MultipartFormDataInput input) throws IOException, FileServiceException, URISyntaxException {

		Map<String, List<InputPart>> form = input.getFormDataMap();

		String owner = null;
		if ( !form.containsKey("owner") ) {
			return Response.status(Response.Status.BAD_REQUEST).entity("parameter 'owner' is mandatory").build();
		} else {
			owner = form.get("owner").get(0).getBodyAsString();
		}
		List<String> receivers = new ArrayList<String> ();
		if ( !form.containsKey("receivers") ) {
			return Response.status(Response.Status.BAD_REQUEST).entity("parameter 'receivers' is mandatory").build();
		} else {
			for ( InputPart part : form.get("receivers") ) {
				receivers.add(part.getBodyAsString());
			}
		}
		String message = null;
		if ( !form.containsKey("message") ) {
			message = "A files as been uploaded for you";
		} else {
			message = form.get("message").get(0).getBodyAsString();
		}

		String id = fileService.preparePostFile(owner,receivers,message);

		//return Response.ok(id).build();
		return Response.temporaryRedirect(new URI("../ftp_step_1.jsp?id="+id)).build();

	}


    @GET
    @Path("/complete/{id}")
    public Response complete(@PathParam("id") String id)
            throws IOException, FileServiceException,
            URISyntaxException {


        LOGGER.log(Level.WARNING, "<----------------------- Complete : …");



        LOGGER.log(Level.WARNING, "return : " + fileService.finalizeFtpUpload(id));
        LOGGER.log(Level.WARNING, "--- done ---");

        return Response.temporaryRedirect(new URI("../ftp.jsp")).build();
    }
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response postFile(MultipartFormDataInput input) throws IOException, FileServiceException {
		LOGGER.log(Level.INFO, "POST (multipart/form-data) /files");
		
		Map<String, List<InputPart>> form = input.getFormDataMap();
		
		String owner = null;
		if ( !form.containsKey("owner") ) {
			return Response.status(Response.Status.BAD_REQUEST).entity("parameter 'owner' is mandatory").build();
		} else {
			owner = form.get("owner").get(0).getBodyAsString();
		}
		List<String> receivers = new ArrayList<String> ();
		if ( !form.containsKey("receivers") ) {
			return Response.status(Response.Status.BAD_REQUEST).entity("parameter 'receivers' is mandatory").build();
		} else {
			for ( InputPart part : form.get("receivers") ) {
				receivers.add(part.getBodyAsString());
			}
		}
		String message = null;
		if ( !form.containsKey("message") ) {
			message = "A files as been uploaded for you";
		} else {
			message = form.get("message").get(0).getBodyAsString();
		}
		String name = null;
		InputStream data = null;
		if ( !form.containsKey("file")) {
			return Response.status(Response.Status.BAD_REQUEST).entity("parameter 'file' is mandatory").build();
		} else {
			InputPart part = form.get("file").get(0);
			String contentHeader = part.getHeaders().getFirst("Content-Disposition");
			name = contentHeader.substring(contentHeader.lastIndexOf("=")+1).replaceAll("\"", "");
			data = part.getBody(InputStream.class, null);
		}
		
		String id = fileServiceLocal.postFile(owner, receivers, message, name, data);
		
		return Response.ok(id).build();
	}
	
	@GET
	@Path("/{key}")
	public FileItem getFile(@PathParam("key") String key) throws FileServiceException {
		LOGGER.log(Level.INFO, "GET /files/" + key);
		return fileService.getFile(key);
	}
	
	@GET
	@Path("/{key}/download")
    @Produces("application/x-octet-stream")
	public Response getFileData(@PathParam("key") String key)
            throws FileServiceException, UnsupportedEncodingException {
		LOGGER.log(Level.INFO, "GET /files/" + key + "/download");
		FileItem item = fileService.getFile(key);
        LOGGER.log(Level.INFO, "×××××××××××××××× name = " + item.getName());
		InputStream data = fileServiceLocal.getFileContent(key);
		return Response.ok(data).header("Content-Disposition", "attachment; filename*=UTF-8''"
                + URLEncoder.encode(item.getName(), "utf-8")).build();
	}
	
	@DELETE
	@Path("/{key}")
	public void deleteFile(@PathParam("key") String key) throws FileServiceException {
		LOGGER.log(Level.INFO, "DELETE /files/" + key);
		fileServiceAdmin.deleteFile(key);
	}

    /*
    public FtpService getFtpService() {
        return ftpService;
    }
     */

    /*
    public void setFtpService(FtpService ftpService) {
        this.ftpService = ftpService;
    }
     */

}
