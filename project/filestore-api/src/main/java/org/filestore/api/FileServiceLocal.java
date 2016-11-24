package org.filestore.api;

import javax.ejb.Local;
import java.io.InputStream;
import java.util.List;

@Local
public interface FileServiceLocal {
	
	public String getFileContent(String id) throws FileServiceException;
	
	public String postFile(String owner, List<String> receivers, String message, String name, InputStream stream) throws FileServiceException;

}
