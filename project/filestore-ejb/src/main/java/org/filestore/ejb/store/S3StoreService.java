package org.filestore.ejb.store;

import org.filestore.api.FileData;

import java.io.InputStream;

public interface S3StoreService {
	
	public boolean exists(String key) throws BinaryStoreServiceException;
	
	public String put(FileData is) throws BinaryStoreServiceException;
	
	public InputStream get(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException;
	
	public void delete(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException;

}
