package org.filestore.ejb.store;

import java.io.InputStream;
import java.nio.file.Path;

public interface BinaryStoreService {

	public String genStreamId();
	
	public boolean exists(String key) throws BinaryStoreServiceException;
	
	public Path put(InputStream is) throws BinaryStoreServiceException;
	
	public InputStream get(Path file) throws BinaryStoreServiceException, BinaryStreamNotFoundException;
	
	public void delete(Path file) throws BinaryStoreServiceException, BinaryStreamNotFoundException;

}
