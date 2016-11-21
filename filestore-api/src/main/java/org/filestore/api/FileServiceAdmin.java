package org.filestore.api;

import java.util.List;

import javax.ejb.Local;

@Local
public interface FileServiceAdmin {
	
	public List<FileItem> listAllFiles() throws FileServiceException;
	
	public FileItem getNextStaleFile() throws FileServiceException;
	
	public void deleteFile(String id) throws FileServiceException;

}
