package org.filestore.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.filestore.api.FileServiceException;
import org.filestore.api.FileServiceMetrics;

@Path("/metrics")
@RequestScoped
@Produces({ MediaType.APPLICATION_JSON })
public class FileMetricsResource {
	
	private static final Logger LOGGER = Logger.getLogger(FileMetricsResource.class.getName());
	
	@EJB
	private FileServiceMetrics fileServiceMetrics;
	
	@GET
	public FileMetricsRepresentation getInfos() throws FileServiceException {
		LOGGER.log(Level.INFO, "GET /infos");
		long downloads = fileServiceMetrics.getTotalDownloads();
		long uploads = fileServiceMetrics.getTotalUploads();
		long uptime = fileServiceMetrics.getUptime();
		return new FileMetricsRepresentation(downloads, uploads, uptime);
	}

}
