package org.filestore.web;

import org.filestore.api.*;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/files")
@RequestScoped
@Produces({ MediaType.APPLICATION_JSON })
public class FileItemsResource {
	
	private static final Logger LOGGER = Logger.getLogger(FileItemsResource.class.getName());
	
	@EJB
	private FileServiceS3 fileService;
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
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response postFile(MultipartFormDataInput input, @HeaderParam("Content-Length") String length) throws IOException, FileServiceException {
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
		final FileData fd = new FileData();
		if ( !form.containsKey("file")) {
			return Response.status(Response.Status.BAD_REQUEST).entity("parameter 'file' is mandatory").build();
		} else {
			InputPart part = form.get("file").get(0);
			String contentHeader = part.getHeaders().getFirst("Content-Disposition");
			name = contentHeader.substring(contentHeader.lastIndexOf("=")+1).replaceAll("\"", "");

			long size = Long.parseLong(length);

			final InputStream data  = part.getBody(InputStream.class, null);
			fd.setName(name);

			fd.setData(new DataHandler(new DataSource() {
				@Override
				public InputStream getInputStream() throws IOException {
					return data;
				}

				@Override
				public OutputStream getOutputStream() throws IOException {
					throw new IOException("Read only");
				}

				@Override
				public String getContentType() {
					return "*/*";
				}

				@Override
				public String getName() {
					return "[File DataHandler Name] InputStream";
				}
			}));
			fd.setSize(size);
		}


		String id = fileService.postFile(owner, receivers, message, name, fd);
		
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
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response getFileData(@PathParam("key") String key) throws FileServiceException, UnsupportedEncodingException {
		LOGGER.log(Level.INFO, "GET /files/" + key + "/download");
		FileItem item = fileService.getFile(key);
		String data = fileService.getFileData(key);
		LOGGER.log(Level.INFO, "Data location : " + data);
		LOGGER.log(Level.INFO, "File name : " + item.getName());
		URL url = null;
		FileOutputStream fis =null;
		try {
			url = new URL(data);
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			 fis = new FileOutputStream("./" + item.getName());
			byte[] buffer = new byte[1024];
			int count=0;
			while((count = bis.read(buffer,0,1024)) != -1)
			{
				fis.write(buffer, 0, count);
			}
			fis.close();
			bis.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		File file = new File("./" + item.getName());
		file.deleteOnExit();

		return Response.ok(file)
				.header("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode( file.getName(), "utf-8"))
				.build();

	}
	
	@DELETE
	@Path("/{key}")
	public void deleteFile(@PathParam("key") String key) throws FileServiceException {
		LOGGER.log(Level.INFO, "DELETE /files/" + key);
		fileServiceAdmin.deleteFile(key);
	}
	
}
