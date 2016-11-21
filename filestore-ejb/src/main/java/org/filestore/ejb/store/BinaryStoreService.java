package org.filestore.ejb.store;

import java.io.InputStream;

public interface BinaryStoreService {
	
	public boolean exists(String key) throws BinaryStoreServiceException;
	
	public String put(InputStream is) throws BinaryStoreServiceException;
	
	public InputStream get(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException;
	
	public void delete(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException;

}
