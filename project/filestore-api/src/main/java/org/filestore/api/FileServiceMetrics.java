package org.filestore.api;

import javax.ejb.Local;

import org.filestore.api.FileServiceException;

@Local
public interface FileServiceMetrics {
	
	public int getTotalUploads() throws FileServiceException;
	
	public int getTotalDownloads() throws FileServiceException;
	
	public int getUptime() throws FileServiceException;

}
