package org.filestore.api;

import java.io.InputStream;
import java.util.List;

import javax.ejb.Local;

@Local
public interface FileServiceLocal {
	
	public InputStream getFileContent(String id) throws FileServiceException;
	
	public String postFile(String owner, List<String> receivers, String message, String name, InputStream stream) throws FileServiceException;

}
